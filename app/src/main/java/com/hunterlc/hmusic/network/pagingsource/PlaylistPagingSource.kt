package com.hunterlc.hmusic.network.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hunterlc.hmusic.data.CommentInnerData
import com.hunterlc.hmusic.network.service.CommentService
import com.hunterlc.hmusic.util.LogUtil

class PlaylistPagingSource (private val commentService: CommentService, val type: Int, val id: Long, private val sortType: Int) : PagingSource<Int, CommentInnerData>() {

    private lateinit var currentCursor: String  //最新的检索下表

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentInnerData> {
        LogUtil.e("getCommentsPagingData","正在调用")
        return try {
            val pageNo = params.key ?: 1 // set page 1 as default
            val pageSize = params.loadSize
            LogUtil.e("getCommentsPagingData",pageSize.toString())
            val commentResponse = if (sortType == 3 && pageNo > 1){
                commentService.getComments(type,id,sortType, pageSize, pageNo, currentCursor )
            } else{
                commentService.getComments(type,id,sortType, pageSize, pageNo,"")
            }
            val commentItems = commentResponse.data.comments
            currentCursor = commentResponse.data.cursor
            val prevKey = if (pageNo > 1) pageNo - 1 else null
            val nextKey = if (commentItems.isNotEmpty()) pageNo + 1 else null
            LoadResult.Page(commentItems, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CommentInnerData>): Int? = null

}