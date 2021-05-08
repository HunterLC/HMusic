package com.hunterlc.hmusic.network.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hunterlc.hmusic.data.CommentInfo
import com.hunterlc.hmusic.data.CommentInnerData
import com.hunterlc.hmusic.network.service.CommentService
import com.hunterlc.hmusic.util.LogUtil

class CommentPagingSource(private val commentService: CommentService, val type: Int, val id: Long, private val sortType: Int) : PagingSource<Int, CommentInnerData>() {

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

//https://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650255228&idx=1&sn=f73e49c6934d9e235e0692a2452f21cd&chksm=88635e13bf14d705c5a9a2c49b1aeac24abeb7fa38206afb089043f9869f099da982841f8bba&mpshare=1&scene=23&srcid=0419WX6ZqPpnveeF6P9BocLx&sharer_sharetime=1620401352716&sharer_shareid=c4b5eaddb5e17a674ed2f5d43d91b132#rd