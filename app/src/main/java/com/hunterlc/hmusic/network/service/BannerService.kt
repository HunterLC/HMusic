package com.hunterlc.hmusic.network.service

import com.hunterlc.hmusic.data.BannerInfo
import retrofit2.Call
import retrofit2.http.GET

interface BannerService {
    @GET("/banner?type=1")  //1代表着Android
    fun getBanner(): Call<BannerInfo>
}