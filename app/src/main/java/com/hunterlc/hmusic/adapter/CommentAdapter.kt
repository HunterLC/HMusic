package com.hunterlc.hmusic.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.data.CommentInnerData
import com.hunterlc.hmusic.util.dp2px
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter: PagingDataAdapter<CommentInnerData, CommentAdapter.ViewHolder>(COMPARATOR) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<CommentInnerData>() {
            override fun areItemsTheSame(oldItem: CommentInnerData, newItem: CommentInnerData): Boolean {
                return oldItem.commentId == newItem.commentId
            }

            override fun areContentsTheSame(oldItem: CommentInnerData, newItem: CommentInnerData): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvLikedCount: TextView = itemView.findViewById(R.id.tvLikedCount)
        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hotComment = getItem(position)
        if (hotComment != null) {
            holder.tvName.text = hotComment.user.nickname
            holder.tvContent.text = hotComment.content
            holder.tvLikedCount.text = hotComment.likedCount.toString()
            holder.tvTime.text = convertLongToTime(hotComment.time)
            holder.ivCover.load(hotComment.user.avatarUrl) {
                transformations(RoundedCornersTransformation(dp2px(6f).toFloat()))
                size(ViewSizeResolver(holder.ivCover))
                error(R.drawable.ic_song_cover)
                crossfade(300)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertLongToTime (time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy/M/dd hh:mm:ss")
        return format.format(date)
    }

}