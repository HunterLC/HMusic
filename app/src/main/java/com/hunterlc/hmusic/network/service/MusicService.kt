package com.hunterlc.hmusic.network.service

import com.hunterlc.hmusic.data.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.URLEncoder

interface MusicService {
    //通过具体的歌单id获取里面包含的歌曲
    @GET("/playlist/detail")
    fun getMusicsByPlaylistId(@Query("id") id: Long): Call<DetailPlaylistData>

    //一次性获取多首歌的信息在传入的ids里面用,隔开歌的id
    @GET("/song/detail")
    fun getMusicDetail(@Query("ids") ids: String): Call<MusicData>

    //根据歌曲的id获取歌曲的URL,多首歌曲id用,拼接起来
    @GET("/song/url")
    fun getSongUrlById(@Query("id") id: String): Call<SongUrl>

    //根据歌曲id获取歌词
    @GET("/lyric")
    fun getLyricById(@Query("id") id: Long): Call<LyricInfo>

    //日推, 需登录
    @GET("/recommend/songs")
    fun getDailySongs( @Query("cookie") cookie: String): Call<DailySongsInfo>

   // val a = URLEncoder.encode()
}