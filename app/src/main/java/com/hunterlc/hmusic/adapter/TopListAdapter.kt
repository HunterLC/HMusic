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
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.data.PlaylistData
import com.hunterlc.hmusic.ui.activity.PlaylistActivity

class TopListAdapter(private val topList: List<PlaylistData>) : RecyclerView.Adapter<TopListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clTrack: ConstraintLayout = view.findViewById(R.id.clTrack)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvTrackCount: TextView = view.findViewById(R.id.tvTrackCount)
        val tvPlayCount: TextView = view.findViewById(R.id.tvPlayCount)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvUpdateFrequency: TextView = view.findViewById(R.id.tvUpdateFrequency)
        val radius = view.context.resources.getDimension(R.dimen.defaultRadius)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_top_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val play = topList[position]
        with(holder){
            ivCover.load(play.coverImgUrl) {
                allowHardware(false)
                crossfade(true)
                size(ViewSizeResolver(ivCover))
                crossfade(300)
            }

            tvName.text = play.name
            tvTrackCount.text = holder.itemView.context.getString(R.string.songs, play.trackCount)
            tvDescription.text = play.description
            tvUpdateFrequency.text = play.updateFrequency
            tvPlayCount.text = when (play.playCount) {
                in 1 until 10_000 -> play.playCount.toString()
                in 10_000 until 100_000_000 -> "${play.playCount / 10000} 万播放"
                else -> "${play.playCount / 100_000_000} 亿播放"
            }

            clTrack.setOnClickListener {
                val intent = Intent(it.context, PlaylistActivity::class.java)
                intent.putExtra(PlaylistActivity.EXTRA_LONG_PLAYLIST_ID, play.id)
                it.context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return topList.size
    }

}