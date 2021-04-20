package com.hunterlc.hmusic.network.service

import com.hunterlc.hmusic.data.MvInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MVService {
    @GET("/mv/url")
    fun getMvUrl(@Query("id") id: Long): Call<MvInfo>
}