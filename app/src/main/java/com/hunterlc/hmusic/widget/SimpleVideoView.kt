package com.hunterlc.hmusic.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.util.LogUtil

class SimpleVideoView : RelativeLayout, View.OnClickListener {
    private var myContext: Context? = null
    private var mView: View? = null
    private var mVideoView //视频控件
            : VideoView? = null
    private var mBigPlayBtn //大的播放按钮
            : ImageView? = null
    private var mPlayBtn //播放按钮
            : ImageView? = null
    private var mBackBtn: ImageView? = null //返回按钮
    private var mTvTitleBar: TextView? = null //标题
    private var mFullScreenBtn //全屏按钮
            : ImageView? = null
    private var mPlayProgressBar //播放进度条
            : SeekBar? = null
    private var mPlayTime //播放时间
            : TextView? = null
    private var mControlPanel: LinearLayout? = null
    private var mTitleBar: LinearLayout? = null
    private var mVideoUri: Uri? = null
    private var outAnima //控制面板出入动画
            : Animation? = null
    private var inAnima //控制面板出入动画
            : Animation? = null
    private var outTopAnima //顶面板出入动画
            : Animation? = null
    private var inTopAnima //顶面板出入动画
            : Animation? = null
    private var mVideoDuration = 0 //视频毫秒数 = 0
    private var mCurrentProgress = 0 //毫秒数 = 0
    private var mUpdateTask: Runnable? = null
    private var mUpdateThread: Thread? = null
    private val UPDATE_PROGRESS = 0
    private val EXIT_CONTROL_PANEL = 1
    private var stopThread = true //停止更新进度线程标志
    private val screenSize = Point() //屏幕大小

    //判断是否为全屏状态
    var isFullScreen = false //是否全屏标志
        private set
    private var mWidth = 0//控件的宽度 = 0
    private var mHeight = 0//控件的高度 = 0

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private val myHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                UPDATE_PROGRESS -> {
                    mPlayProgressBar!!.progress = mCurrentProgress
                    setPlayTime(mCurrentProgress)
                }
                EXIT_CONTROL_PANEL ->                     //执行退出动画
                    if (mControlPanel!!.visibility != View.GONE && mTitleBar!!.visibility == View.GONE) {
                        mControlPanel!!.startAnimation(outAnima)
                        mTitleBar!!.startAnimation(outTopAnima)
                        mControlPanel!!.visibility = View.GONE
                        mTitleBar!!.visibility = View.GONE
                    }
            }
        }
    }

    //初始化控件
    private fun init(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) {
        this.myContext = context
        mView = LayoutInflater.from(context).inflate(R.layout.layout_video_view, this)
        mBigPlayBtn =
            mView!!.findViewById<View>(R.id.big_play_button) as ImageView
        mPlayBtn =
            mView!!.findViewById<View>(R.id.play_button) as ImageView
        mFullScreenBtn =
            mView!!.findViewById<View>(R.id.full_screen_button) as ImageView
        mBackBtn = mView!!.findViewById<View>(R.id.btnBack) as ImageView
        mPlayProgressBar = mView!!.findViewById<View>(R.id.progress_bar) as SeekBar
        mPlayTime = mView!!.findViewById<View>(R.id.time) as TextView
        mTvTitleBar = mView!!.findViewById<View>(R.id.tvTitleBar) as TextView
        mControlPanel = mView!!.findViewById<View>(R.id.control_panel) as LinearLayout
        mTitleBar = mView!!.findViewById<View>(R.id.bar_control_panel) as LinearLayout
        mVideoView = mView!!.findViewById<View>(R.id.video_view) as VideoView
        //获取屏幕大小
        (context as Activity).windowManager.defaultDisplay.getSize(screenSize)
        //加载动画
        outAnima =
            AnimationUtils.loadAnimation(context, R.anim.exit_from_bottom)
        inAnima =
            AnimationUtils.loadAnimation(context, R.anim.enter_from_bottom)

        outTopAnima =
            AnimationUtils.loadAnimation(context, R.anim.exit_from_top)
        inTopAnima =
            AnimationUtils.loadAnimation(context, R.anim.enter_from_top)
        //设置控制面板初始不可见
        mControlPanel!!.visibility = View.GONE
        mTitleBar!!.visibility = View.GONE
        //设置大的播放按钮可见
        mBigPlayBtn!!.visibility = View.VISIBLE
        //设置媒体控制器
//		mMediaController = new MediaController(context);
//		mMediaController.setVisibility(View.GONE);
//		mVideoView.setMediaController(mMediaController);
        mVideoView!!.setOnPreparedListener { //视频加载完成后才能获取视频时长
            initVideo()
        }
        //视频播放完成监听器
        mVideoView!!.setOnCompletionListener {
            mPlayBtn!!.setImageResource(R.drawable.ic_play)
            mVideoView!!.seekTo(0)
            mPlayProgressBar!!.progress = 0
            setPlayTime(0)
            stopThread = true
            sendHideControlPanelMessage()
        }
        mView!!.setOnClickListener(this)
    }

    //初始化视频，设置视频时间和进度条最大值
    @SuppressLint("SetTextI18n")
    private fun initVideo() {
        //初始化时间和进度条
        mVideoDuration = mVideoView!!.duration //毫秒数
        val seconds = mVideoDuration / 1000
        mPlayTime!!.text = "00:00/" +
                (if (seconds / 60 > 9) seconds / 60 else "0" + seconds / 60) + ":" +
                if (seconds % 60 > 9) seconds % 60 else "0" + seconds % 60
        mPlayProgressBar!!.max = mVideoDuration
        mPlayProgressBar!!.progress = 0
        //更新进度条和时间任务
        mUpdateTask = Runnable {
            while (!stopThread) {
                mCurrentProgress = mVideoView!!.currentPosition
                myHandler.sendEmptyMessage(UPDATE_PROGRESS)
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        mBigPlayBtn!!.setOnClickListener(this)
        mPlayBtn!!.setOnClickListener(this)
        mFullScreenBtn!!.setOnClickListener(this)
        mBackBtn!!.setOnClickListener(this)
        //进度条进度改变监听器
        mPlayProgressBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                myHandler.sendEmptyMessageDelayed(EXIT_CONTROL_PANEL, 3000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                myHandler.removeMessages(EXIT_CONTROL_PANEL)
            }

            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mVideoView!!.seekTo(progress) //设置视频
                    setPlayTime(progress) //设置时间
                }
            }
        })
        mWidth = this.width
        mHeight = this.height
    }

    override fun onClick(v: View) {
        if (v === mView) {
            if (mBigPlayBtn!!.visibility == View.VISIBLE) {
                return
            }
            if (mControlPanel!!.visibility == View.VISIBLE && mTitleBar!!.visibility == View.VISIBLE) {
                //执行退出动画
                mControlPanel!!.startAnimation(outAnima)
                mTitleBar!!.startAnimation(outTopAnima)
                mControlPanel!!.visibility = View.GONE
                mTitleBar!!.visibility = View.GONE
            } else {
                //执行进入动画
                mControlPanel!!.startAnimation(inAnima)
                mTitleBar!!.startAnimation(inTopAnima)
                mControlPanel!!.visibility = View.VISIBLE
                mTitleBar!!.visibility = View.VISIBLE
                sendHideControlPanelMessage()
            }
        } else if (v.id == R.id.big_play_button) { //大的播放按钮
            mBigPlayBtn!!.visibility = View.GONE
            mVideoView!!.background = null
            if (!mVideoView!!.isPlaying) {
                mVideoView!!.start()
                mPlayBtn!!.setImageResource(R.drawable.ic_play)
                //开始更新进度线程
                mUpdateThread = Thread(mUpdateTask)
                stopThread = false
                mUpdateThread!!.start()
            }
        } else if (v.id == R.id.play_button) { //播放/暂停按钮
            if (mVideoView!!.isPlaying) {
                mVideoView!!.pause()
                mPlayBtn!!.setImageResource(R.drawable.ic_pause)
            } else {
                if (mUpdateThread == null || !mUpdateThread!!.isAlive) {
                    //开始更新进度线程
                    mUpdateThread = Thread(mUpdateTask)
                    stopThread = false
                    mUpdateThread!!.start()
                }
                mVideoView!!.start()
                mPlayBtn!!.setImageResource(R.drawable.ic_play)
            }
            sendHideControlPanelMessage()
        } else if (v.id == R.id.full_screen_button) { //全屏
            if (myContext!!.resources.configuration.orientation
                == Configuration.ORIENTATION_PORTRAIT
            ) {
                setFullScreen()
            } else {
                setNoFullScreen()
            }
            sendHideControlPanelMessage()
        } else if (v.id == R.id.btnBack) {
            if (myContext!!.resources.configuration.orientation
                == Configuration.ORIENTATION_PORTRAIT
            ) {
                val activity = myContext as Activity
                activity.finish()
            } else {
                setNoFullScreen()
            }
            sendHideControlPanelMessage()
        }

    }

    //设置当前时间
    private fun setPlayTime(millisSecond: Int) {
        val currentSecond = millisSecond / 1000
        val currentTime =
            (if (currentSecond / 60 > 9) (currentSecond / 60).toString() + "" else "0" + currentSecond / 60) + ":" +
                    if (currentSecond % 60 > 9) (currentSecond % 60).toString() + "" else "0" + currentSecond % 60
        val text = StringBuilder(mPlayTime!!.text.toString())
        text.replace(0, text.indexOf("/"), currentTime)
        mPlayTime!!.text = text
    }

    //设置控件的宽高
    private fun setSize() {
        val lp = this.layoutParams
        if (isFullScreen) {
            lp.width = screenSize.y
            lp.height = screenSize.x
        } else {
            lp.width = mWidth
            lp.height = mHeight
        }
        this.layoutParams = lp
    }

    //两秒后隐藏控制面板
    private fun sendHideControlPanelMessage() {
        myHandler.removeMessages(EXIT_CONTROL_PANEL)
        myHandler.sendEmptyMessageDelayed(EXIT_CONTROL_PANEL, 3000)
    }

    //设置视频路径
    //获取视频路径
    var videoUri: Uri?
        get() = mVideoUri
        set(uri) {
            mVideoUri = uri
            mVideoView!!.setVideoURI(mVideoUri)
        }

    fun setTitleBar(name: String, color: Int){
        mTvTitleBar!!.text = name
        mTvTitleBar!!.setTextColor(color)
        mBackBtn!!.setColorFilter(color)
    }

    //设置视频初始画面
    fun setInitPicture(d: Drawable?) {
        mVideoView!!.background = d
    }

    //挂起视频
    fun suspend() {
        if (mVideoView != null) {
            mVideoView!!.suspend()
        }
    }

    //设置视频进度
    fun setVideoProgress(millisSecond: Int, isPlaying: Boolean) {
        mVideoView!!.background = null
        mBigPlayBtn!!.visibility = View.GONE
        mPlayProgressBar!!.progress = millisSecond
        setPlayTime(millisSecond)
        if (mUpdateThread == null || !mUpdateThread!!.isAlive) {
            mUpdateThread = Thread(mUpdateTask)
            stopThread = false
            mUpdateThread!!.start()
        }
        mVideoView!!.seekTo(millisSecond)
        if (isPlaying) {
            mVideoView!!.start()
            mPlayBtn!!.setImageResource(R.drawable.ic_play)
        } else {
            mVideoView!!.pause()
            mPlayBtn!!.setImageResource(R.drawable.ic_pause)
        }
    }

    //获取视频进度
    val videoProgress: Int
        get() = mVideoView!!.currentPosition

    //判断视频是否正在播放
    val isPlaying: Boolean
        get() = mVideoView!!.isPlaying

    //设置竖屏
    fun setNoFullScreen() {
        isFullScreen = false
        val ac = myContext as Activity?
        ac!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ac.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setSize()
    }

    //设置横屏
    private fun setFullScreen() {
        isFullScreen = true
        val ac = myContext as Activity?
        ac!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        ac.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setSize()
    }

    //判断目前状态是不是横屏
    fun getFullScreenState(): Boolean{
        return isFullScreen
    }
}