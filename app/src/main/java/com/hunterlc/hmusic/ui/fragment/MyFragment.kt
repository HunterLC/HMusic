package com.hunterlc.hmusic.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.adapter.PlaylistAdapter
import com.hunterlc.hmusic.data.PlaylistData
import com.hunterlc.hmusic.databinding.FragmentMyBinding
import com.hunterlc.hmusic.ui.base.BaseFragment
import com.hunterlc.hmusic.ui.viewmodel.MainViewModel
import com.hunterlc.hmusic.ui.viewmodel.MyFragmentViewModel
import com.hunterlc.hmusic.util.AnimationUtil
import com.hunterlc.hmusic.util.LogUtil
import com.hunterlc.hmusic.util.runOnMainThread

/***
 * 首页 我的
 */
class MyFragment: BaseFragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!

    // activity 和 fragment 共享数据
    private val mainViewModel: MainViewModel by activityViewModels()
    private val myFragmentViewModel by lazy { ViewModelProvider(this).get(MyFragmentViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {

    }

    override fun initListener() {
        binding.apply {
            clUser.setOnClickListener {
                //点击头像区域判断当前用户是否登录
                AnimationUtil.click(it)
                if (MyApplication.userManager.getCurrentUid() == 0L) {
                    MyApplication.activityManager.startLoginActivity(requireActivity())
                } else {
//                    MyApplication.activityManager.startUserActivity(
//                        requireActivity(),
//                        MyApplication.userManager.getCurrentUid()
//                    )
                }
            }
            // 我喜欢的音乐
            clFavorite.setOnClickListener {

            }
            // 播放历史
            clLatest.setOnClickListener {

            }
            clPersonalFM.setOnClickListener {

            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initObserver() {

        mainViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId == 0L) {
                binding.tvUserName.text = "立即登录"
            } else {
                //更新歌单
                myFragmentViewModel.getUserPlayList(userId)
                //更新用户信息
            }
        }

        myFragmentViewModel.userInfoLiveData.observe(this, Observer { result ->
            val userDetailData = result.getOrNull()
            if (userDetailData != null){
                runOnMainThread {
                    binding.ivUser.load(userDetailData.profile?.avatarUrl) {
                        transformations(CircleCropTransformation())
                        size(ViewSizeResolver(binding.ivUser))
                        // error(R.drawable.ic_song_cover)
                    }
                    binding.tvUserName.text = userDetailData.profile?.nickname
                    binding.tvLevel.text = "Lv.${userDetailData.level}"

                }
            }

        })

        // 用户歌单的观察
        myFragmentViewModel.userPlaylistLiveData.observe(this, Observer { result ->
            val userPlaylistData = result.getOrNull()
            if (userPlaylistData != null){
                LogUtil.e("Fragment","用户歌单正在更新")
                setPlaylist(userPlaylistData)
            }
        })


        mainViewModel.statusBarHeight.observe(viewLifecycleOwner, Observer {
            (binding.clTop.layoutParams as LinearLayout.LayoutParams).apply {
                topMargin = it + ((56 + 8) * mainViewModel.scale.value!! + 0.5f).toInt()
            }
        })

        mainViewModel.singleColumnPlaylist.observe(viewLifecycleOwner, Observer {
            val count = if (it) {
                1
            } else {
                2
            }
            binding.rvPlaylist.layoutManager = GridLayoutManager(this.context, count)
        })
    }

    /**
     * 设置歌单
     */
    private fun setPlaylist(playlist: ArrayList<PlaylistData>) {
        runOnMainThread {
            binding.rvPlaylist.adapter = activity?.let { it1 -> PlaylistAdapter(playlist, it1) }
        }
    }

}