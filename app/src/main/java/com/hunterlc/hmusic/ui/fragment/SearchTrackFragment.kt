package com.hunterlc.hmusic.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.adapter.SearchSongAdapter
import com.hunterlc.hmusic.adapter.SongDataAdapter
import com.hunterlc.hmusic.data.SongsInnerData
import com.hunterlc.hmusic.databinding.FragmentSearchTrackBinding
import com.hunterlc.hmusic.ui.base.BaseFragment
import com.hunterlc.hmusic.ui.viewmodel.SearchTrackViewModel
import com.hunterlc.hmusic.ui.viewmodel.SearchViewModel
import com.hunterlc.hmusic.util.runOnMainThread

class SearchTrackFragment: BaseFragment() {
    private var _binding: FragmentSearchTrackBinding? = null
    private val binding get() = _binding!!

    // activity 和 fragment 共享数据
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val searchTrackViewModel by lazy { ViewModelProvider(this).get(SearchTrackViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
    }

    override fun initListener() {

    }

    override fun initObserver() {

        searchViewModel.searchContent.observe(this@SearchTrackFragment, Observer { result ->
            //调用搜索
            if (result != null){
                searchTrackViewModel.search(result,1)
            }
        })

        searchTrackViewModel.searchLiveData.observe(this@SearchTrackFragment, Observer { result ->
            val searchResult = result.getOrNull()
            if (searchResult != null){
                initRecycleView(searchResult.songs as ArrayList<SongsInnerData>)
            }
        })
    }

    private fun initRecycleView(songList: ArrayList<SongsInnerData>) {
        runOnMainThread {
            binding.rvPlaylist.layoutManager = LinearLayoutManager(MyApplication.context)
            binding.rvPlaylist.adapter = SearchSongAdapter {}.apply {
                submitList(songList)
            }
        }
    }

}