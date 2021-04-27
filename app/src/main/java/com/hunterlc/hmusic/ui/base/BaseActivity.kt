package com.hunterlc.hmusic.ui.base

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import coil.load
import coil.size.ViewSizeResolver
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.data.SongsInnerData
import com.hunterlc.hmusic.databinding.MiniPlayerBinding
import com.hunterlc.hmusic.manager.ActivityCollector
import com.hunterlc.hmusic.util.*

/***
 * 抽象基类 Activity
 */
abstract class BaseActivity : AppCompatActivity() {

    var miniPlayer: MiniPlayerBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
        //各种初始化
        initBinding()
        initView()
        initData()
        initListener()
        initObserver()
        initBroadcastReceiver()
        initMiniPlayer()
    }

    override fun onStart() {
        super.onStart()
        //检查是否开启了Dark模式
        if (DarkThemeUtil.isDarkTheme(this)) {
            setStatusBarIconColor(this, false)
        }
        initShowDialogListener()
    }

    /**
     * 获取播放状态 MiniPlayer 图标
     */
    private fun getPlayStateSourceId(playing: Boolean): Int {
        return if (playing) {
            R.drawable.ic_mini_player_pause
        } else {
            R.drawable.ic_mini_player_play
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        miniPlayer = null
        ActivityCollector.removeActivity(this)
    }

    protected open fun initBinding() {}

    protected open fun initView() {}

    protected open fun initData() {}

    protected open fun initListener() {}

    protected open fun initObserver() {}

    protected open fun initBroadcastReceiver() {}

    protected open fun initShowDialogListener() {}

    @SuppressLint("SetTextI18n")
    private fun initMiniPlayer() {
        miniPlayer?.let { mini ->

            mini.apply {
                root.setOnClickListener { MyApplication.activityManager.startPlayerActivity(this@BaseActivity) }
                ivStartOrPause.setOnClickListener { MyApplication.musicController.value?.changePlayState() }
            }
            MyApplication.musicController.observe(this, Observer{ nullableController ->
                nullableController?.apply {
                    getPlayingSongData().observe(this@BaseActivity, Observer{ songData ->
                        songData?.let {
                            mini.tvTitle.text = songData.name + " - " + songData.artists?.let { parseArtist(
                                it as ArrayList<SongsInnerData.ArtistsData>
                            ) }
                        }
                    })
                    isPlaying().observe(this@BaseActivity, Observer{
                        mini.ivStartOrPause.setImageResource(getPlayStateSourceId(it))
                    })
                    getPlayerCover().observe(this@BaseActivity, Observer{ bitmap ->
                        mini.ivCover.load(bitmap) {
                            size(ViewSizeResolver(mini.ivCover))
                            error(R.drawable.ic_song_cover)
                        }
                    })
                }
            })
        }

    }

}