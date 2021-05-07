package com.hunterlc.hmusic.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.data.RecommendPlaylistData
import com.hunterlc.hmusic.ui.activity.PlaylistActivity
import com.hunterlc.hmusic.util.dp
import com.hunterlc.hmusic.util.dp2px

class PlaylistRecommendAdapter(private val playlistRecommendDataResult: ArrayList<RecommendPlaylistData>) : RecyclerView.Adapter<PlaylistRecommendAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clPlaylist: ConstraintLayout = view.findViewById(R.id.clPlaylist)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSub: TextView = view.findViewById(R.id.tvSub)
        val tvPlayCount: TextView = view.findViewById(R.id.tvPlayCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_my_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0 || position == 1) {
            val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            layoutParams.setMargins(dp2px(10f).toInt(), 0, 0, 0)
            holder.clPlaylist.layoutParams = layoutParams
        } else if (position == playlistRecommendDataResult.lastIndex
            || position == playlistRecommendDataResult.lastIndex - 1) {
            val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            layoutParams.setMargins(0, 0, 10.dp(), 0)
            holder.clPlaylist.layoutParams = layoutParams
        } else {
            val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            layoutParams.setMargins(0, 0, 0, 0)
            holder.clPlaylist.layoutParams = layoutParams
        }

        // 获取当前歌单
        val playlist = playlistRecommendDataResult[position]

        holder.ivCover.load(playlist.coverImgUrl) {
            size(ViewSizeResolver(holder.ivCover))
            transformations(RoundedCornersTransformation(dp2px(8f).toFloat()))
            crossfade(300)
        }
        holder.clPlaylist.setOnClickListener {
            val intent = Intent(it.context, PlaylistActivity::class.java)
            intent.putExtra(PlaylistActivity.EXTRA_LONG_PLAYLIST_ID, playlist.id)
            it.context.startActivity(intent)
        }
        holder.tvTitle.text = playlist.name
        holder.tvSub.text = when (playlist.subscribedCount) {
            in 1 until 10_000 -> playlist.subscribedCount.toString()
            in 10_000 until 100_000_000 -> "${playlist.subscribedCount / 10000} 万收藏"
            else -> "${playlist.subscribedCount / 100_000_000} 亿收藏"
        }

        holder.tvPlayCount.text = when (playlist.playCount) {
            in 1 until 10_000 -> playlist.playCount.toString()
            in 10_000 until 100_000_000 -> "${playlist.playCount / 10000} 万播放"
            else -> "${playlist.playCount / 100_000_000} 亿播放"
        }
    }

    override fun getItemCount(): Int {
        return playlistRecommendDataResult.size
    }

}