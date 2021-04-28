package com.hunterlc.hmusic.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.*
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.broadcast.BecomingNoisyReceiver
import com.hunterlc.hmusic.data.SongsInnerData
import com.hunterlc.hmusic.service.base.BaseMediaService
import com.hunterlc.hmusic.ui.activity.MainActivity
import com.hunterlc.hmusic.ui.activity.PlayerActivity
import com.hunterlc.hmusic.util.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MusicService : BaseMediaService() {
    // 懒加载音乐控制器
    private val musicController by lazy { MusicController() }
    private var mode: Int = MyApplication.mmkv.decodeInt(ConfigUtil.PLAY_MODE, MODE_CIRCLE)
    private var notificationManager: NotificationManager? = null
    private var isAudioFocus = MyApplication.mmkv.decodeBool(ConfigUtil.ALLOW_AUDIO_FOCUS, true) // 是否开启音频焦点

    private var mediaSessionCallback: MediaSessionCompat.Callback? = null
    private var mediaSession: MediaSessionCompat? = null

    /* 音频控制器 */
    private var mediaController: MediaControllerCompat? = null

    private var speed = 1f // 默认播放速度，0f 表示暂停
    private var pitch = 1f // 默认音高
    private var pitchLevel = 0 // 音高等级
    private val pitchUnit = 0.05f // 音高单元，每次改变的音高单位

    private lateinit var audioManager: AudioManager
    private lateinit var audioAttributes: AudioAttributes
    private lateinit var audioFocusRequest: AudioFocusRequest

    override fun onCreate() {
        // 在 super.onCreate() 前
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager // 通知管理
        super.onCreate()
    }

    override fun initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "HMusic Notification"
            val descriptionText = "HMusic 音乐通知"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = descriptionText
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override fun initAudioFocus() {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener { focusChange ->
                    when (focusChange) {
                        // AudioManager.AUDIOFOCUS_GAIN -> musicBinder.play()
                        // AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> musicBinder.play()
                        // AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> musicBinder.play()
                        AudioManager.AUDIOFOCUS_LOSS -> {
                            // audioManager.abandonAudioFocusRequest(audioFocusRequest)
                            musicController.pause()
                        }
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> musicController.pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> musicController.pause()
                    }
                }.build()
            if (isAudioFocus) {
                audioManager.requestAudioFocus(audioFocusRequest)
            }
        }
    }

    override fun initMediaSession() {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

        var myNoisyAudioStreamReceiverTag = false
        val myNoisyAudioStreamReceiver = BecomingNoisyReceiver()
        // 媒体会话的回调，Service 控制通知这个 Callback 来控制 MediaPlayer
        mediaSessionCallback = object : MediaSessionCompat.Callback() {
            // 播放
            override fun onPlay() {
                // 注册广播
                if (!myNoisyAudioStreamReceiverTag) {
                    registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
                    myNoisyAudioStreamReceiverTag = true
                }

                mediaSession?.setPlaybackState(
                    PlaybackStateCompat.Builder()
                        .setState(
                            PlaybackStateCompat.STATE_PLAYING,
                            (MyApplication.musicController.value?.getProgress() ?: 0).toLong(),
                            1f
                        )
                        .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                        .build()
                )
            }

            // 暂停
            override fun onPause() {
                mediaSession?.setPlaybackState(
                    PlaybackStateCompat.Builder()
                        .setState(
                            PlaybackStateCompat.STATE_PAUSED,
                            (MyApplication.musicController.value?.getProgress() ?: 0).toLong(),
                            1f
                        )
                        .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                        .build()
                )
            }

            // 播放下一首
            override fun onSkipToNext() {
                musicController.playNext()
            }

            // 播放上一首
            override fun onSkipToPrevious() {
                musicController.playPrevious()
            }

            // 关闭
            override fun onStop() {
                // 注销广播
                if (myNoisyAudioStreamReceiverTag) {
                    unregisterReceiver(myNoisyAudioStreamReceiver)
                    myNoisyAudioStreamReceiverTag = false
                }

                //AudioPlayer.get().stopPlayer()
            }

            // 跳转
            override fun onSeekTo(pos: Long) {
                mediaPlayer?.seekTo(pos.toInt())
                if (musicController.isPlaying().value == true) {
                    onPlay()
                }
            }

            override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                if (mediaButtonEvent != null) {
                    val keyEvent = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT) as KeyEvent?
                    when (mediaButtonEvent.action) {
                        Intent.ACTION_MEDIA_BUTTON -> {
                            if (keyEvent != null) {
                                when (keyEvent.action) {
                                    // 按键按下
                                    KeyEvent.ACTION_DOWN -> {
                                        when (keyEvent.keyCode) {
                                            KeyEvent.KEYCODE_MEDIA_PLAY -> musicController.play()
                                            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> musicController.changePlayState()
                                            KeyEvent.KEYCODE_MEDIA_PAUSE -> musicController.pause()
                                            KeyEvent.KEYCODE_MEDIA_NEXT -> musicController.playNext()
                                            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> musicController.playPrevious()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return true
            }

        }
        // 初始化 MediaSession
        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            // 设置 Callback
            setCallback(mediaSessionCallback, Handler(Looper.getMainLooper()))
            // 把 MediaSession 置为 active，这样才能开始接收各种信息
            if (!isActive) {
                isActive = true
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getIntExtra("int_code", 0)) {
            CODE_PREVIOUS -> musicController.playPrevious()
            CODE_PLAY -> {
                LogUtil.e(musicController.isPlaying().value.toString(), TAG)
                if (musicController.isPlaying().value == true) {
                    LogUtil.e("按钮请求暂停音乐", TAG)
                    musicController.pause()
                } else {
                    LogUtil.e("按钮请求继续播放音乐", TAG)
                    musicController.play()
                }
            }
            CODE_NEXT -> musicController.playNext()
        }
        return START_NOT_STICKY // 非粘性服务
    }

    /**
     * 绑定
     */
    override fun onBind(p0: Intent?): IBinder {
        return musicController
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放 mediaSession
        mediaSession?.let {
            it.setCallback(null)
            it.release()
        }
        mediaPlayer?.release()
    }

    /**
     * inner class Music Controller
     */
    inner class MusicController : Binder(), MusicControllerInterface, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

        /* 是否是恢复 */
        private var recover = false

        /* 来自恢复的歌曲进度 */
        private var recoverProgress = 0

        private var songData = MutableLiveData<SongsInnerData?>()

        private val isSongPlaying = MutableLiveData<Boolean>().also {
            it.value = mediaPlayer?.isPlaying ?: false
        }

        private var isPrepared = false // 音乐是否准备完成

        /* Song cover bitmap*/
        private val coverBitmap = MutableLiveData<Bitmap?>()

        override fun setPlaylist(songListData: ArrayList<SongsInnerData>) {
            PlayQueue.setNormal(songListData)
            if (mode == MODE_RANDOM) {
                PlayQueue.random()
            }
        }

        override fun getPlaylist(): ArrayList<SongsInnerData>? = PlayQueue.currentQueue.value

        override fun playMusic(song: SongsInnerData) {

            isPrepared = false
            songData.value = song
            // 保存当前播放音乐
            MyApplication.mmkv.encode(ConfigUtil.SERVICE_CURRENT_SONG, song)
            // 如果 MediaPlayer 已经存在，释放
            if (mediaPlayer != null) {
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }
            // 初始化
            mediaPlayer = MediaPlayer().apply {
                //val it = Repository.getSongUrlById(song.id.toString()).value
                if (!InternetStateUtil.isWifi(MyApplication.context) && !MyApplication.mmkv.decodeBool(ConfigUtil.PLAY_ON_MOBILE, false)) {
                    Toast.makeText(this@MusicService,"移动网络下已禁止播放，请在设置中打开选项（注意流量哦）",Toast.LENGTH_SHORT).show()
                } else {
                    val it = "https://music.163.com/song/media/outer/url?id=${song.id}.mp3"
                    if ( it != null){
                        //val url = it.getOrNull()?.get(0)?.url
                        setDataSource(it)
                    }
                    setOnPreparedListener(this@MusicController) // 歌曲准备完成的监听
                    setOnCompletionListener(this@MusicController) // 歌曲完成后的回调
                    setOnErrorListener(this@MusicController)
                    prepareAsync()
                }


            }

        }

        private fun sendMusicBroadcast() {
            // Service 通知
            val intent = Intent("com.hunterlc.hmusic.MUSIC_BROADCAST")
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }

        override fun onPrepared(p0: MediaPlayer?) {
            isPrepared = true
            mediaController = mediaSession?.sessionToken?.let { it1 -> MediaControllerCompat(this@MusicService, it1) }
            if (recover) {
                this.setProgress(recoverProgress)
                recover = false
                LogUtil.e("recover","true")
            } else {
                this.play()
                LogUtil.e("recover","false")
            }
            sendMusicBroadcast()
            refreshNotification()
//            setPlaybackParams()  //引起软件重启后自动播放
            // 获取封面
            songData.value?.let {
                it.album.picUrl?.let { it1 ->
                    GlideUtil.load(it1){
                        coverBitmap.value = it
                    }
                }


            }
            // 添加到播放历史
            getPlayingSongData().value?.let {

            }
        }

        override fun changePlayState() {
            isSongPlaying.value?.let {
                if (it) {
                    mediaPlayer?.pause()
                    mediaSessionCallback?.onPause()
                } else {
                    mediaPlayer?.start()
                    mediaSessionCallback?.onPlay()
                }
                isSongPlaying.value = mediaPlayer?.isPlaying ?: false
            }
            sendMusicBroadcast()
            refreshNotification()
        }

        override fun play() {
            if (isPrepared) {
                mediaPlayer?.start()
                isSongPlaying.value = mediaPlayer?.isPlaying ?: false
                mediaSessionCallback?.onPlay()
                sendMusicBroadcast()
                refreshNotification()
            }
        }

        override fun pause() {
            if (isPrepared) {
                mediaPlayer?.pause()
                isSongPlaying.value = mediaPlayer?.isPlaying ?: false
                mediaSessionCallback?.onPause()
                sendMusicBroadcast()
                refreshNotification()
            }
        }

        override fun addToNextPlay(standardSongData: SongsInnerData) {
            if (standardSongData == songData.value) {
                return
            }
            if (PlayQueue.currentQueue.value?.contains(standardSongData) == true) {
                PlayQueue.currentQueue.value?.remove(standardSongData)
            }
            val currentPosition = PlayQueue.currentQueue.value?.indexOf(songData.value) ?: -1
            PlayQueue.currentQueue.value?.add(currentPosition + 1, standardSongData)
        }

        override fun setAudioFocus(status: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (status != isAudioFocus) {
                    if (status) {
                        audioManager.requestAudioFocus(audioFocusRequest)
                    } else {
                        audioManager.abandonAudioFocusRequest(audioFocusRequest)
                    }
                    isAudioFocus = status
                    MyApplication.mmkv.encode(ConfigUtil.ALLOW_AUDIO_FOCUS, isAudioFocus)
                }
            }
        }

        override fun stopMusicService() {
            stopSelf(-1)
        }

        override fun getPlayerCover(): MutableLiveData<Bitmap?> = coverBitmap

        override fun isPlaying(): MutableLiveData<Boolean> = isSongPlaying

        override fun setRecover(value: Boolean) {
            recover = value
        }

        override fun setRecoverProgress(value: Int) {
            recoverProgress = value
        }

        override fun getDuration(): Int {
            return if (isPrepared) {
                mediaPlayer?.duration ?: 0
            } else {
                0
            }
        }

        override fun getProgress(): Int {
            return if (isPrepared) {
                //保存歌曲进度
                MyApplication.musicController.value?.let { mediaPlayer?.currentPosition?.let { it1 ->
                    MyApplication.mmkv.encode(ConfigUtil.SERVICE_RECOVER_PROGRESS,
                        it1
                    )
                } }
                mediaPlayer?.currentPosition ?: 0
            } else {
                0
            }
        }

        override fun setProgress(newProgress: Int) {
            mediaPlayer?.seekTo(newProgress)
            mediaSessionCallback?.onPlay()
            refreshNotification()
        }

        override fun getPlayingSongData(): MutableLiveData<SongsInnerData?> = songData

        override fun changePlayMode() {
            when (mode) {
                MODE_CIRCLE -> mode = MODE_REPEAT_ONE
                MODE_REPEAT_ONE -> {
                    mode = MODE_RANDOM
                    PlayQueue.random()
                }
                MODE_RANDOM -> {
                    mode = MODE_CIRCLE
                    PlayQueue.normal()
                }
            }
            // 将播放模式存储
            MyApplication.mmkv.encode(ConfigUtil.PLAY_MODE, mode)
            sendMusicBroadcast()
        }

        override fun getPlayMode(): Int = mode

        override fun playPrevious() {
            when (val position = PlayQueue.currentQueue.value?.indexOf(songData.value) ?: -1) {
                -1 -> return
                0 -> {
                    PlayQueue.currentQueue.value?.get(
                        PlayQueue.currentQueue.value?.lastIndex ?: 0
                    )?.let {
                        playMusic(it)
                    }
                }
                else -> {
                    PlayQueue.currentQueue.value?.get(position - 1)?.let {
                        playMusic(it)
                    }
                }
            }
        }

        override fun playNext() {
            when (val position = PlayQueue.currentQueue.value?.indexOf(songData.value) ?: -1) {
                -1 -> return
                PlayQueue.currentQueue.value?.lastIndex -> {
                    PlayQueue.currentQueue.value?.get(0)?.let {
                        playMusic(it)
                    }
                }
                else -> {
                    PlayQueue.currentQueue.value?.get(position + 1)?.let {
                        playMusic(it)
                    }
                }
            }
        }

        override fun getNowPosition(): Int {
            return PlayQueue.currentQueue.value?.indexOf(songData.value) ?: -1
        }

        override fun getAudioSessionId(): Int {
            return mediaPlayer?.audioSessionId ?: 0
        }

        override fun sendBroadcast() {
            sendMusicBroadcast()
        }

        override fun setSpeed(speed: Float) {
            this@MusicService.speed = speed
            setPlaybackParams()
        }

        override fun getSpeed(): Float = speed

        override fun getPitchLevel(): Int = pitchLevel

        override fun increasePitchLevel() {
            pitchLevel++
            val value = pitchUnit * (pitchLevel + 1f / pitchUnit)
            if (value < 1.5f) {
                pitch = value
                setPlaybackParams()
            } else {
                decreasePitchLevel()
            }
        }

        override fun decreasePitchLevel() {
            pitchLevel--
            val value = pitchUnit * (pitchLevel + 1f / pitchUnit)
            if (value > 0.5f) {
                pitch = value
                setPlaybackParams()
            } else {
                increasePitchLevel()
            }
        }

        //倍速？
        private fun setPlaybackParams() {
            if (isPrepared) {
                mediaPlayer?.let {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val playbackParams = it.playbackParams
                            // playbackParams.speed = speed // 0 表示暂停
                            playbackParams.pitch = pitch
                            it.playbackParams = playbackParams
                        }
                    } catch (e: Exception) {

                    }
                }
            }
        }

        override fun onCompletion(p0: MediaPlayer?) {
            autoPlayNext()
        }

        private fun autoPlayNext() {
            if (mode == MODE_REPEAT_ONE) {
                setProgress(0)
                play()
                return
            }
            when (val position = PlayQueue.currentQueue.value?.indexOf(songData.value) ?: -1) {
                -1 -> return
                PlayQueue.currentQueue.value?.lastIndex -> {
                    PlayQueue.currentQueue.value?.get(0)?.let {
                        playMusic(it)
                    }
                }
                else -> {
                    PlayQueue.currentQueue.value?.get(position + 1)?.let {
                        playMusic(it)
                    }
                }
            }
        }

        override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
            if (MyApplication.mmkv.decodeBool(ConfigUtil.SKIP_ERROR_MUSIC, true)) {
                // 播放下一首
                Toast.makeText(this@MusicService,"播放错误 (${p1},${p2}) ，开始播放下一首",Toast.LENGTH_SHORT).show()
                playNext()
            } else {
                Toast.makeText(this@MusicService,"播放错误",Toast.LENGTH_SHORT).show()
            }
            return true
        }



    }

    private fun getPendingIntentActivity(): PendingIntent {
        val intentMain = Intent(this, MainActivity::class.java)
        val intentPlayer = Intent(this, PlayerActivity::class.java)
        val intents = arrayOf(intentMain, intentPlayer)
        return PendingIntent.getActivities(this, 1, intents, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingIntentPrevious(): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("int_code", CODE_PREVIOUS)
        return PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingIntentPlay(): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("int_code", CODE_PLAY)
        return PendingIntent.getService(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getPendingIntentNext(): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("int_code", CODE_NEXT)
        return PendingIntent.getService(this, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * 刷新通知
     */
    private fun refreshNotification() {
        val song = musicController.getPlayingSongData().value
        GlobalScope.launch {
            if (song != null) {
                song.album.picUrl?.let {
                    GlideUtil.load(it){
                        runOnMainThread {
                            showNotification(song,it)
                        }
                    }
                }

            }
        }

    }

    /**
     * 显示通知
     */
    private fun showNotification(song: SongsInnerData, bitmap: Bitmap?) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_launcher_foreground)
            .setLargeIcon(bitmap)
            .setContentTitle(song.name)
            .setContentText(song.artists.let { it1 -> parseArtist(it1 as ArrayList<SongsInnerData.ArtistsData>) })
            .setContentIntent(getPendingIntentActivity())
            .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", getPendingIntentPrevious())
            .addAction(getPlayIcon(), "play", getPendingIntentPlay())
            .addAction(R.drawable.ic_baseline_skip_next_24, "next", getPendingIntentNext())
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setOngoing(true)
            .build()

        mediaSession?.apply {
            setMetadata(
                MediaMetadataCompat.Builder().apply {
                    putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.name)
                    putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artists.parse())
                    putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        putLong(
                            MediaMetadata.METADATA_KEY_DURATION,
                            (MyApplication.musicController.value?.getDuration() ?: 0).toLong()
                        )
                    }
                }.build()
            )
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        (MyApplication.musicController.value?.getProgress() ?: 0).toLong(),
                        1f
                    )
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .build()
            )
            setCallback(mediaSessionCallback)
            isActive = true
        }
        if (musicController.isPlaying().value != true) {
            mediaSessionCallback?.onPause()
        }
        // 更新通知
        startForeground(START_FOREGROUND_ID, notification)
    }

    /**
     * 获取通知栏播放的图标
     */
    private fun getPlayIcon(): Int {
        return if (musicController.isPlaying().value == true) {
            R.drawable.ic_baseline_pause_24
        } else {
            R.drawable.ic_baseline_play_arrow_24
        }
    }
}