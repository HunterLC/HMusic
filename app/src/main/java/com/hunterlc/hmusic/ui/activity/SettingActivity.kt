package com.hunterlc.hmusic.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.databinding.ActivitySettingBinding
import com.hunterlc.hmusic.ui.base.BaseActivity
import com.hunterlc.hmusic.util.BroadcastUtil
import com.hunterlc.hmusic.util.ConfigUtil
import com.hunterlc.hmusic.util.DarkThemeUtil

class SettingActivity : BaseActivity() {

    companion object {
        const val ACTION = "com.hunterlc.hmusic.SETTINGS_CHANGE"
    }

    private lateinit var binding: ActivitySettingBinding

    override fun initBinding() {
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            //binding.itemAudioFocus.visibility = View.GONE
        }
        // 按钮
        binding.apply {
            switcherDarkTheme.setChecked(MyApplication.mmkv.decodeBool(ConfigUtil.DARK_THEME, false))
            switcherSquareCD.setChecked(MyApplication.mmkv.decodeBool(ConfigUtil.SQUARE_CD_COVER, false))
            switcherSingleColumnPlaylist.setChecked(MyApplication.mmkv.decodeBool(ConfigUtil.SINGLE_COLUMN_USER_PLAYLIST, false))
            switcherPlayOnMobile.setChecked(MyApplication.mmkv.decodeBool(ConfigUtil.PLAY_ON_MOBILE,false))
            switcherDynamicBackground.setChecked(MyApplication.mmkv.decodeBool(ConfigUtil.DYNAMIC_BACKGROUND,false))
        }

    }

    override fun initListener() {
        binding.apply {
            switcherDarkTheme.setOnCheckedChangeListener {
                MyApplication.mmkv.encode(ConfigUtil.DARK_THEME, it)
                DarkThemeUtil.setDarkTheme(it)
            }

            switcherSquareCD.setOnCheckedChangeListener {
                MyApplication.mmkv.encode(ConfigUtil.SQUARE_CD_COVER, it)
            }

            switcherSingleColumnPlaylist.setOnCheckedChangeListener {
                MyApplication.mmkv.encode(ConfigUtil.SINGLE_COLUMN_USER_PLAYLIST, it)
            }

            switcherPlayOnMobile.setOnCheckedChangeListener {
                MyApplication.mmkv.encode(ConfigUtil.PLAY_ON_MOBILE, it)
            }

            switcherDynamicBackground.setOnCheckedChangeListener {
                MyApplication.mmkv.encode(ConfigUtil.DYNAMIC_BACKGROUND, it)
            }



        }
    }

    override fun onPause() {
        super.onPause()
        BroadcastUtil.send(this, ACTION)
    }
}