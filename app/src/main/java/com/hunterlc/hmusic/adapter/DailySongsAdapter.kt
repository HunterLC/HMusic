package com.hunterlc.hmusic.adapter

import android.annotation.SuppressLint
import android.app.Activity
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
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.data.SongsInnerData
import com.hunterlc.hmusic.ui.activity.PlayerActivity
import com.hunterlc.hmusic.ui.activity.VideoActivity
import com.hunterlc.hmusic.util.ConfigUtil
import com.hunterlc.hmusic.widget.ScrollTextView

class DailySongsAdapter
@JvmOverloads
constructor(private val activity: Activity): ListAdapter<SongsInnerData, DailySongsAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val clSong: ConstraintLayout = view.findViewById(R.id.clSong)

        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvArtist: TextView = view.findViewById(R.id.tvArtist)
        val ivMore: ImageView = view.findViewById(R.id.ivMore)
        val ivHq: ImageView = view.findViewById(R.id.ivHq)
        val ivMv: ImageView = view.findViewById(R.id.ivMv)
        val tvReason: TextView = view.findViewById(R.id.tvReason)

        val isAnimation = MyApplication.mmkv.decodeBool(ConfigUtil.PLAYLIST_SCROLL_ANIMATION, true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_daily_songs_item, parent, false).apply {
            return ViewHolder(this)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            // ??????
            if (isAnimation) {
                clSong.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recycle_item)
            }

            if (position > itemCount) {
                return
            }

            val song = getItem(position)

            tvNumber.text = (position + 1).toString()
            tvName.text = song.name //  + song.neteaseInfo?.pl?.toString()
            tvReason.text = song.reason
            val artists = song.artists
            if (artists.isNullOrEmpty()) {
                tvArtist.text = "??????"
            } else {
                var name = ""
                for (artist in artists){
                    name = name + artist.name +"/"
                }
                tvArtist.text = name.take(name.length - 1)
            }
            if (song.mv != 0.toLong()){
                ivMv.visibility = View.VISIBLE
                ivMv.setOnClickListener {
                    val intent = Intent(activity, VideoActivity::class.java)
                    intent.putExtra("mv",song.mv)
                    intent.putExtra("name",song.name)
                    activity.startActivity(intent)
                }
            } else {
                ivMv.visibility = View.INVISIBLE
            }
            ivCover.load(song.album.picUrl) {
                allowHardware(false)
                crossfade(true)
                size(ViewSizeResolver(ivCover))
                //crossfade(300)
            }
            // ????????????
            clSong.setOnClickListener {
                playMusic(song, it)
            }

            // ?????????????????????????????????????????????
            ivMore.setOnClickListener {

            }
            // ??????
            clSong.setOnLongClickListener {
                return@setOnLongClickListener true
            }
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
    private fun showSongMenuDialog(songData: SongsInnerData, view: View) {

    }

    /**
     * ??????????????????
     */
    fun playFirst() {
        playMusic(getItem(0), null)
    }

    /**
     * ????????????
     */
    private fun playMusic(songData: SongsInnerData, view: View?) {
        // ????????????
        if (MyApplication.musicController.value?.getPlaylist() == currentList) {
            // position ??????
            if (songData == MyApplication.musicController.value?.getPlayingSongData()?.value) {
                if (view != null) {
                    view.context.startActivity(Intent(view.context, PlayerActivity::class.java))
                    (view.context as Activity).overridePendingTransition(
                        R.anim.anim_slide_enter_bottom,
                        R.anim.anim_no_anim
                    )
                }
            } else {
                MyApplication.musicController.value?.playMusic(songData)
            }
        } else {
            // ????????????
            MyApplication.musicController.value?.setPlaylist(currentList.toList() as ArrayList<SongsInnerData>)
            // ????????????
            MyApplication.musicController.value?.playMusic(songData)
        }
    }

}