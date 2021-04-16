# HMusic
this is a simple music demo for practicing

# 写在最前面的话
+ 我是菜鸡
+ 开发进度
- [x] 启动页
- [x] 登录页
- [x] 主页面

# 更新日志
## 2021-04-16
1. 适配播放界面横屏问题，方案是添加 **layout-land**布局文件夹，当系统横屏时会自动调用该文件夹的同名布局文件
2. 解决横屏状态下View不浸入状态栏区域
+ 方案一：在activity中设置
```
WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            activity.getWindow().setAttributes(lp);
```
+ 方案二：在themes.xml(之前的工程是style.xml)中你**当前界面使用的主题**中添加如下声明
```
<item name="android:windowLayoutInDisplayCutoutMode">shortEdges</item><item name="android:windowLayoutInDisplayCutoutMode">shortEdges</item>
```
+ Android P版本为我们提供了解决方法，我们可以通过对Window设置layoutInDisplayCutoutMode来达到我们的目的，先来看一下layoutInDisplayCutoutMode的几种属性：

> LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT：默认情况下，全屏窗口不会使用到刘海区域，非全屏窗口可正常使用刘海区
>
> LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER：窗口不允许和刘海屏重叠
>
> LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES：该窗口始终允许延伸到屏幕短边上的DisplayCutout区域。
+ 几个设置沉浸式状态栏的基础属性：

> View.SYSTEM_UI_FLAG_LOW_PROFILE：低调模式, 会隐藏不重要的状态栏图标；
>
> View.SYSTEM_UI_FLAG_HIDE_NAVIGATION：隐藏导航栏；
>
> View.SYSTEM_UI_FLAG_FULLSCREEN：状态栏隐藏（高度不变）；
>
> View.SYSTEM_UI_FLAG_LAYOUT_STABLE：保持整个View稳定, 常和控制System UI悬浮, 隐藏的Flags共用, 使View不会因为System UI的变化而重新layout；
>
> View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION：视图延伸至导航栏区域，导航栏覆盖在视图之上（在style中设置windowTranslucentNavigation）；
>
> View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：视图延伸至状态栏区域，状态栏覆盖在视图之上（在style中设置windowTranslucentStatus）；
>
> View.SYSTEM_UI_FLAG_IMMERSIVE：配合2或3同时使用，假设同时设置了2和7，状态栏隐藏，此时在状态栏顶部下滑，系统清除2设置，重新唤出状态栏，导航栏同理，滑动方向为由下至上；
>
> View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY：作用与7相似，但是设置并未被清楚，所以状态栏与导航栏在被唤出3s后或再次点击时，再次隐藏。

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