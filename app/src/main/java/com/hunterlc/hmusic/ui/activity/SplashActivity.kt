package com.hunterlc.hmusic.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.databinding.ActivitySplashBinding
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.util.LogUtil


class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding

    //跳转的时间线程
    var thread: TimeThread? = null

    //消息类型
    val MSG_TYPE = 1

    override fun initBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        //val typefaceEN = Typeface.createFromAsset(assets, "fonts/SellvinyQueen.ttf")
        val typefaceCN = Typeface.createFromAsset(assets, "fonts/Happy.ttf")
        //binding.tvAPPName.typeface = typefaceEN
        binding.tvAPPSlogan.typeface = typefaceCN
    }

    override fun initListener() {
        binding.apply{
            //响应跳过启动页
            btnSkipSplash.setOnClickListener {
                if (thread != null) {
                    thread?.interrupt()
                    thread = null
                }
                LogUtil.d("SplashActivity", "clicked the SKIP button")
                //跳转到首页
                MyApplication.activityManager.startMainActivity(this@SplashActivity)
                //结束当前Activity
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //开启跳过倒计时线程
        if (thread == null) {
            thread = TimeThread()
            thread?.start()
            /* Log.d("Thread", "startThread()....")*/
        }
    }

    //不响应
    override fun onBackPressed() {

    }

    //使用handle在主线程更新UI
    val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            //进行ui操作
            when (msg.what) {
                MSG_TYPE -> {
                    binding.btnSkipSplash.text = "跳过 ${msg.arg1}"
                    if (msg.arg1 <= 0) {
                        //先销毁欢迎界面
                        finish()
                        //去登录界面
                        MyApplication.activityManager.startMainActivity(this@SplashActivity)
                    }
                }
            }
        }
    }

    // 自定义的线程---控制倒计时
    inner class TimeThread : Thread() {
        override fun run() {
            var countDown = 1  //倒计时
            while (countDown >= 0) {
                var msg = Message()
                msg.what = MSG_TYPE
                //把倒计时传过去
                msg.arg1 = countDown
                //倒计时发送
                handler.sendMessage(msg)
                countDown--
                try {
                    // 每1000毫秒更新一次位置
                    sleep(1000)
                    //播放进度
                } catch (e: Exception) {
                    // e.printStackTrace()
                    break
                }
            }
        }
    }

}