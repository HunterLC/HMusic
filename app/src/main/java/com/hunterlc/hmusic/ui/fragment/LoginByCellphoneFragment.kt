package com.hunterlc.hmusic.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.databinding.FragmentLoginByCellphoneBinding
import com.hunterlc.hmusic.ui.base.BaseFragment
import com.hunterlc.hmusic.ui.viewmodel.LoginByCellphoneViewModel
import com.hunterlc.hmusic.util.ConfigUtil
import com.hunterlc.hmusic.util.md5
import java.net.URLEncoder

class LoginByCellphoneFragment: BaseFragment() {
    private var _binding: FragmentLoginByCellphoneBinding? = null
    private val binding get() = _binding!!

    private val loginCellphoneViewModel by lazy { ViewModelProvider(this).get(
        LoginByCellphoneViewModel::class.java) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginByCellphoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initListener() {
        // 点击登录按钮
        binding.btnLoginByPhone.setOnClickListener {
            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            if (phone == "" || password == "") {
                Toast.makeText(MyApplication.context,"请输入手机号或密码",Toast.LENGTH_SHORT).show()
            } else {
                binding.btnLoginByPhone.visibility = View.GONE
                binding.llLoading.visibility = View.VISIBLE
                binding.lottieLoading.repeatCount = -1
                binding.lottieLoading.playAnimation()
                //登录
                loginCellphoneViewModel.loginByCellphone(phone,86, password.md5())
            }

        }
    }

    override fun initObserver() {
        loginCellphoneViewModel.loginLiveData.observe(this, Observer { result ->
            val loginInfo = result.getOrNull()
            loginInfo?.let {
                if (it.code == 200){
                    //登录成功
                    it.profile?.let { it1 -> MyApplication.mmkv.encode(ConfigUtil.UID, it1.userId) }
                    // UID 登录Cookie
                    it.cookie?.let { it1 -> MyApplication.userManager.setCloudMusicCookie(URLEncoder.encode(it1)) }
                    // 发送广播
                    val intent = Intent("com.hunterlc.hmusic.LOGIN")
                    intent.setPackage(activity?.packageName)
                    activity?.sendBroadcast(intent)
                    // 通知 Login 关闭
                    activity?.setResult(RESULT_OK, Intent())
                    activity?.finish()
                } else {
                    //登录失败
                    binding.btnLoginByPhone.visibility = View.VISIBLE
                    binding.llLoading.visibility = View.GONE
                    binding.lottieLoading.cancelAnimation()
                    if (it.code == 250) {
                        Toast.makeText(MyApplication.context,"错误代码：250\n当前登录失败，请稍后再试",Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(MyApplication.context,"登录失败，请检查服务、用户名或密码",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

}