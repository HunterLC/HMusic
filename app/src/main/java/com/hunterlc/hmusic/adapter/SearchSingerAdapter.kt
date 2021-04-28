package com.hunterlc.hmusic.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.data.SongsInnerData
import com.hunterlc.hmusic.ui.activity.PlayerActivity
import com.hunterlc.hmusic.util.ConfigUtil
import com.hunterlc.hmusic.util.dp2px
import com.hunterlc.hmusic.util.parse

class SearchSingerAdapter(
    private val itemMenuClickedListener: (SongsInnerData.ArtistsData) -> Unit
) : ListAdapter<SongsInnerData.ArtistsData, SearchSingerAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(view: View, itemMenuClickedListener: (SongsInnerData.ArtistsData) -> Unit) : RecyclerView.ViewHolder(view) {
        val clSong: ConstraintLayout = view.findViewById(R.id.clSong)
        val ivSinger: ImageView = view.findViewById(R.id.ivSinger)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvTransName: TextView = view.findViewById(R.id.tvTransName)
        val tvAlbumsNum: TextView = view.findViewById(R.id.tvAlbumsNum)
        val tvMVsNum: TextView = view.findViewById(R.id.tvMVsNum)

        val isAnimation = MyApplication.mmkv.decodeBool(ConfigUtil.PLAYLIST_SCROLL_ANIMATION, true)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_singer, parent, false).apply {
            return ViewHolder(this, itemMenuClickedListener)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val artist = getItem(position)
            // 动画
            if (isAnimation) {
                clSong.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recycle_item)
            }

            val imageUrl = artist.picUrl

            ivSinger.load(imageUrl) {
                transformations(RoundedCornersTransformation(dp2px(6f).toFloat()))
                size(ViewSizeResolver(ivSinger))
                error(R.drawable.ic_song_cover)
                crossfade(300)
            }

            tvName.text = artist.name
            tvTransName.text = artist.trans
            tvAlbumsNum.text = artist.albumSize.toString()
            tvMVsNum.text = artist.mvSize.toString()

            // 点击项目
            clSong.setOnClickListener {

            }
        }
    }


    object DiffCallback : DiffUtil.ItemCallback<SongsInnerData.ArtistsData>() {
        override fun areItemsTheSame(oldItem: SongsInnerData.ArtistsData, newItem: SongsInnerData.ArtistsData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SongsInnerData.ArtistsData, newItem: SongsInnerData.ArtistsData): Boolean {
            return oldItem == newItem
        }
    }
}