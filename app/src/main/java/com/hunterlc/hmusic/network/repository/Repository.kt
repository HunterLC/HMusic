package com.hunterlc.hmusic.network.repository

import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hunterlc.hmusic.data.CommentInnerData
import com.hunterlc.hmusic.network.CloudMusicNetwork
import com.hunterlc.hmusic.network.pagingsource.CommentPagingSource
import com.hunterlc.hmusic.network.service.CommentService
import com.hunterlc.hmusic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

/***
 * 仓库类，统一封装网络请求方法，在ViewModel中调用即可
 */
object Repository {

    /***
     * 获取用户的歌单列表
     */
    fun getUserPlayList(uid: Long) = fire(Dispatchers.IO) {
        LogUtil.e("repo","bbbba1")
        val userPlaylistData = CloudMusicNetwork.getUserPlayList(uid)

        if (userPlaylistData.playlist != null) {
            LogUtil.e("Repository-getUserPlayList","获取用户歌单列表成功")
            Result.success(userPlaylistData.playlist)
        } else {
            LogUtil.e("Repository-getUserPlayList","用户歌单列表为null")
            Result.failure(RuntimeException("response of playlist is null"))
        }
    }

    /***
     * 通过手机号码登录网易云音乐
     */
    fun loginByCellphone(phone: String, countrycode: Int, md5_password: String) = fire(Dispatchers.IO) {
        val userDetailData = CloudMusicNetwork.loginByCellphone(phone, countrycode, md5_password)

        if (userDetailData != null){
            LogUtil.e("Repository-loginByCellphone","登录并获取用户信息")
            Result.success(userDetailData)
        } else {
            LogUtil.e("Repository-loginByCellphone","登录并获取用户信息为null")
            Result.failure(RuntimeException("response of user info is null"))
        }
    }

    /***
     * 根据歌单id获取其中所有的歌曲（因为接口原因，这里只能获取所有歌的id）
     */
    fun getMusicsByPlaylistId(id: Long) = fire(Dispatchers.IO){
        val detailPlaylistData = CloudMusicNetwork.getMusicsByPlaylistId(id)

        if (detailPlaylistData.code == 200){
            LogUtil.e("Repository-getMusicsByPlaylistId","获取歌单详情成功")
            Result.success(detailPlaylistData.playlist)
        } else {
            LogUtil.e("Repository-getMusicsByPlaylistId","获取歌单详情失败")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 根据歌曲id获取其中所有的歌曲的信息
     */
    fun getMusicDetail(ids: String) = fire(Dispatchers.IO){
        val musicData = CloudMusicNetwork.getMusicDetail(ids)

        if (musicData.code == 200){
            LogUtil.e("Repository-getMusicDetail","根据歌曲id获取其中所有的歌曲的信息成功")
            Result.success(musicData)
        }else {
            LogUtil.e("Repository-getMusicDetail","根据歌曲id获取其中所有的歌曲的信息失败")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 根据歌曲id获取其播放地址URL
     */
    fun getSongUrlById(id: String) = fire(Dispatchers.IO){
        val songUrl = CloudMusicNetwork.getSongUrlById(id)
        if (songUrl.code == 200){
            LogUtil.e("Repository-getSongUrlById","根据歌曲id获取其播放地址URL成功")
            //检测部分：部分用户反馈获取的 url 会 403,hwaphon找到的解决方案是当获取到音乐的 id 后，将 https://music.163.com/song/media/outer/url?id=id.mp3 以 src 赋予 Audio 即可
            for (songData in songUrl.data){
                if (songData.url == "")
                    songData.url = "https://music.163.com/song/media/outer/url?id=${songData.id}.mp3"
            }
            Result.success(songUrl.data)
        } else {
            LogUtil.e("Repository-getSongUrlById","根据歌曲id获取其播放地址URL失败")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 根据歌曲id获得歌词
     */
    fun getLyricById(id: Long) = fire(Dispatchers.IO){
        val lyricInfo = CloudMusicNetwork.getLyricById(id)

        if (lyricInfo.code == 200){
            LogUtil.e("Repository-getLyricById","根据歌曲id获得歌词成功")
            Result.success(lyricInfo)
        } else {
            LogUtil.e("Repository-getLyricById","根据歌曲id获得歌词失败")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 根据id获取用户信息
     */
    fun getUserDetail(uid: Long) = fire(Dispatchers.IO){
        val userDetailData = CloudMusicNetwork.getUserDetail(uid)

        if (userDetailData.code == 200){
            LogUtil.e("Repository-getUserDetail","根据id获取用户信息成功")
            Result.success(userDetailData)
        } else {
            LogUtil.e("Repository-getUserDetail","根据id获取用户信息失败")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 获取首页的Banner
     */
    fun getBanner() = fire(Dispatchers.IO){
        val bannerInfo = CloudMusicNetwork.getBanner()

        if (bannerInfo.code == 200){
            LogUtil.e("Repository-getBanner","获取首页的Banner成功")
            Result.success(bannerInfo.banners)
        } else {
            LogUtil.e("Repository-getBanner","获取首页的Banner失败")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 获取mv
     */
    fun getMvUrl(id: Long) = fire(Dispatchers.IO){
        val mvInfo = CloudMusicNetwork.getMvUrl(id)

        if (mvInfo.code == 200){
            LogUtil.e("Repository-getMvUrl","获取mv成功")
            Result.success(mvInfo.data.url)
        } else {
            LogUtil.e("Repository-getMvUrl","获取mv失败")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 获取默认搜索关键词
     */
    fun getSearchDefault()= fire(Dispatchers.IO){
        val searchDefaultInfo = CloudMusicNetwork.getSearchDefault()

        if (searchDefaultInfo.code == 200){
            LogUtil.e("Repository-getSearchDefault","获取默认搜索关键词成功")
            Result.success(searchDefaultInfo.data)
        } else {
            LogUtil.e("Repository-getSearchDefault","获取默认搜索关键词失败")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 获取热搜详细列表
     */
    fun getSearchHotDetail() = fire(Dispatchers.IO){
        val searchHotDetailInfo = CloudMusicNetwork.getSearchHotDetail()

        if (searchHotDetailInfo.code == 200){
            LogUtil.e("Repository-getSearchHotDetail","获取热搜详细列表成功")
            Result.success(searchHotDetailInfo)
        } else {
            LogUtil.e("Repository-getSearchHotDetail","获取热搜详细列表失败")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 搜索单曲、歌手名
     */
    fun search(keywords: String, type: Int) = fire(Dispatchers.IO){
        val searchInfo = CloudMusicNetwork.search(keywords, type)

        if (searchInfo.code == 200){
            LogUtil.e("Repository-search","搜索单曲、歌手名成功")
            Result.success(searchInfo.result)
        } else {
            LogUtil.e("Repository-search","搜索单曲、歌手名失败")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 获取推荐歌单
     */
    fun getRecommendPlaylist() = fire(Dispatchers.IO){
        val recommendPlaylistInfo = CloudMusicNetwork.getRecommendPlaylist()

        if (recommendPlaylistInfo.code == 200){
            LogUtil.e("Repository-getRecommendPlaylist","获取推荐歌单成功")
            Result.success(recommendPlaylistInfo.playlists)
        } else {
            LogUtil.e("Repository-getRecommendPlaylist","获取推荐歌单失败")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 获取榜单
     */
    fun getToplist() = fire(Dispatchers.IO){
        val topListInfo = CloudMusicNetwork.getToplist()

        if (topListInfo.code == 200){
            LogUtil.e("Repository-getToplist","获取榜单成功")
            Result.success(topListInfo.list)
        } else {
            LogUtil.e("Repository-getToplist","获取榜单失败")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 日推, 需登录
     */
    fun getDailySongs(cookie: String) = fire(Dispatchers.IO){
        val dailySongsInfo = CloudMusicNetwork.getDailySongs(cookie)

        if (dailySongsInfo.code == 200){
            LogUtil.e("Repository-getDailySongs","获取日推成功")
            Result.success(dailySongsInfo.data)
        } else {
            LogUtil.e("Repository-getDailySongs","需要登录")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 日推歌单, 需登录
     */
    fun getRecommendDaily(cookie: String) = fire(Dispatchers.IO){
        val dailyPlaylistInfo = CloudMusicNetwork.getRecommendDaily(cookie)

        if (dailyPlaylistInfo.code == 200){
            LogUtil.e("Repository-getRecommendDaily","获取日推成功")
            Result.success(dailyPlaylistInfo.recommend)
        } else {
            LogUtil.e("Repository-getRecommendDaily","需要登录")
            Result.failure(RuntimeException("response of code is not 200"))
        }
    }

    /***
     * 获取评论
     */
    private const val PAGE_SIZE = 20
    private val commentService = CommentService.create()
    fun getCommentsPagingData(type: Int, id: Long, sortType: Int): Flow<PagingData<CommentInnerData>> {
        return Pager(
            config = PagingConfig(PAGE_SIZE),
            pagingSourceFactory = { CommentPagingSource(commentService, type, id, sortType) }
        ).flow
    }


    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e:Exception){
                LogUtil.e("Repository","请求失败")
                Result.failure<T>(e)
            }
            emit(result)
        }
}