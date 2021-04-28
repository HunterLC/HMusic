package com.hunterlc.hmusic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.adapter.SearchSingerAdapter
import com.hunterlc.hmusic.adapter.SearchSongAdapter
import com.hunterlc.hmusic.data.SongsInnerData
import com.hunterlc.hmusic.databinding.FragmentSearchSingerBinding
import com.hunterlc.hmusic.databinding.FragmentSearchTrackBinding
import com.hunterlc.hmusic.ui.base.BaseFragment
import com.hunterlc.hmusic.ui.viewmodel.SearchSingerViewModel
import com.hunterlc.hmusic.ui.viewmodel.SearchTrackViewModel
import com.hunterlc.hmusic.ui.viewmodel.SearchViewModel
import com.hunterlc.hmusic.util.runOnMainThread

class SearchSingerFragment: BaseFragment() {
    private var _binding: FragmentSearchSingerBinding? = null
    private val binding get() = _binding!!

    // activity 和 fragment 共享数据
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val searchSingerViewModel by lazy { ViewModelProvider(this).get(SearchSingerViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchSingerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
    }

    override fun initListener() {

    }

    override fun initObserver() {

        searchViewModel.searchContent.observe(this@SearchSingerFragment, Observer { result ->
            //调用搜索
            if (result != null){
                searchSingerViewModel.search(result,100)
            }
        })

        searchSingerViewModel.searchLiveData.observe(this@SearchSingerFragment, Observer { result ->
            val searchResult = result.getOrNull()
            if (searchResult != null){
                initRecycleView(searchResult.artists as ArrayList<SongsInnerData.ArtistsData>)
            }
        })
    }

    private fun initRecycleView(artistsList: ArrayList<SongsInnerData.ArtistsData>) {
        runOnMainThread {
            binding.rvArtistsList.layoutManager = LinearLayoutManager(MyApplication.context)
            binding.rvArtistsList.adapter = SearchSingerAdapter {}.apply {
                submitList(artistsList)
            }
        }
    }
}