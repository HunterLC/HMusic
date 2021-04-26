package com.hunterlc.hmusic.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hunterlc.hmusic.data.SongsInnerData
import java.lang.reflect.Type

class ArtistDataConverter {

    @TypeConverter
    fun objectToString(list: List<SongsInnerData.ArtistsData>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun stringToObject(json: String): List<SongsInnerData.ArtistsData> {
        val listType: Type = object : TypeToken<List<SongsInnerData.ArtistsData>>() {}.type
        return Gson().fromJson(json, listType)
    }

}

class AlbumDataConverter {
    @TypeConverter
    fun objectToString(album: SongsInnerData.AlbumData): String {
        return Gson().toJson(album)
    }

    @TypeConverter
    fun stringToObject(json: String): SongsInnerData.AlbumData {
        val albumType: Type = object : TypeToken<SongsInnerData.AlbumData>() {}.type
        return Gson().fromJson(json, albumType)
    }
}

class SongDataConverter {

    @TypeConverter
    fun objectToString(song: SongsInnerData): String {
        return Gson().toJson(song)
    }

    @TypeConverter
    fun stringToObject(json: String): SongsInnerData {
        val songType: Type = object : TypeToken<SongsInnerData>() {}.type
        return Gson().fromJson(json, songType)
    }

}

