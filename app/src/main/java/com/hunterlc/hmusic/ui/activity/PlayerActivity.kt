package com.hunterlc.hmusic.ui.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.palette.graphics.Palette
import coil.Coil
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dirror.lyricviewx.OnPlayClickListener
import com.dirror.lyricviewx.OnSingleClickListener
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.data.LyricViewData
import com.hunterlc.hmusic.data.SongsInnerData
import com.hunterlc.hmusic.databinding.ActivityPlayerBinding
import com.hunterlc.hmusic.manager.VolumeManager
import com.hunterlc.hmusic.service.base.BaseMediaService
import com.hunterlc.hmusic.ui.activity.CommentActivity.Companion.EXTRA_LONG_ID
import com.hunterlc.hmusic.ui.activity.CommentActivity.Companion.EXTRA_STRING_NAME
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.ui.base.SlideBackActivity
import com.hunterlc.hmusic.ui.viewmodel.PlayerViewModel
import com.hunterlc.hmusic.util.*
import eightbitlab.com.blurview.RenderScriptBlur
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_player.*
import java.lang.Exception

class PlayerActivity : SlideBackActivity() {
    companion object {
        private const val MUSIC_BROADCAST_ACTION = "com.hunterlc.hmusic.MUSIC_BROADCAST"
        private const val DELAY_MILLIS = 500L

        // Handle ?????????????????????
        private const val MSG_PROGRESS = 0

        private const val BACKGROUND_SCALE_Y = 1.5F
        private const val BACKGROUND_SCALE_X = 2.5F

        // ??????????????????
        private const val BLUR_RADIUS = 1
        private const val BLUR_SAMPLING = 1

        // ??????????????????
        private const val DURATION_CD = 27_500L
        private const val ANIMATION_REPEAT_COUNTS = -1
        private const val ANIMATION_PROPERTY_NAME = "rotation"
    }

    private lateinit var binding: ActivityPlayerBinding

    // ?????????????????????
    private var isLandScape = false

    // ?????????????????????
    private lateinit var musicBroadcastReceiver: MusicBroadcastReceiver

    // ViewModel ?????????????????????
    private val playViewModel: PlayerViewModel by viewModels()

    // Looper + Handler
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == MSG_PROGRESS) {
                playViewModel.refreshProgress()
            }
        }
    }

    // CD ????????????
    private val objectAnimator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(binding.ivCover, ANIMATION_PROPERTY_NAME, 0f, 360f).apply {
            interpolator = LinearInterpolator()
            duration = DURATION_CD
            repeatCount = ANIMATION_REPEAT_COUNTS
            start()
        }
    }

    private val objectAnimator1: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(binding.ivBackgroundBlur1, ANIMATION_PROPERTY_NAME, 0f, 360f).apply {
            interpolator = LinearInterpolator()
            duration = 70000L
            repeatCount = ANIMATION_REPEAT_COUNTS
            start()
        }
    }

    private val objectAnimator2: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(binding.ivBackgroundBlur2, ANIMATION_PROPERTY_NAME, 0f, 360f).apply {
            interpolator = LinearInterpolator()
            duration = 80000L
            repeatCount = ANIMATION_REPEAT_COUNTS
            start()
        }
    }

    private val objectAnimator3: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(binding.ivBackgroundBlur3, ANIMATION_PROPERTY_NAME, 0f, 360f).apply {
            interpolator = LinearInterpolator()
            duration = 75000L
            repeatCount = ANIMATION_REPEAT_COUNTS
            start()
        }
    }


    override fun initBroadcastReceiver() {
        // Intent ????????????????????? "com.dirror.foyou.MUSIC_BROADCAST" ????????????
        var intentFilter = IntentFilter()
        intentFilter.addAction(MUSIC_BROADCAST_ACTION)
        musicBroadcastReceiver = MusicBroadcastReceiver()
        // ???????????????
        registerReceiver(musicBroadcastReceiver, intentFilter)
    }

    override fun initBinding() {
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        // ?????? SlideBackLayout
        bindSlide(this, binding.clBase)
        // ????????????
        val configuration = this.resources.configuration //???????????????????????????
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandScape = true
            // ?????????????????????
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && MyApplication.mmkv.decodeBool(ConfigUtil.LANDSCAPE_HIDE_BARS, true)) {
                window.insetsController?.hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE)
            }
        } else {
            // ?????????????????????
            val navigationHeight = if (MyApplication.mmkv.decodeBool(ConfigUtil.PARSE_NAVIGATION, true)) {
                getNavigationBarHeight(this@PlayerActivity)
            } else {
                0
            }
            (binding.clBottom.layoutParams as ConstraintLayout.LayoutParams).apply {
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                bottomMargin = navigationHeight
            }
        }
        // ?????????????????????
        binding.titleBar?.let {
            (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                topMargin = getStatusBarHeight(window, this@PlayerActivity)
            }
        }

        //???????????????????????????????????????????????????????????????
        binding.llBase?.let {
            (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                if (isLandScape){
                    marginStart = (getStatusBarHeight(window, this@PlayerActivity) / MyApplication.context.resources.displayMetrics.density + 0.5f).toInt()
                }

            }
        }

        val radius = 25f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background
        binding.blurViewLyric.setupWith(decorView.findViewById(R.id.blurViewCover))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            //.setOverlayColor(ContextCompat.getColor(this, R.color.dso_color_lyrics_back))



        binding.apply {
            // ???????????????
            ttvDuration.setAlignRight()
            // ??????????????????
            ivBackground.scaleY = BACKGROUND_SCALE_Y
            ivBackground.scaleX = BACKGROUND_SCALE_X
            // ????????????????????????
            ivTranslation.visibility = View.GONE
            // ?????????????????????
            seekBarVolume.max = VolumeManager.maxVolume
            seekBarVolume.progress = VolumeManager.getCurrentVolume()

            lyricView.setLabel("???????????????")
            lyricView.setTimelineTextColor(ContextCompat.getColor(this@PlayerActivity, R.color.colorTextForeground))
        }
        if (isLandScape) {
            slideBackEnabled = false
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        binding.apply {
            // ????????????
            ivBack.setOnClickListener { finish() }
            // ?????? / ??????????????????
            ivPlay.setOnClickListener { playViewModel.changePlayState() }
            // ?????????
            ivLast.setOnClickListener { playViewModel.playLast() }
            // ?????????
            ivNext.setOnClickListener { playViewModel.playNext() }
            // ??????????????????
            ivMode.setOnClickListener { playViewModel.changePlayMode() }
            // ??????
            ivComment.setOnClickListener {
                var intent = Intent(MyApplication.context,CommentActivity::class.java)
                intent.putExtra(EXTRA_LONG_ID,MyApplication.musicController.value?.getPlayingSongData()?.value?.id)
                intent.putExtra(EXTRA_STRING_NAME,MyApplication.musicController.value?.getPlayingSongData()?.value?.name)
                startActivity(intent)
            }
            // ????????????
            ivLike.setOnClickListener {

            }
            // CD
            clCd.setOnClickListener {
                if (binding.clLyric.visibility == View.INVISIBLE && !isLandScape) {
                    AnimationUtil.fadeOut(binding.clCd, true)
                    AnimationUtil.fadeOut(binding.clMenu, true)
                    binding.clLyric.visibility = View.VISIBLE
                    binding.tvSongName?.visibility = View.VISIBLE
                    binding.tvArtistName?.visibility = View.VISIBLE
                    slideBackEnabled = false
                }
            }
            // lyricView

            lyricView.setDraggable(true, object : OnPlayClickListener {
                override fun onPlayClick(time: Long): Boolean {
                    playViewModel.setProgress(time.toInt())
                    return true
                }
            })
            lyricView.setOnSingerClickListener(object : OnSingleClickListener {
                override fun onClick() {
                    if (!isLandScape) {
                        AnimationUtil.fadeIn(binding.clCd)
                        AnimationUtil.fadeIn(binding.clMenu)
                        binding.clLyric.visibility = View.INVISIBLE
                        binding.tvSongName?.visibility = View.INVISIBLE
                        binding.tvArtistName?.visibility = View.INVISIBLE
                        slideBackEnabled = true

                    }
                }
            })
            edgeTransparentView.setOnClickListener {
                if (!isLandScape) {
                    AnimationUtil.fadeIn(binding.clCd)
                    AnimationUtil.fadeIn(binding.clMenu)
                    binding.clLyric.visibility = View.INVISIBLE
                    binding.tvSongName?.visibility = View.INVISIBLE
                    binding.tvArtistName?.visibility = View.INVISIBLE
                    slideBackEnabled = true
                }
            }
            // ?????????
            tvArtist.setOnClickListener {

            }
            // ????????????
            ivTranslation.setOnClickListener {
                playViewModel.setLyricTranslation(playViewModel.lyricTranslation.value != true)
            }
            // ??????????????????????????????
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // ?????????????????????
                    if (fromUser) {
                        playViewModel.setProgress(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })
            // ??????????????????
            seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // ?????????????????????
                    if (fromUser) {
                        playViewModel.currentVolume.value = progress
                        VolumeManager.setStreamVolume(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })
        }
    }

    override fun initShowDialogListener() {

    }



    override fun initObserver() {
        playViewModel.apply {
            // ?????????????????????
            playMode.observe(this@PlayerActivity, Observer {
                when (it) {
                    BaseMediaService.MODE_CIRCLE -> binding.ivMode.setImageResource(R.drawable.ic_bq_player_mode_circle)
                    BaseMediaService.MODE_REPEAT_ONE -> binding.ivMode.setImageResource(R.drawable.ic_bq_player_mode_repeat_one)
                    BaseMediaService.MODE_RANDOM -> binding.ivMode.setImageResource(R.drawable.ic_bq_player_mode_random)
                }
            })
            // ?????????????????????
            MyApplication.musicController.value?.getPlayingSongData()?.observe(this@PlayerActivity,
                Observer {
//                    objectAnimator.cancel()
//                    objectAnimator.start()
                    it?.let {
                        binding.tvName.text = it.name
                        binding.tvSongName?.text = it.name
                        binding.tvSongName?.setSpeed(-1)
                        binding.tvArtist.text = it.artists.let { artists ->
                            parseArtist(artists as ArrayList<SongsInnerData.ArtistsData>)
                        }
                        binding.tvArtistName?.text = binding.tvArtist.text
                        // ????????????
                        playViewModel.getLyricById(it.id)

                    }
                })
            //?????????????????????
            lyricLiveData.observe(this@PlayerActivity, Observer { result ->
                // ????????????
                MyApplication.musicController.value?.getPlayingSongData()?.value?.let {
                    val lyric = result.getOrNull()
                    if (lyric != null ) {
                        //_lyricViewData.value = LyricViewData(lyric.lrc?.lyric?:"", lyric.tlyric?.lyric?:"")
                        if (lyricTranslation.value == true) {
                            hasLyric.value = LyricViewData(lyric.lrc?.lyric?:"", lyric.tlyric?.lyric?:"")
                            noLyric.value = LyricViewData(
                                LyricViewData(
                                    lyric.lrc?.lyric?:"",
                                    lyric.tlyric?.lyric?:""
                                ).lyric, "true")
                            lyricViewData.value = hasLyric.value

                        } else {
                            hasLyric.value = LyricViewData(lyric.lrc?.lyric?:"", lyric.tlyric?.lyric?:"")
                            noLyric.value = LyricViewData(
                                LyricViewData(
                                    lyric.lrc?.lyric?:"",
                                    lyric.tlyric?.lyric?:""
                                ).lyric, "true")
                            lyricViewData.value = noLyric.value
                        }
                    }

                }
            })
            // ????????????
            MyApplication.musicController.value?.getPlayerCover()?.observe(this@PlayerActivity,
                Observer { bitmap ->
                    // ?????? CD ??????
                    binding.ivCover.load(bitmap) {
                        placeholder(binding.ivCover.drawable)
                        transformations(coil.transform.RoundedCornersTransformation())
                        size(ViewSizeResolver(binding.ivCover))
                    }

//                    binding.ivBackground.load(bitmap) {
//                        placeholder(binding.ivBackground.drawable)
//                        transformations(coil.transform.RoundedCornersTransformation())
//                        size(ViewSizeResolver(binding.ivBackground))
//                    }
//                    binding.ivBackgroundBlur3.load(bitmap) {
//                        placeholder(binding.ivBackgroundBlur3.drawable)
//                        transformations(coil.transform.RoundedCornersTransformation())
//                        size(ViewSizeResolver(binding.ivBackgroundBlur3))
//                    }
//                    binding.ivBackgroundBlur2.load(bitmap) {
//                        placeholder(binding.ivBackgroundBlur2.drawable)
//                        transformations(coil.transform.RoundedCornersTransformation())
//                        size(ViewSizeResolver(binding.ivBackgroundBlur2))
//                    }
//                    binding.ivBackgroundBlur1.load(bitmap) {
//                        placeholder(binding.ivBackgroundBlur1.drawable)
//                        transformations(coil.transform.RoundedCornersTransformation())
//                        size(ViewSizeResolver(binding.ivBackgroundBlur1))
//                    }
                    // ?????? ?????? ??????
                    Glide.with(MyApplication.context)
                        .load(bitmap)
                        .placeholder(binding.ivBackground.drawable)
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(BLUR_RADIUS, BLUR_SAMPLING)))
                        .into(binding.ivBackground)

                    Glide.with(MyApplication.context)
                        .load(bitmap)
                        .placeholder(binding.ivBackgroundBlur3.drawable)
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(BLUR_RADIUS, BLUR_SAMPLING)))
                        .into(binding.ivBackgroundBlur3)

                    Glide.with(MyApplication.context)
                        .load(bitmap)
                        .placeholder(binding.ivBackgroundBlur1.drawable)
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(BLUR_RADIUS, BLUR_SAMPLING)))
                        .into(binding.ivBackgroundBlur1)

                    Glide.with(MyApplication.context)
                        .load(bitmap)
                        .placeholder(binding.ivBackgroundBlur2.drawable)
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(BLUR_RADIUS, BLUR_SAMPLING)))
                        .into(binding.ivBackgroundBlur2)

                    // ????????????
                    bitmap?.let {
                        Palette.from(bitmap)
                            .clearFilters()
                            .generate { palette ->
                                palette?.let {
                                    val muteColor = if (DarkThemeUtil.isDarkTheme(this@PlayerActivity)) {
                                        palette.getLightMutedColor(PlayerViewModel.DEFAULT_COLOR)
                                    } else {
                                        palette.getDarkMutedColor(PlayerViewModel.DEFAULT_COLOR)
                                    }
                                    val vibrantColor = palette.getVibrantColor(PlayerViewModel.DEFAULT_COLOR)
                                    playViewModel.color.value = muteColor.colorMix(vibrantColor)
                                }
                            }
                    }
                })
            // ?????????????????????
            MyApplication.musicController.observe(this@PlayerActivity, Observer { nullableController ->
                    nullableController?.let { controller ->
                        controller.isPlaying().observe(this@PlayerActivity, Observer {
                            objectAnimator1.resume()
                            objectAnimator2.resume()
                            objectAnimator3.resume()
                            if (it) {
                                binding.ivPlay.setImageResource(R.drawable.ic_pause_btn)
                                handler.sendEmptyMessageDelayed(MSG_PROGRESS, DELAY_MILLIS)
                                //??????????????????
                                if(!MyApplication.mmkv.decodeBool(ConfigUtil.SQUARE_CD_COVER,false)){
                                    startRotateAlways()
                                    binding.diffuseView.start()
                                }
                            } else {
                                binding.ivPlay.setImageResource(R.drawable.ic_play_btn)
                                handler.removeMessages(MSG_PROGRESS)
                                //??????????????????
                                if(!MyApplication.mmkv.decodeBool(ConfigUtil.SQUARE_CD_COVER,false)){
                                    pauseRotateAlways()
                                    binding.diffuseView.stop()
                                }

                            }
                        })
                    }
                })

            // ??????????????????
            duration.observe(this@PlayerActivity, Observer {
                binding.seekBar.max = it
                binding.ttvDuration.setText(it)
            })
            // ???????????????
            progress.observe(this@PlayerActivity, Observer {
                binding.seekBar.progress = it
                binding.ttvProgress.setText(it)
                handler.sendEmptyMessageDelayed(MSG_PROGRESS, DELAY_MILLIS)
                // ????????????????????????
                binding.lyricView.updateTime(it.toLong())

            })
            // ????????????
            lyricTranslation.observe(this@PlayerActivity, Observer {
                if (it == true) {
                    binding.ivTranslation.alpha = 1F
                } else {
                    binding.ivTranslation.alpha = 0.3F
                }
            })
            // ????????????
            lyricViewData.observe(this@PlayerActivity, Observer {result ->
                // ??????????????????
                binding.ivTranslation.visibility = if (result.secondLyric.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                if (playViewModel.lyricTranslation.value == true) {
                    LogUtil.e("?????????????????????","?????????????????????4")
                    binding.lyricView.loadLyric(result.lyric, result.secondLyric)
                } else {
                    binding.lyricView.loadLyric(result.lyric)
                }
//                binding.lyricView.setNormalColor(R.color.lrc_current_text_color)
//                binding.lyricView.setCurrentColor(R.color.lrc_normal_text_color)
            })

            // ????????????
            currentVolume.observe(this@PlayerActivity, Observer {
                binding.seekBarVolume.progress = it
            })
            // ????????????
            color.observe(this@PlayerActivity, Observer {
                binding.apply {
                    ivPlay.setColorFilter(it)
                    ivLast.setColorFilter(it)
                    ivMode.setColorFilter(it)
                    ivList.setColorFilter(it)
                    ivNext.setColorFilter(it)
                    tvName.setTextColor(it)
                    tvArtist.setTextColor(it)
                    tvArtistName?.setTextColor(it)
                    tvSongName?.setTextColor(it)
                    ivBack.setColorFilter(it)
                    ivShare?.setColorFilter(it)
                    diffuseView.setColor(it)
                    lyricView.apply {
                        setCurrentColor(it)
                        setTimeTextColor(it)
                        setTimelineColor(it.colorAlpha(0.25f))
                        //LogUtil.e("??????",Color.argb(Color.alpha(it)*0.9F.toInt(),Color.red(it),Color.green(it),Color.blue(it)).toString())
                        setTimelineTextColor(it)

                        setNormalColor(it.colorAlpha(0.5f))
                    }
                }
            })
            //?????????????????????
            square_cd.observe(this@PlayerActivity, Observer { isSquare ->
                if (isSquare){
                    LogUtil.e("square_cd","????????????")
                    //??????cvCd???????????????????????????????????????
                    cvCd.radius = (10.dp()).toFloat()
                    //??????????????????
                    objectAnimator.cancel()
                    objectAnimator.end()
                    pauseRotateAlways()
                    //???????????????????????????
                    diffuseView.stop()
                    diffuseView.visibility = View.INVISIBLE

                } else{
                    //??????cvCd????????????????????????????????????
                    LogUtil.e("square_cd","????????????")
                    cvCd.radius = (120.dp()).toFloat()
                    //??????????????????
//                    objectAnimator.cancel()
//                    objectAnimator.start()
//                    startRotateAlways()
//                    //???????????????????????????
//                    diffuseView.start()
//                    diffuseView.visibility = View.VISIBLE
                }

            })
            dynamic_background.observe(this@PlayerActivity, Observer { isTurnOn ->
                if (isTurnOn){
                    //????????????
                    objectAnimator1.start()
                    objectAnimator2.start()
                    objectAnimator3.start()
                    //??????View
                    ivBackgroundBlur1.visibility = View.VISIBLE
                    ivBackgroundBlur2.visibility = View.VISIBLE
                    ivBackgroundBlur3.visibility = View.VISIBLE

                } else {
                    //????????????
                    objectAnimator1.end()
                    objectAnimator2.end()
                    objectAnimator3.end()
                    //??????View
                    ivBackgroundBlur1.visibility = View.INVISIBLE
                    ivBackgroundBlur2.visibility = View.INVISIBLE
                    ivBackgroundBlur3.visibility = View.INVISIBLE
                }

            })
        }
    }

    /**
     * ??????????????????
     */
    private fun startRotateAlways() {
        objectAnimator.resume()
    }

    /**
     * ??????????????????
     */
    private fun pauseRotateAlways() {
        playViewModel.rotation = binding.ivCover.rotation
        playViewModel.rotationBackground = binding.ivBackground.rotation
        objectAnimator.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // ??????????????????????????????
        try {
            unregisterReceiver(musicBroadcastReceiver)
        }catch (e: Exception){

        }

        // ?????? Handler ??????????????????????????????????????????
        handler.removeCallbacksAndMessages(null)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_slide_exit_bottom
        )
    }

    /**
     * ????????? - ?????????????????????
     */
    inner class MusicBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // ViewModel ????????????
            playViewModel.refresh()

        }
    }

    /**
     * ????????????????????????
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                playViewModel.addVolume()
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                playViewModel.reduceVolume()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }


}