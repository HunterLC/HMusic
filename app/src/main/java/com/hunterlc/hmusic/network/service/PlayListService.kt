package com.hunterlc.hmusic.network.service

import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.data.*
import com.hunterlc.hmusic.network.creator.ServiceCreator
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

    //通过具体的歌单id获取里面包含的歌曲
    @GET("/playlist/track/all")
    fun getSongsByPlaylistId(@Query("id") id: Long, @Query("limit") limit: Int, @Query("offset") offset: Int): Call<DetailPlaylistData>

    companion object {
        fun create(): PlayListService {
            return ServiceCreator.create(PlayListService::class.java)
        }
    }

}