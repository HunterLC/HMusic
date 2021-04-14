# HMusic
this is a simple music demo for practicing

# 写在最前面的话
+ 我是菜鸡
+ 开发进度
- [x] 启动页
- [ ] 登录页
- [ ] 主页面

# 更新日志
## 2021-04-04
1. 添加歌单界面
## 2021-04-01
1. 注意当访问的是http，而非https时，高版本Android需要申明一下
```
//在AndroidManifest.xml中
android:networkSecurityConfig="@xml/network_security_config"
//同时在新增的文件中加入
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```
2. 分辨率适配，注意dp和px的相互转换
## 2021-03-28
1. 毛玻璃特效[BlurView](https://github.com/Dimezis/BlurView)
2. 编写主界面MainActivity
3. 修复Dark模式下ActionBar重现问题
4. 支持English语言
5. 修复侧滑栏不支持沉浸的问题
## 2021-03-27
1. 添加MyApplication类，用于技巧性获取一些全局性的信息，例如context
```
//注意需要在AndroidManifest.xml中指定自己的Application，而非使用系统默认的Application
<application
        android:name=".MyApplication"
</application>
```
2. 添加LogUtil类，用于日志的输出，可以根据不同的阶段自定义日志输出的等级
3. 添加欢迎界面，SplashActivity
+ 沉浸状态栏
```
  //step 1: 在 res -> values -> themes.xml 中取消掉原生的ActionBar
        <!-- Base application theme. -->
        <style name="Theme.HMusic" parent="Theme.AppCompat.Light.NoActionBar">
  //step 2: 在onCreate中加入
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
  //step 3: 布局文件 activity_splash.xml 的根布局中加入
        android:fitsSystemWindows="true"
    
```
+ 设置线程更新倒计时秒数
4. 修复启动页在Dark模式下显示错误的Bug
## 2021-03-25
1. 添加ViewBinding支持，用途嘛，主要是用于View中控件id的自动绑定，**避免重复的findViewById**
```
  //在 Project -> app -> build.gradle中添加
  buildFeatures {
        viewBinding = true
  }
```
+ 至于为什么不使用ButterKnife或者kotlin-android-extensions之类的插件呢？郭婶的解释我放在[这里](https://blog.csdn.net/guolin_blog/article/details/113089706)了
2. 开启Dark模式
## 2021-03-24
1. 编写基础的BaseActivity
2. 利用ActivityCollector来管理应用的Activity
3. 初始化[HMusic](https://github.com/HunterLC/HMusic)的GitHub仓库