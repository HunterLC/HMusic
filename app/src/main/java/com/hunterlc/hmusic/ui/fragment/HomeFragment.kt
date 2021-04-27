package com.hunterlc.hmusic.ui.fragment

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
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.adapter.BannerImageAdapter
import com.hunterlc.hmusic.databinding.FragmentHomeBinding
import com.hunterlc.hmusic.databinding.FragmentMyBinding
import com.hunterlc.hmusic.ui.base.BaseFragment
import com.hunterlc.hmusic.ui.viewmodel.HomeFragmentViewModel
import com.hunterlc.hmusic.ui.viewmodel.MainViewModel
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
    }

    override fun initObserver() {
        mainViewModel.statusBarHeight.observe(viewLifecycleOwner) {
            (binding.clUser.layoutParams as LinearLayout.LayoutParams).apply {
                topMargin = it + ((56 + 8) * mainViewModel.scale.value!! + 0.5f).toInt()
            }
        }
        homeFragmentViewModel.bannerInfoLiveData.observe(this, Observer {  result ->
            val banners = result.getOrNull()
            if (banners != null){
                for (item in banners){
                    imageUrls.add(item.pic)
                }
                var adapter = BannerImageAdapter(imageUrls)
                banner?.let {
                    it.addBannerLifecycleObserver(this)
                    it.setIndicator(CircleIndicator(MyApplication.context))
                    it.setBannerRound(20f)
                    it.setPageTransformer(ZoomOutPageTransformer());
                    it.adapter = adapter
                }
            }

        })
    }

}