package com.hunterlc.hmusic.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.data.CommentInnerData
import com.hunterlc.hmusic.network.repository.Repository
import kotlinx.coroutines.flow.Flow

class VideoViewModel: ViewModel() {
    var id = MutableLiveData<Long>().also {
        it.value = 0L
    }

    var videoPath = Transformations.switchMap(id) { id ->
        Repository.getMvUrl(id)
    }

    fun getMvUrl(id: Long) {
        this.id.value = id
    }

    fun getCommentsPagingData(type: Int, id: Long, sortType: Int): Flow<PagingData<CommentInnerData>> {
        return Repository.getCommentsPagingData(type, id, sortType).cachedIn(viewModelScope)
    }

}