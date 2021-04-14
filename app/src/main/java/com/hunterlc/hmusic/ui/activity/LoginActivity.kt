package com.hunterlc.hmusic.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.databinding.ActivityLoginBinding
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.util.ConfigUtil
import java.util.regex.Pattern

/**
 * 通过网易云 UID 登录
 */
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun initBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                    intent.setPackage(packageName)
                    sendBroadcast(intent)
                    // 通知 Login 关闭
                    setResult(RESULT_OK, Intent())
                    finish()
                } else {
                    Toast.makeText(this,"错误的 UID",Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this,"请输入 UID",Toast.LENGTH_SHORT).show()
            }

        }

        // 帮助
        binding.tvHelp.setOnClickListener {
            //MyApplication.activityManager.startWebActivity(this, "https://moriafly.xyz/foyou/uidlogin.html")
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