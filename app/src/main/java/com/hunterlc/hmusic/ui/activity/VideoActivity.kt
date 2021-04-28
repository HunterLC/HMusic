package com.hunterlc.hmusic.ui.activity

import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.net.Uri.parse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
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
import com.hunterlc.hmusic.util.LogUtil
import com.hunterlc.hmusic.widget.SimpleVideoView
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : BaseActivity() {
    private lateinit var binding: ActivityVideoBinding
    private val videoViewModel by lazy { ViewModelProviders.of(this).get(VideoViewModel::class.java) }
    private var id: Long = 0L  //mv的id
    private lateinit var name: String  //mv的名字
    // 是否是横屏状态
    private var isLandScape = false

    /***
     * 初始化绑定
     */
    override fun initBinding() {
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
//        if (!binding.video.isFullScreen) { // 横屏
//            isLandScape = true
//            binding.titleBar.visibility = View.GONE
//            // 横屏隐藏状态栏
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//                window.insetsController?.hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE)
//            }
//        } else { // 竖屏
//            isLandScape = false
//            binding.titleBar.visibility = View.VISIBLE
//            binding.titleBar.setTitleBarText(name)
//            binding.titleBar.setTextColor(-1)  //白色
//            binding.ivShare.setColorFilter(-1)
//        }
        id = intent.getLongExtra("mv",0L)
        name = intent.getStringExtra("name").toString()
        LogUtil.e("mv的名字",name)
        videoViewModel.getMvUrl(id)
    }

    override fun initObserver() {
        super.initObserver()
        videoViewModel.videoPath.observe(this, Observer { result ->
            var path = result.getOrNull()
            val uri: Uri = parse(path)
            binding.video.videoUri = uri
            binding.video.setTitleBar(name,-1)
        })
    }

    override fun onDestroy(){
        super.onDestroy()
        binding.video.suspend()
    }

    override fun onBackPressed(){
        if(binding.video.isFullScreen){
            binding.video.setNoFullScreen()
        }else{
            super.onBackPressed()
        }
    }
}

