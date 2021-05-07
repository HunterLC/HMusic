package com.hunterlc.hmusic.adapter

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hunterlc.hmusic.MyApplication
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.util.BannerUtils

class BannerImageAdapter(imageUrls: List<String>) : BannerAdapter<String, BannerImageAdapter.ImageHolder>(imageUrls) {


    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ImageHolder {
        val imageView = ImageView(parent!!.context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        //通过裁剪实现圆角
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BannerUtils.setBannerRound(imageView, 20f)
        }
        return ImageHolder(imageView)
    }

    override fun onBindView(holder: ImageHolder?, data: String?, position: Int, size: Int) {
        Glide.with(holder!!.itemView)
            .load(data)
            .into(holder.imageView)
        holder.imageView.setOnClickListener {
            Toast.makeText(MyApplication.context,"还没有开发QAQ",Toast.LENGTH_SHORT).show()
        }
    }


    class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView

        init {
            imageView = view as ImageView
        }
    }

}