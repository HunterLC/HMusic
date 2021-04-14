package com.hunterlc.hmusic.network.service

import com.hunterlc.hmusic.data.UserPlaylistData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayListService {
    //根据UID获取用户的歌单列表
    @GET("/user/playlist")
    fun getUserPlayList( @Query("uid") uid: Long): Call<UserPlaylistData>
}