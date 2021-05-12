package com.hunterlc.hmusic.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.adapter.BannerImageAdapter
import com.hunterlc.hmusic.adapter.PlaylistRecommendAdapter
import com.hunterlc.hmusic.data.RecommendPlaylistData
import com.hunterlc.hmusic.databinding.FragmentHomeBinding
import com.hunterlc.hmusic.databinding.FragmentMyBinding
import com.hunterlc.hmusic.ui.activity.TopListActivity
import com.hunterlc.hmusic.ui.base.BaseFragment
import com.hunterlc.hmusic.ui.viewmodel.HomeFragmentViewModel
import com.hunterlc.hmusic.ui.viewmodel.MainViewModel
import com.hunterlc.hmusic.util.runOnMainThread
import com.youth.banner.config.BannerConfig
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.transformer.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment: BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    // activity 和 fragment 共享数据
    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeFragmentViewModel by lazy { ViewModelProvider(this).get(
        HomeFragmentViewModel::class.java) }

    var imageUrls = mutableListOf<String>() //创建空的List

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun initView() {
        homeFragmentViewModel.getBanner()
        homeFragmentViewModel.getRecommendPlaylist()
    }

    override fun initListener() {
        _binding?.ivTopList1?.setOnClickListener {
            val intent = Intent(MyApplication.context,TopListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initObserver() {
        mainViewModel.statusBarHeight.observe(viewLifecycleOwner, Observer {
            (binding.clUser.layoutParams as LinearLayout.LayoutParams).apply {
                topMargin = it + ((56 + 4) * mainViewModel.scale.value!! + 0.5f).toInt()
            }
        })
        homeFragmentViewModel.bannerInfoLiveData.observe(this, Observer {  result ->
            val banners = result.getOrNull()
            if (banners != null){
                for (item in banners){
                    imageUrls.add(item.pic)
                }
                var adapter = BannerImageAdapter(imageUrls)
                banner?.let {
                    it.addBannerLifecycleObserver(this)
                    it.indicator = CircleIndicator(MyApplication.context)
                    it.setBannerRound(20f)
                    it.setPageTransformer(ZoomOutPageTransformer())
                    it.adapter = adapter
                }
            }

        })
        homeFragmentViewModel.recommendPlaylistLiveData.observe(this, Observer { result ->
            val recommendPlaylistData = result.getOrNull()
            if (recommendPlaylistData != null){
                runOnMainThread {
                    binding.rvPlaylistRecommend.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
                    binding.rvPlaylistRecommend.adapter = PlaylistRecommendAdapter(
                        recommendPlaylistData as ArrayList<RecommendPlaylistData>
                    )

                }
            }
        })
    }


}