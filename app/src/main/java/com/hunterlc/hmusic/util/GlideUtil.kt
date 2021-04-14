package com.hunterlc.hmusic.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.transition.Transition
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.*
import com.bumptech.glide.request.target.Target
import com.hunterlc.hmusic.MyApplication

object GlideUtil {

    fun load(url: String, imageView: ImageView) {
        runOnMainThread {
            Glide.with(MyApplication.context)
                .asBitmap()
                .override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(imageView)
        }
    }

    fun load(url: String, imageView: ImageView, size: Int) {
        runOnMainThread {
            Glide.with(MyApplication.context)
                .asBitmap()
                .override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .load(url)
                .override(size)
                .into(imageView)
        }
    }

    fun load(url: String, imageView: ImageView, placeHolder: Drawable) {
        runOnMainThread {
            Glide.with(MyApplication.context)
                .asBitmap()
                .override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .load(url)
                .placeholder(placeHolder)
                .into(imageView)
        }
    }

    fun load(url: String, imageView: ImageView, placeHolderImageView: ImageView) {
        if (placeHolderImageView.drawable != null) {
            Glide.with(MyApplication.context)
                .asBitmap()
                .override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .load(url)
                .placeholder(placeHolderImageView.drawable)
                .into(imageView)
        } else {
            Glide.with(MyApplication.context)
                .asBitmap()
                .override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .load(url)
                .into(imageView)
        }
    }

    fun load(url: String, success: (Bitmap) -> Unit) {
        Glide.with(MyApplication.context)
            .asBitmap()
            .load(url)
            .override(480)
            .apply(RequestOptions().centerCrop())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    LogUtil.d("获取成功","...")
                    success.invoke(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    @Deprecated("过时方法")
    fun loadCloudMusicImage(url: String, width: Int, height: Int, imageView: ImageView) {
        val imageUrl = "$url?param=${width}y${height}"
        load(imageUrl, imageView)
    }

    /**
     * 加载圆形图片
     */
    fun loadCircle(url: String, imageView: ImageView, needSize: Int) {
        Glide.with(MyApplication.context)
            .load(url)
            .override(needSize)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(imageView)
    }

}