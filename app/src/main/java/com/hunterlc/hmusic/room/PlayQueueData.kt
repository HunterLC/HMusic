package com.hunterlc.hmusic.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hunterlc.hmusic.data.SongsInnerData

@Entity
@TypeConverters(ArtistDataConverter::class,AlbumDataConverter::class,SongDataConverter::class)
data class PlayQueueData(
    @Embedded
    var songData: SongsInnerData
) {
    @PrimaryKey(autoGenerate = true)
    var databaseId: Long = 0
}

fun List<PlayQueueData>.toSongList(): ArrayList<SongsInnerData> {
    val list = ArrayList<SongsInnerData>()
    this.forEach {
        list.add(it.songData)
    }
    return list
}