package com.hunterlc.hmusic.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.ViewSizeResolver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.adapter.SongDataAdapter
import com.hunterlc.hmusic.databinding.ActivityPlaylistBinding
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.ui.viewmodel.MainViewModel
import com.hunterlc.hmusic.ui.viewmodel.PlaylistViewModel
import com.hunterlc.hmusic.util.ConfigUtil
import com.hunterlc.hmusic.util.LogUtil
import com.hunterlc.hmusic.util.ScreenUtil.getNavigationBarHeight
import com.hunterlc.hmusic.util.StatusbarUtil.getStatusBarHeight
import com.hunterlc.hmusic.util.copyToClipboard
import com.hunterlc.hmusic.util.runOnMainThread
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_playlist.*

/**
 * 新版 Playlist
 */
class PlaylistActivity: BaseActivity() {

    companion object {
        const val EXTRA_PLAYLIST_SOURCE = "int_playlist_source"
        const val EXTRA_LONG_PLAYLIST_ID = "int_playlist_id"
        const val EXTRA_INT_TAG = "int_tag"
    }

    private lateinit var binding: ActivityPlaylistBinding

    private lateinit var updatePlaylistReceiver: UpdatePlaylistReceiver

    private var detailPlaylistAdapter = SongDataAdapter(this)

    private val playlistViewModel: PlaylistViewModel by lazy { ViewModelProvider(this).get(
        PlaylistViewModel::class.java)}

    override fun initBinding() {
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        // 屏幕适配
        (binding.titleBar.layoutParams as ConstraintLayout.LayoutParams).apply {
            topMargin = getStatusBarHeight(window, this@PlaylistActivity)
        }
        val navigationHeight = if (MyApplication.mmkv.decodeBool(ConfigUtil.PARSE_NAVIGATION, true)) {
            getNavigationBarHeight(this)
        } else {
            0
        }
        (binding.miniPlayer.root.layoutParams as ConstraintLayout.LayoutParams).apply {
            bottomMargin = navigationHeight
        }
        // 色彩
        binding.ivPlayAll.setColorFilter(ContextCompat.getColor(this, R.color.colorAppThemeColor))
        // 获取歌单 id
        playlistViewModel.id.value = intent.getLongExtra(EXTRA_LONG_PLAYLIST_ID, 0L)

        binding.lottieLoading.repeatCount = -1
        binding.lottieLoading.playAnimation()


        binding.rvPlaylist.layoutManager =  LinearLayoutManager(this@PlaylistActivity)

        var rvPlaylistScrollY = 0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            binding.rvPlaylist.setOnScrollChangeListener { _, _, _, _, oldScrollY ->
                rvPlaylistScrollY += oldScrollY
                if (rvPlaylistScrollY < 0) {
                    if (binding.titleBar.text == getString(R.string.playlist)) {
                        binding.titleBar.setTitleBarText(binding.tvName.text.toString())
                    }
                } else {
                    binding.titleBar.setTitleBarText(getString(R.string.playlist))
                }
            }
        }
    }

    override fun initBroadcastReceiver() {

    }

    override fun initListener() {
        binding.apply {
            // 全部播放 播放第一首歌
            clNav.setOnClickListener {

            }
            ivShare.setOnClickListener {
                Toast.makeText(this@PlaylistActivity,"歌单 ID 已经成功复制到剪贴板",Toast.LENGTH_SHORT).show()
                copyToClipboard(this@PlaylistActivity, playlistViewModel.id.toString())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initObserver() {
        playlistViewModel.apply {
            playlist.observe(this@PlaylistActivity, Observer {result ->
                val musicData = result.getOrNull()

                detailPlaylistAdapter = SongDataAdapter(this@PlaylistActivity).apply {
                    if (musicData != null) {
                        submitList(musicData.songs)
                        binding.tvPlayAll.text = getString(R.string.play_all, musicData.songs?.size)
                    }
                }
                binding.rvPlaylist.adapter = detailPlaylistAdapter
                binding.clLoading.visibility = View.GONE
                binding.lottieLoading.pauseAnimation()
            })
            //把所有的id拼接起来

            //查询歌曲信息
            playlistMusicIdLiveData.observe(this@PlaylistActivity, Observer { result ->
                val playlistDetail = result.getOrNull()
                val musicIds = playlistDetail?.trackIds
                var ids = "" //歌单内所包含歌曲的id
                if (musicIds != null){
                    for (music in musicIds){
                        ids = ids + music.id + ","
                    }
                    ids = ids.take(ids.length - 1) //去除最后一个逗号
                    LogUtil.e("playlist",ids)
                }
                //设置头图信息等
                if (playlistDetail != null) {
                    with(binding){
                        ivCover.load(playlistDetail.coverImgUrl){
                            allowHardware(false)
                            crossfade(true)
                            size(ViewSizeResolver(ivCover))
                        }
                        ivBackground.load(playlistDetail.coverImgUrl){
                            allowHardware(false)
                            crossfade(true)
                            size(ViewSizeResolver(ivCover))
                        }
                        tvName.text = playlistDetail.name
                        tvDescription.text = playlistDetail.description
                    }

                }
                //根据歌曲id查歌曲信息
                playlistViewModel.getMusicDetail(ids)
            })
        }
    }

    inner class UpdatePlaylistReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //playlistViewModel.updatePlaylist()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑
        //unregisterReceiver(updatePlaylistReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //全屏且让背景沉浸到状态栏
//        val decorView = window.decorView
//        decorView.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.statusBarColor = Color.TRANSPARENT
    }


}