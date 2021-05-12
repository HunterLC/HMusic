package com.hunterlc.hmusic.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.hunterlc.hmusic.adapter.TopListAdapter
import com.hunterlc.hmusic.data.PlaylistData
import com.hunterlc.hmusic.databinding.ActivityTopListBinding
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.ui.viewmodel.PlaylistViewModel
import com.hunterlc.hmusic.ui.viewmodel.TopListViewModel
import com.hunterlc.hmusic.util.LogUtil
import com.hunterlc.hmusic.util.runOnMainThread

class TopListActivity : BaseActivity() {

    private lateinit var binding: ActivityTopListBinding

    private val topListViewModel: TopListViewModel by viewModels()

    override fun initBinding() {
        binding = ActivityTopListBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        //获取榜单
        topListViewModel.getTopList()
    }

    override fun initObserver() {
        topListViewModel.topListLiveData.observe(this@TopListActivity, { result ->
            LogUtil.e("QAQ", "QAQ")
            val topListData = result.getOrNull()
            if (topListData != null) {
                setPlaylist(topListData)
            } else {
                LogUtil.e("QAQ", "QAQ")
            }
        })
    }

    override fun initListener() {

    }

    private fun setPlaylist(playlist: List<PlaylistData>) {
        LogUtil.e("QAQ", "QAQ111")
        runOnMainThread {
            binding.rvPlaylist.adapter = TopListAdapter(playlist)
        }
    }
}