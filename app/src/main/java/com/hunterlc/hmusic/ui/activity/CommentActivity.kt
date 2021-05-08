package com.hunterlc.hmusic.ui.activity

import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.adapter.CommentAdapter
import com.hunterlc.hmusic.databinding.ActivityCommentBinding
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.ui.viewmodel.CommentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CommentActivity : BaseActivity() {
    companion object {
        const val EXTRA_STRING_NAME = "extra_string_name"
        const val EXTRA_LONG_ID = "extra_long_id"
    }

    private lateinit var binding: ActivityCommentBinding

    private var id: Long = 0L
    private var name: String = ""
    private val hotCommentAdapter = CommentAdapter()
    private val latestCommentAdapter = CommentAdapter()
    private val commentViewModel by lazy { ViewModelProvider(this).get(CommentViewModel::class.java) }

    override fun initBinding() {
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initData() {

    }

    override fun initView() {

        binding.rvHotComment.layoutManager = LinearLayoutManager(MyApplication.context)
        binding.rvLatestComment.layoutManager = LinearLayoutManager(MyApplication.context)
        binding.rvHotComment.adapter = hotCommentAdapter
        binding.rvLatestComment.adapter = latestCommentAdapter

        id = intent.getLongExtra(EXTRA_LONG_ID,0L)
        name = intent.getStringExtra(EXTRA_STRING_NAME).toString()
        binding.titleBar.setTitleBarText(name)

        lifecycleScope.launch {
            commentViewModel.getCommentsPagingData(0, id ,2).collectLatest { pagingData ->
                hotCommentAdapter.submitData(pagingData)
            }
        }


//        hotCommentAdapter.addLoadStateListener {
//            when (it.refresh) {
//                is LoadState.NotLoading -> {
////                    binding.progressBar.visibility = View.INVISIBLE
//                    binding.rvHotComment.visibility = View.VISIBLE
//                }
//                is LoadState.Loading -> {
////                    binding.progressBar.visibility = View.VISIBLE
//                    binding.rvHotComment.visibility = View.INVISIBLE
//                }
//                is LoadState.Error -> {
//                    val state = it.refresh as LoadState.Error
////                    binding.progressBar.visibility = View.INVISIBLE
//                    Toast.makeText(MyApplication.context, "Load Error: ${state.error.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

//        var rvPlaylistScrollY = 0
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            binding.rvHotComment.setOnScrollChangeListener { _, _, _, _, oldScrollY ->
//                rvPlaylistScrollY += oldScrollY
//                slideBackEnabled = rvPlaylistScrollY == 0
//            }
//            binding.rvLatestComment.setOnScrollChangeListener { _, _, _, _, oldScrollY ->
//                rvPlaylistScrollY += oldScrollY
//                slideBackEnabled = rvPlaylistScrollY == 0
//            }
//        }
    }

    override fun initListener() {

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position
                when( position ){
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
                            commentViewModel.getCommentsPagingData(0,id,3).collectLatest { pagingData ->
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

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_no_anim,
            R.anim.anim_slide_exit_bottom
        )
    }
}