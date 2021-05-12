package com.hunterlc.hmusic.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.databinding.ActivityLoginBinding
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.ui.fragment.HomeFragment
import com.hunterlc.hmusic.ui.fragment.LoginByCellphoneFragment
import com.hunterlc.hmusic.ui.fragment.LoginByUidFragment
import com.hunterlc.hmusic.ui.fragment.MyFragment
import com.hunterlc.hmusic.util.ConfigUtil
import com.hunterlc.hmusic.util.LogUtil
import com.hunterlc.hmusic.util.ViewPager2Util
import java.net.URLEncoder
import java.util.regex.Pattern

/**
 * 通过网易云 UID 登录
 */
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun initView() {
        //对应两个tab创建两个fragment
        binding.viewPager2.offscreenPageLimit = 2
        binding.viewPager2.adapter = object : FragmentStateAdapter(this) {
            // 2 个页面
            override fun getItemCount(): Int {
                return 2
            }

            //对应两个fragment
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> LoginByCellphoneFragment()
                    else -> LoginByUidFragment()
                }
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.login_by_phone)
                else -> getString(R.string.login_by_uid)
            }
        }.attach()

        ViewPager2Util.changeToNeverMode(binding.viewPager2)
    }

    override fun initBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

}