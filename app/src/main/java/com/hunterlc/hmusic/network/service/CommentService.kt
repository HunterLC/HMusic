package com.hunterlc.hmusic.network.service

import com.hunterlc.hmusic.data.CommentInfo
import com.hunterlc.hmusic.network.creator.ServiceCreator
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CommentService {

    /***
     *-----------必选
     *
    id : 资源 id, 如歌曲 id,mv id

    tpye: 数字 , 资源类型 , 对应歌曲 , mv, 专辑 , 歌单 , 电台, 视频对应以下类型

    0: 歌曲

    1: mv

    2: 歌单

    3: 专辑

    4: 电台

    5: 视频

    6: 动态

     ----------可选

     * pageNo:分页参数,第N页,默认为1

       pageSize:分页参数,每页多少条数据,默认20

       sortType: 排序方式,1:按推荐排序,2:按热度排序,3:按时间排序

       cursor: 当sortType为3时且页数不是第一页时需传入,值为上一条数据的time
     */
    @GET("/comment/new")
    suspend fun getComments(@Query("type") type: Int, @Query("id") id: Long, @Query("sortType") sortType: Int,@Query("pageSize") pageSize: Int, @Query("pageNo") pageNo: Int, @Query("cursor") cursor: String): CommentInfo

    companion object {
        fun create(): CommentService {
            return ServiceCreator.create(CommentService::class.java)
        }
    }
}