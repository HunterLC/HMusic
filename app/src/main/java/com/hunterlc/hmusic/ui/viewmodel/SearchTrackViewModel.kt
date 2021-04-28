package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hunterlc.hmusic.network.repository.Repository

class SearchTrackViewModel: ViewModel() {
    inner class Search(val keywords: String, val type: Int){
    }
    private val searchChangeLiveData = MutableLiveData<Search>()

    var searchLiveData = Transformations.switchMap(searchChangeLiveData) { search ->
        Repository.search(search.keywords,search.type)
    }

    fun search(keywords: String, type: Int){
        searchChangeLiveData.value = Search(keywords, type)
    }
}