package com.hunterlc.hmusic.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.databinding.ActivityMainBinding
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.ui.viewmodel.MainViewModel
import com.hunterlc.hmusic.util.StatusbarUtil.getStatusBarHeight
import eightbitlab.com.blurview.RenderScriptBlur
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hunterlc.hmusic.MyApplication.Companion.context
import com.hunterlc.hmusic.ui.fragment.HomeFragment
import com.hunterlc.hmusic.ui.fragment.MyFragment
import com.hunterlc.hmusic.ui.viewmodel.LoginViewModel
import com.hunterlc.hmusic.ui.viewmodel.MyFragmentViewModel
import com.hunterlc.hmusic.ui.viewmodel.SearchViewModel
import com.hunterlc.hmusic.util.*
import com.hunterlc.hmusic.util.cache.ACache
import kotlinx.android.synthetic.main.menu_main.*
import kotlin.concurrent.thread

class MainActivity : BaseActivity() {
    //绑定控件
    private lateinit var binding: ActivityMainBinding
    //private lateinit var headSetChangeReceiver: HeadsetChangeReceiver // 耳机广播接收
    private lateinit var loginReceiver: LoginReceiver // 登录广播接收
    /* 设置改变广播接收 */
    private lateinit var settingChangeReceiver: SettingChangeReceiver

    private val mainViewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java)}

    /***
     * 初始化绑定
     */
    override fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        miniPlayer = binding.miniPlayer
        setContentView(binding.root)
    }

    /***
     * 数据初始化
     */
    override fun initData() {

    }

    override fun initBroadcastReceiver(){
        // Intent 过滤器
        var intentFilter = IntentFilter()

        intentFilter.addAction("com.hunterlc.hmusic.LOGIN")
        loginReceiver = LoginReceiver()
        registerReceiver(loginReceiver, intentFilter) // 注册接收器

        intentFilter = IntentFilter()
        intentFilter.addAction(SettingActivity.ACTION)
        settingChangeReceiver = SettingChangeReceiver()
        registerReceiver(settingChangeReceiver, intentFilter)
    }

    /***
     * 视图初始化
     */
    override fun initView() {
        mainViewModel.updateUI()

        thread {
            ACache.get(this).getAsBitmap(ConfigUtil.APP_THEME_BACKGROUND)?.let {
                runOnMainThread {
                    binding.ivTheme.setImageBitmap(it)
                    val pixelsPair = ScreenUtil.getDisplayPixels()
                    Glide.with(this)
                        .asBitmap()
                        .load(it)
                        .override(pixelsPair.first, pixelsPair.second)
                        .apply(RequestOptions().centerCrop())
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                // binding.navigationView.background = resource.toDrawable(resources)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })

                }
            }
        }

        // android 5.0 出来的Transition过渡动画
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            //是否覆盖执行，其实可以理解成是否同步执行还是顺序执行
            window.allowEnterTransitionOverlap = true
            window.allowReturnTransitionOverlap = true
        }

        //初始化两块毛玻璃特效
        //用法参见https://github.com/Dimezis/BlurView
        val radius = 20f
        val decorView: View = window.decorView
        val windowBackground: Drawable = decorView.background
        binding.blurViewTop.setupWith(decorView.findViewById(R.id.clTheme))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)
        binding.blurViewPlay.setupWith(decorView.findViewById(R.id.clTheme))
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)

        // 适配状态栏
        val statusBarHeight = getStatusBarHeight(window, this) // px
        LogUtil.e("状态栏高度",statusBarHeight.toString())
        mainViewModel.statusBarHeight.value = statusBarHeight
        //dp转px
        val scale = mainViewModel.scale.value!!
        LogUtil.e("scale",scale.toString())

        (binding.blurViewTop.layoutParams as ConstraintLayout.LayoutParams).apply {
            height = (56 * scale + 0.5f).toInt() + statusBarHeight
            LogUtil.e("blur高度",height.toString())
        }
        (binding.titleBar.layoutParams as ConstraintLayout.LayoutParams).apply{
            topMargin = statusBarHeight
        }
        // 侧滑状态栏适配
        (binding.menuMain.cvOthers.layoutParams as LinearLayout.LayoutParams).apply{
            topMargin = statusBarHeight + (8 * scale + 0.5f).toInt()
        }
        // 适配导航栏
        val navigationBarHeight = if (MyApplication.mmkv.decodeBool(ConfigUtil.PARSE_NAVIGATION, true)) {
            getNavigationBarHeight(this)
        } else {
            0
        }
        (binding.miniPlayer.root.layoutParams as ConstraintLayout.LayoutParams).apply{
            bottomMargin = navigationBarHeight
        }
        (binding.blurViewPlay.layoutParams as ConstraintLayout.LayoutParams).apply{
            height = (52 * scale + 0.5f).toInt() + navigationBarHeight
        }

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
                    0 -> MyFragment()
                    else -> HomeFragment()
                }
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.my)
                else -> getString(R.string.home)
            }
        }.attach()

        ViewPager2Util.changeToNeverMode(binding.viewPager2)
    }

    override fun initObserver() {
        mainViewModel.userInfoLiveData.observe(this, Observer { result ->
            val userDetailData = result.getOrNull()
            if (userDetailData != null){
                binding.menuMain.ivUserPic.load(userDetailData.profile?.avatarUrl) {
                    transformations(CircleCropTransformation())
                    size(ViewSizeResolver(binding.menuMain.ivUserPic))
                }
                binding.menuMain.ivUserBackground.load(userDetailData.profile?.backgroundUrl){
                    transformations(RoundedCornersTransformation())
                    size(ViewSizeResolver(binding.menuMain.ivUserBackground))
                }
                binding.menuMain.tvUserName.text = userDetailData.profile?.nickname
                binding.menuMain.tvUserSignature.text = userDetailData.profile?.signature

            }

        })
    }

    /***
     * 控件响应初始化
     */
    override fun initListener() {
        //
        binding.apply {
            // 搜索按钮
            ivSearch.setOnClickListener {
                var intent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(intent)
            }
            // 设置按钮
            ivSettings.setOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        // 侧滑
        binding.menuMain.apply {
            itemSettings.setOnClickListener {
                MyApplication.activityManager.startSettingActivity(this@MainActivity)
            }
            itemExit.setOnClickListener {
                MyApplication.mmkv.encode(ConfigUtil.UID,0L)
                mainViewModel.setUserId()
                MyApplication.activityManager.startSplashActivity(this@MainActivity)
                finish()
            }

        }

    }

    override fun onBackPressed() {
        //响应返回键，如果侧滑菜单打开时返回，则收回菜单
        if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }
    override fun onStart() {
        super.onStart()
        // 请求广播
        MyApplication.musicController.value?.sendBroadcast()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑广播接收
        try {
            //unregisterReceiver(headSetChangeReceiver)
            unregisterReceiver(loginReceiver)
            unregisterReceiver(settingChangeReceiver)
        } catch (e: Exception) {

        }
    }

    inner class LoginReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // 通知 viewModel
            mainViewModel.setUserId()
        }
    }

    inner class SettingChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mainViewModel.updateUI()
        }
    }


}