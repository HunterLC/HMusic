package com.hunterlc.hmusic.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.data.PlaylistData
import com.hunterlc.hmusic.ui.activity.PlaylistActivity
import com.hunterlc.hmusic.ui.viewmodel.MainViewModel
import com.hunterlc.hmusic.util.GlideUtil
import com.hunterlc.hmusic.util.dp2px

/**
 * 我的歌单适配器
 */
class PlaylistAdapter(private val playlist: ArrayList<PlaylistData>, val activity: Activity) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clTrack: ConstraintLayout = view.findViewById(R.id.clTrack)
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvTrackCount: TextView = view.findViewById(R.id.tvTrackCount)

        val radius = view.context.resources.getDimension(R.dimen.defaultRadius)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_playlist, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val play = playlist[position]
        val url = "${play.coverImgUrl}?param=${dp2px(56F)}y${dp2px(56F)}"
        with(holder){
            ivCover.load(url) {
                allowHardware(false)
                crossfade(true)
                size(ViewSizeResolver(ivCover))
                //crossfade(300)
            }
            tvName.text = play.name
            tvTrackCount.text = holder.itemView.context.getString(R.string.songs, play.trackCount)
            clTrack.setOnClickListener {
                val intent = Intent(it.context, PlaylistActivity::class.java)
                intent.putExtra(PlaylistActivity.EXTRA_LONG_PLAYLIST_ID, play.id)
                it.context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return playlist.size
    }

}
