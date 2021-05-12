package com.hunterlc.hmusic.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.databinding.FragmentLoginByUidBinding
import com.hunterlc.hmusic.databinding.FragmentMyBinding
import com.hunterlc.hmusic.ui.base.BaseFragment
import com.hunterlc.hmusic.util.ConfigUtil
import java.util.regex.Pattern

class LoginByUidFragment: BaseFragment() {
    private var _binding: FragmentLoginByUidBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginByUidBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initListener() {
        // 点击登录按钮
        binding.btnLogin.setOnClickListener {
            // 获取输入
            var netease = binding.etUid.text.toString()

            // 判断是否直接是网易云分享用户链接
            if (netease != "") {
                val index = netease.indexOf("id=")
                if (index != -1) {
                    netease = netease.subSequence(index + 3, netease.length).toString()
                }
                netease = keepDigital(netease)
                if (netease != "") {
                    MyApplication.mmkv.encode(ConfigUtil.UID, netease.toLong())
                    // UID 登录清空 Cookie
                    MyApplication.userManager.setCloudMusicCookie("")
                    // 发送广播
                    val intent = Intent("com.hunterlc.hmusic.LOGIN")
                    intent.setPackage(activity?.packageName)
                    activity?.sendBroadcast(intent)
                    // 通知 Login 关闭
                    activity?.setResult(AppCompatActivity.RESULT_OK, Intent())
                    activity?.finish()
                } else {
                    Toast.makeText(MyApplication.context,"错误的 UID", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(MyApplication.context,"请输入 UID", Toast.LENGTH_SHORT).show()
            }

        }

        // 帮助
        binding.tvHelp.setOnClickListener {
        }
    }

    /**
     * 只保留数字
     */
    private fun keepDigital(oldString: String): String {
        val newString = StringBuffer()
        val matcher = Pattern.compile("\\d").matcher(oldString)
        while (matcher.find()) {
            newString.append(matcher.group())
        }
        return newString.toString()
    }

}