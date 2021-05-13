package com.hunterlc.hmusic.network

import com.hunterlc.hmusic.network.creator.ServiceCreator
import com.hunterlc.hmusic.network.service.*
import com.hunterlc.hmusic.util.LogUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.RuntimeException
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/***
 * 统一的网络数据源访问入口
 */
object CloudMusicNetwork {
    //创建动态代理对象
    private val playListService = ServiceCreator.create(PlayListService::class.java)
    private val loginService = ServiceCreator.create(LoginService::class.java)
    private val musicService = ServiceCreator.create(MusicService::class.java)
    private val userService = ServiceCreator.create(UserService::class.java)
    private val bannerService = ServiceCreator.create(BannerService::class.java)
    private val mvService = ServiceCreator.create(MVService::class.java)
    private val searchService = ServiceCreator.create(SearchService::class.java)

    //获取用户歌单列表
    suspend fun getUserPlayList(uid: Long) = playListService.getUserPlayList(uid).await()
    //登录并获取用户信息
    suspend fun loginByCellphone(phone: String, countrycode: Int, md5_password: String) = loginService.loginByCellphone(phone, countrycode, md5_password).await()
    //根据歌单id获取其中所有的歌曲（因为接口原因，这里只能获取所有歌的id）
    suspend fun getMusicsByPlaylistId(id: Long) = musicService.getMusicsByPlaylistId(id).await()
    //根据歌曲id获取其中所有的歌曲的信息
    suspend fun getMusicDetail(ids: String) = musicService.getMusicDetail(ids).await()
    //根据歌曲id获取其播放地址URL
    suspend fun getSongUrlById(id: String) = musicService.getSongUrlById(id).await()
    //根据歌曲id获取歌词
    suspend fun getLyricById(id: Long) = musicService.getLyricById(id).await()
    //根据id获取用户信息
    suspend fun getUserDetail(uid: Long) = userService.getUserDetail(uid).await()
    //获取首页的Banner
    suspend fun getBanner() = bannerService.getBanner().await()
    //获取MV
    suspend fun getMvUrl(id: Long) = mvService.getMvUrl(id).await()
    //获取默认搜索关键词
    suspend fun getSearchDefault() = searchService.getSearchDefault().await()
    //获取热搜详细列表
    suspend fun getSearchHotDetail() = searchService.getSearchHotDetail().await()
    //搜索单曲、歌手名
    suspend fun search(keywords: String, type: Int) = searchService.search(keywords, type).await()
    //获取推荐歌单
    suspend fun getRecommendPlaylist() = playListService.getRecommendPlaylist().await()
    //获取榜单
    suspend fun getToplist() = playListService.getToplist().await()
    //日推, 需登录
    suspend fun getDailySongs(cookie: String) = musicService.getDailySongs(cookie).await()
    //日推歌单, 需登录
    suspend fun getRecommendDaily(cookie: String) = playListService.getRecommendDaily(cookie).await()


    //借助协程技术
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    //响应成功
                    LogUtil.e("CloudMusicNetwork","响应成功")
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    //响应失败
                    LogUtil.e("CloudMusicNetwork","响应失败")
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}