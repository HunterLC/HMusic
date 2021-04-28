package com.hunterlc.hmusic.network.service

import com.hunterlc.hmusic.data.SearchDefaultInfo
import com.hunterlc.hmusic.data.SearchHotDetailInfo
import com.hunterlc.hmusic.data.SearchInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    //获取默认关键词
    @GET("/search/default")
    fun getSearchDefault(): Call<SearchDefaultInfo>

    //获取热搜详细列表
    @GET("/search/hot/detail")
    fun getSearchHotDetail(): Call<SearchHotDetailInfo>

    //搜索单曲、歌手名
    @GET("/cloudsearch")  //type: 搜索类型；默认为 1 即单曲 , 取值意义 : 1: 单曲, 10: 专辑, 100: 歌手, 1000: 歌单, 1002: 用户, 1004: MV, 1006: 歌词, 1009: 电台, 1014: 视频, 1018:综合
    fun search(@Query("keywords") keywords: String, @Query("type") type: Int): Call<SearchInfo>
}