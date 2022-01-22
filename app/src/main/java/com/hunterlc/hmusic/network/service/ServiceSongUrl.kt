package com.hunterlc.hmusic.network.service

import com.hunterlc.hmusic.data.SongUrl
import com.hunterlc.hmusic.data.SongsInnerData
import com.hunterlc.hmusic.network.creator.ServiceCreator
import com.hunterlc.hmusic.network.repository.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ServiceSongUrl {
    inline fun getSongUrl(song: SongsInnerData, cookie: String, crossinline success: (String?) -> Unit) {
        val musicService = ServiceCreator.create(MusicService::class.java)
        musicService.getSongUrlById(song.id.toString(),cookie).enqueue(object : Callback<SongUrl> {
            override fun onFailure(call: Call<SongUrl>, t: Throwable) {
                success.invoke("https://music.163.com/song/media/outer/url?id=${song.id}.mp3")
            }

            override fun onResponse(call: Call<SongUrl>, response: Response<SongUrl>) {
                val body = response.body()
                success.invoke(body?.data?.get(0)?.url)
            }
        })
//        val aa = Repository.getSongUrlById(song.id.toString(),cookie)
//        success.invoke(aa.value?.getOrNull())
//        success.invoke("http://m701.music.126.net/20220117155023/334e1e4c8ac299fa88d9ca0c22e2a046/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/10268718131/bec6/e83d/db96/1f1d1f48c701f1a65c144a852a001552.flac")
    }
}