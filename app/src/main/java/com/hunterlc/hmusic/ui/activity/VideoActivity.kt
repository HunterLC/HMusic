package com.hunterlc.hmusic.ui.activity

import android.net.Uri
import android.net.Uri.parse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsetsController
import android.widget.MediaController
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.databinding.ActivityMainBinding
import com.hunterlc.hmusic.databinding.ActivityVideoBinding
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.ui.viewmodel.MyFragmentViewModel
import com.hunterlc.hmusic.ui.viewmodel.VideoViewModel
import com.hunterlc.hmusic.util.ConfigUtil
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : BaseActivity() {
    private lateinit var binding: ActivityVideoBinding
    private val videoViewModel by lazy { ViewModelProviders.of(this).get(VideoViewModel::class.java) }
    /***
     * 初始化绑定
     */
    override fun initBinding() {
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        super.initView()
        // 横屏隐藏状态栏
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE)
        }
        var id = intent.getLongExtra("mv",0L)
        videoViewModel.getMvUrl(id)

    }

    override fun initObserver() {
        super.initObserver()
        videoViewModel.videoPath.observe(this, Observer { result ->
            var path = result.getOrNull()
            //Creating MediaController
            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            //specify the location of media file
            val uri: Uri = parse(path)
            //Setting MediaController and URI, then starting the videoView
            videoView.setMediaController(mediaController)
            videoView.setVideoURI(uri)
            videoView.requestFocus()
            videoView.start()
        })
    }
}

