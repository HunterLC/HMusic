package com.hunterlc.hmusic.ui.activity

import android.content.res.Configuration
import android.hardware.SensorManager
import android.net.Uri
import android.net.Uri.parse
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.adapter.CommentAdapter
import com.hunterlc.hmusic.databinding.ActivityVideoBinding
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.ui.viewmodel.VideoViewModel
import com.hunterlc.hmusic.util.LogUtil
import com.hunterlc.hmusic.util.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class VideoActivity : BaseActivity() {
    private lateinit var binding: ActivityVideoBinding
    private val videoViewModel by lazy { ViewModelProvider(this).get(VideoViewModel::class.java) }
    private var id: Long = 0L  //mv的id
    private lateinit var name: String  //mv的名字

    private val hotCommentAdapter = CommentAdapter()
    private val latestCommentAdapter = CommentAdapter()


    /***
     * 初始化绑定
     */
    override fun initBinding() {
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {

        id = intent.getLongExtra("mv", 0L)
        name = intent.getStringExtra("name").toString()
        LogUtil.e("mv的名字", name)
        videoViewModel.getMvUrl(id)
        //评论
        binding.rvHotComment.layoutManager = LinearLayoutManager(MyApplication.context)
        binding.rvLatestComment.layoutManager = LinearLayoutManager(MyApplication.context)
        binding.rvHotComment.adapter = hotCommentAdapter
        binding.rvLatestComment.adapter = latestCommentAdapter
        lifecycleScope.launch {
            videoViewModel.getCommentsPagingData(1, id, 2).collectLatest { pagingData ->
                hotCommentAdapter.submitData(pagingData)
            }
        }

    }

    override fun initObserver() {
        videoViewModel.videoPath.observe(this, Observer { result ->
            val path = result.getOrNull()
            val uri: Uri = parse(path)
            binding.video.videoUri = uri
            binding.video.setTitleBar(name, -1)
        })
    }

    override fun initListener() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position
                when (position) {
                    0 -> {
                        //Toast.makeText(MyApplication.context,tab?.text,Toast.LENGTH_SHORT).show()
                        binding.rvHotComment.visibility = View.VISIBLE
                        binding.rvLatestComment.visibility = View.GONE
                    }

                    else -> {
                        //Toast.makeText(MyApplication.context,tab?.text,Toast.LENGTH_SHORT).show()
                        binding.rvHotComment.visibility = View.GONE
                        binding.rvLatestComment.visibility = View.VISIBLE
                        lifecycleScope.launch {
                            videoViewModel.getCommentsPagingData(1, id, 3)
                                .collectLatest { pagingData ->
                                    latestCommentAdapter.submitData(pagingData)
                                }
                        }
                    }
                }
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }


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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //设置横屏状态下的播放
            binding.clComment.visibility = View.GONE
            (binding.clVideo.layoutParams as ConstraintLayout.LayoutParams).apply {
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //设置竖屏状态下的播放
            binding.clComment.visibility = View.VISIBLE
            (binding.clVideo.layoutParams as ConstraintLayout.LayoutParams).apply {
                height = 200.dp()
            }
        }
    }
}

