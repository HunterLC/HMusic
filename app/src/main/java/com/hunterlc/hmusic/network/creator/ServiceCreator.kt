package com.hunterlc.hmusic.network.creator

import com.hunterlc.hmusic.api.API_MY_VERVEL
import com.hunterlc.hmusic.network.service.PlayListService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
    private val retrofit = Retrofit.Builder()
        .baseUrl(API_MY_VERVEL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

}