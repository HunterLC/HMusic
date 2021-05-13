package com.hunterlc.hmusic.network.service

import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.data.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayListService {
    //根据UID获取用户的歌单列表
    @GET("/user/playlist")
    fun getUserPlayList( @Query("uid") uid: Long): Call<UserPlaylistData>

    //推荐歌单
    @GET("/top/playlist/highquality")
    fun getRecommendPlaylist(): Call<RecommendPlaylistInfo>

    //日推歌单, 需登录
    @GET("/recommend/resource")
    fun getRecommendDaily( @Query("cookie") cookie: String): Call<DailyPlaylistInfo>

    //榜单
    @GET("/toplist")
    fun getToplist(): Call<ToplistInfo>

}