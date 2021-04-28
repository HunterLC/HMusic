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

class SearchSongAdapter(
    private val itemMenuClickedListener: (SongsInnerData) -> Unit
) : ListAdapter<SongsInnerData, SearchSongAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(view: View, itemMenuClickedListener: (SongsInnerData) -> Unit) : RecyclerView.ViewHolder(view) {
        val clSong: ConstraintLayout = view.findViewById(R.id.clSong)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSub: TextView = view.findViewById(R.id.tvSub)
        private val ivMenu: ImageView = view.findViewById(R.id.ivMenu)
        val ivTag: ImageView = view.findViewById(R.id.ivTag)

        val isAnimation = MyApplication.mmkv.decodeBool(ConfigUtil.PLAYLIST_SCROLL_ANIMATION, true)

        var songData: SongsInnerData? = null

        init {
            ivMenu.setOnClickListener {
                songData?.let { it1 -> itemMenuClickedListener(it1) }
            }
            clSong.setOnLongClickListener {
                songData?.let { it1 -> itemMenuClickedListener(it1) }
                return@setOnLongClickListener true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_song, parent, false).apply {
            return ViewHolder(this, itemMenuClickedListener)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val song = getItem(position)
            songData = song
            // 动画
            if (isAnimation) {
                clSong.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recycle_item)
            }

            holder.tvTitle.alpha = 1f
            holder.tvSub.alpha = 1f
            holder.ivTag.visibility = View.VISIBLE

//            if (song.neteaseInfo?.pl == 0) {
//                holder.tvTitle.alpha = 0.25f
//                holder.tvSub.alpha = 0.25f
//            } else {
//                holder.tvTitle.alpha = 1f
//                holder.tvSub.alpha = 1f
//            }

//            if (song.quality() == SONG_QUALITY_HQ) {
//                holder.ivTag.visibility = View.VISIBLE
//            } else {
//                holder.ivTag.visibility = View.GONE
//            }

            val imageUrl = song.album.picUrl

            ivCover.load(imageUrl) {
                transformations(RoundedCornersTransformation(dp2px(6f).toFloat()))
                size(ViewSizeResolver(ivCover))
                error(R.drawable.ic_song_cover)
                crossfade(300)
            }

            tvTitle.text = song.name
            val artist = song.artists.parse()
            tvSub.text = if (artist.isNullOrEmpty()) {
                "未知"
            } else {
                artist
            }
            // 点击项目
            clSong.setOnClickListener {
                playMusic(it.context, song, currentList.toList() as ArrayList)
//                if (song.neteaseInfo?.pl != 0) {
//                    playMusic(it.context, song, currentList.toList() as ArrayList)
//                } else {
//                    toast("网易云暂无版权或者是 VIP 歌曲，可以试试 QQ 音源")
//                }
            }
        }
    }

    /**
     * 播放第一首歌
     */
    fun playFirst() {
        playMusic( null, getItem(0), currentList.toList() as ArrayList)
    }

    fun playMusic(context: Context?, song: SongsInnerData, songList: ArrayList<SongsInnerData>) {
        // 获取 position
        val position = if (songList.indexOf(song) == -1) {
            0
        } else {
            songList.indexOf(song)
        }
        // 歌单相同
        if (MyApplication.musicController.value?.getPlaylist() == songList) {
            // position 相同
            if (position == MyApplication.musicController.value?.getNowPosition()) {
                if (context != null) {
                    context.startActivity(Intent(context, PlayerActivity::class.java))
                    (context as Activity).overridePendingTransition(
                        R.anim.anim_slide_enter_bottom,
                        R.anim.anim_no_anim
                    )
                }
            } else {
                MyApplication.musicController.value?.playMusic(song)
            }
        } else {
            // 设置歌单
            MyApplication.musicController.value?.setPlaylist(songList)
            // 播放歌单
            MyApplication.musicController.value?.playMusic(song)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<SongsInnerData>() {
        override fun areItemsTheSame(oldItem: SongsInnerData, newItem: SongsInnerData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SongsInnerData, newItem: SongsInnerData): Boolean {
            return oldItem == newItem
        }
    }

}