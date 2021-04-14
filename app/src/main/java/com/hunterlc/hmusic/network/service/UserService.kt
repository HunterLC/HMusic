package com.hunterlc.hmusic.network.service

import com.hunterlc.hmusic.data.UserDetailData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {
    @GET("/user/detail")
    fun getUserDetail(@Query("uid") uid: Long): Call<UserDetailData>

}