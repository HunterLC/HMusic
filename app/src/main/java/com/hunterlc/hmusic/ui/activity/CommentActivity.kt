package com.hunterlc.hmusic.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.databinding.ActivityCommentBinding
import com.hunterlc.hmusic.ui.base.SlideBackActivity
import com.hunterlc.hmusic.ui.fragment.SearchSingerFragment
import com.hunterlc.hmusic.ui.fragment.SearchTrackFragment
import com.hunterlc.hmusic.util.ViewPager2Util

class CommentActivity : SlideBackActivity() {
    companion object {
        const val EXTRA_INT_SOURCE = "extra_int_source"
        const val EXTRA_STRING_ID = "extra_string_id"
    }

    private lateinit var binding: ActivityCommentBinding

    private lateinit var id: String


    override fun initBinding() {
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initData() {
//        id = intent.getStringExtra(EXTRA_STRING_ID)?:""
//        source = intent.getIntExtra(EXTRA_INT_SOURCE, SOURCE_NETEASE)
//        when (source) {
//            SOURCE_NETEASE -> {
//                MyApplication.cloudMusicManager.getComment(id, {
//                    runOnMainThread {
//                        binding.rvComment.layoutManager = LinearLayoutManager(this@CommentActivity)
//                        binding.rvComment.adapter = CommentAdapter(it, this@CommentActivity)
//                    }
//                }, {
//
//                })
//            }
//        }
    }

    override fun initView() {
        bindSlide(this, binding.clBase)
        binding.vp2Comment.offscreenPageLimit = 3
        binding.vp2Comment.adapter = object : FragmentStateAdapter(this) {
            // 3 个页面
            override fun getItemCount(): Int {
                return 3
            }

            //对应三个fragment
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> SearchTrackFragment()
                    1 -> SearchTrackFragment()
                    else -> SearchSingerFragment()
                }
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.vp2Comment) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.recommended)
                1 -> getString(R.string.hot)
                else -> getString(R.string.latest)
            }
        }.attach()

        ViewPager2Util.changeToNeverMode(binding.vp2Comment)

//        var rvPlaylistScrollY = 0
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            binding.rvComment.setOnScrollChangeListener { _, _, _, _, oldScrollY ->
//                rvPlaylistScrollY += oldScrollY
//                slideBackEnabled = rvPlaylistScrollY == 0
//            }
//        }
    }

    override fun initListener() {
//        binding.btnSendComment.setOnClickListener {
//            val content = binding.etCommentContent.text.toString()
//            if (content != "") {
//                when (source) {
//                    SOURCE_NETEASE -> {
//                        MyApplication.cloudMusicManager.sendComment(1, 0, id, content, 0L, {
//                            toast("评论成功")
//                        }, {
//                            toast("评论失败")
//                        })
//                    }
//                }
//            } else {
//                toast("请输入")
//            }
//        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_slide_exit_bottom
        )
    }
}