package com.hunterlc.hmusic.network.service

import com.hunterlc.hmusic.data.SearchDefaultInfo
import retrofit2.Call
import retrofit2.http.GET

interface SearchService {
    //获取默认关键词
    @GET("/search/default")
    fun getSearchDefault(): Call<SearchDefaultInfo>
}