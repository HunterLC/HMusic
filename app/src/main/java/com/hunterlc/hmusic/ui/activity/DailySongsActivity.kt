package com.hunterlc.hmusic.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.adapter.DailySongsAdapter
import com.hunterlc.hmusic.adapter.SongDataAdapter
import com.hunterlc.hmusic.databinding.ActivityDailySongsBinding
import com.hunterlc.hmusic.databinding.ActivityMainBinding
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.ui.viewmodel.DailySongsViewModel
import com.hunterlc.hmusic.util.LogUtil

class DailySongsActivity : BaseActivity(){
    //绑定控件
    private lateinit var binding: ActivityDailySongsBinding
    private var detailPlaylistAdapter = DailySongsAdapter(this)
    private val dailySongsViewModel: DailySongsViewModel by lazy { ViewModelProvider(this).get(
        DailySongsViewModel::class.java)}

    /***
     * 初始化绑定
     */
    override fun initBinding() {
        binding = ActivityDailySongsBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        binding.rvPlaylist.layoutManager = LinearLayoutManager(this)
        if (MyApplication.userManager.hasCookie()){
            //获取日推
            dailySongsViewModel.getDailySongs(MyApplication.userManager.getCloudMusicCookie())

        } else {
            Toast.makeText(this,"未登录",Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    override fun initObserver() {
        dailySongsViewModel.cookieLiveData.observe(this, Observer { result ->
            val songList = result.getOrNull()
            songList?.let {
                detailPlaylistAdapter = DailySongsAdapter(this).apply {
                    submitList(songList.dailySongs)
                }
                binding.rvPlaylist.adapter = detailPlaylistAdapter
            }
        })
    }
}