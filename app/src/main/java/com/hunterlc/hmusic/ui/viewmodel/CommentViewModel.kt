package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hunterlc.hmusic.data.CommentInnerData
import com.hunterlc.hmusic.network.repository.Repository
import kotlinx.coroutines.flow.Flow

class CommentViewModel: ViewModel() {
    fun getCommentsPagingData(type: Int, id: Long, sortType: Int): Flow<PagingData<CommentInnerData>> {
        return Repository.getCommentsPagingData(type, id, sortType).cachedIn(viewModelScope)
    }
}
