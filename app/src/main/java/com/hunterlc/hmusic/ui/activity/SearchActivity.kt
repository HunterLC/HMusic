package com.hunterlc.hmusic.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.adapter.SearchHotAdapter
import com.hunterlc.hmusic.data.SongsInnerData
import com.hunterlc.hmusic.databinding.ActivitySearchBinding
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.ui.fragment.HomeFragment
import com.hunterlc.hmusic.ui.fragment.MyFragment
import com.hunterlc.hmusic.ui.fragment.SearchSingerFragment
import com.hunterlc.hmusic.ui.fragment.SearchTrackFragment
import com.hunterlc.hmusic.ui.viewmodel.MyFragmentViewModel
import com.hunterlc.hmusic.ui.viewmodel.SearchTrackViewModel
import com.hunterlc.hmusic.ui.viewmodel.SearchViewModel
import com.hunterlc.hmusic.util.ViewPager2Util
import com.hunterlc.hmusic.util.runOnMainThread

class SearchActivity : BaseActivity() {
    private lateinit var binding: ActivitySearchBinding

    private val searchViewModel: SearchViewModel by viewModels()

    private var realKeyWord = ""

    override fun initBinding() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    override fun initView() {
        // 获取焦点
        binding.etSearch.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }
        // 获取推荐关键词
        searchViewModel.getSearchDefault()
        // 获取热搜
        searchViewModel.getSearchHotDetail()

        //对应两个tab创建两个fragment
        binding.vp2Search.offscreenPageLimit = 2
        binding.vp2Search.adapter = object : FragmentStateAdapter(this) {
            // 2 个页面
            override fun getItemCount(): Int {
                return 2
            }

            //对应两个fragment
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> SearchTrackFragment()
                    else -> SearchSingerFragment()
                }
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.vp2Search) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.track)
                else -> getString(R.string.singer)
            }
        }.attach()

        ViewPager2Util.changeToNeverMode(binding.vp2Search)
    }

    override fun initListener() {
        binding.apply {
            // ivBack
            ivBack.setOnClickListener {
                if (clPanel.visibility == View.VISIBLE) {
                    finish()
                } else {
                    clPanel.visibility = View.VISIBLE
                }
            }
            // 搜索
            btnSearch.setOnClickListener {
                search()
            }

            // 搜索框
            etSearch.apply {
                setOnEditorActionListener { _, p1, _ ->
                    if (p1 == EditorInfo.IME_ACTION_SEARCH) { // 软键盘点击了搜索
                        search()
                    }
                    false
                }

                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(s: Editable) {
                        if (binding.etSearch.text.toString() != "") {
                            binding.ivClear.visibility = View.VISIBLE // 有文字，显示清楚按钮
                        } else {
                            binding.ivClear.visibility = View.INVISIBLE // 隐藏
                        }
                    }
                })
            }

            ivClear.setOnClickListener {
                    binding.etSearch.setText("")
                }
        }

    }

    override fun initObserver() {
        searchViewModel.searchDefaultLiveData.observe(this, Observer { result ->
            val searchDefaultData = result.getOrNull()
            if (searchDefaultData != null) {
                binding.etSearch.hint = searchDefaultData.showKeyword
                realKeyWord = searchDefaultData.realkeyword
            }
        })
        searchViewModel.searchHotDetailLiveData.observe(this, Observer { result ->
            val searchHotDetailInfo = result.getOrNull()
            if (searchHotDetailInfo != null){
                binding.rvSearchHot.layoutManager = LinearLayoutManager(this)
                val searchHotAdapter = SearchHotAdapter(searchHotDetailInfo)
                searchHotAdapter.setOnItemClick(object : SearchHotAdapter.OnItemClick {
                    override fun onItemClick(view: View?, position: Int) {
                        val searchWord = searchHotDetailInfo.data[position].searchWord
                        binding.etSearch.setText(searchWord)
                        binding.etSearch.setSelection(searchWord.length)
                        search()
                    }
                })
                binding.rvSearchHot.adapter = searchHotAdapter
                binding.rvSearchHot.layoutManager = GridLayoutManager(this, 2)
            }
        })

    }

    /**
     * 搜索音乐
     */
    private fun search() {
        // 关闭软键盘
        val inputMethodManager: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.window?.decorView?.windowToken, 0)

        var keywords = binding.etSearch.text.toString()

        if (keywords == "") {
            keywords = realKeyWord
            binding.etSearch.setText(keywords)
            binding.etSearch.setSelection(keywords.length)
        }
        if (keywords != "") {
            searchViewModel.searchContent.value = keywords
            binding.clPanel.visibility = View.GONE
            binding.clSearchList.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (binding.clPanel.visibility == View.VISIBLE) {
            super.onBackPressed()
        } else {
            binding.clPanel.visibility = View.VISIBLE
            binding.clSearchList.visibility = View.GONE
        }
    }

}