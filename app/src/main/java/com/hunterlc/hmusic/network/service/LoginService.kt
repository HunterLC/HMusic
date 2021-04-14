package com.hunterlc.hmusic.network.service

import com.hunterlc.hmusic.data.UserDetailData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginService {

    @POST("/login/cellphone")
    fun loginByCellphone(@Query("phone") phone: String, @Query("countrycode") countrycode: Int, @Query("password") password: String): Call<UserDetailData>
}