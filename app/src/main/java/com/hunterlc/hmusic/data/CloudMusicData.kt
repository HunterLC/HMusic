package com.hunterlc.hmusic.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

// 详细用户信息
@Keep
data class UserDetailData(
    @SerializedName("level") val level: Int?, // 用户等级
    @SerializedName("code") val code: Int?, // 参数，一般为错误代码，可能是空
    @SerializedName("listenSongs") val listenSongs: Int?, //听过歌曲的总数
    @SerializedName("mobileSign") val mobileSign: Boolean?,
    @SerializedName("pcSign") val pcSign: Boolean?,
    @SerializedName("profile") val profile: ProfileData?,
    @SerializedName("cookie") val cookie: String?
)

// 用户简单信息
@Keep
data class ProfileData(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("userId") val userId: Long,
    @SerializedName("avatarUrl") val avatarUrl: String, // 头像
    @SerializedName("follows") val follows: Int, // 关注
    @SerializedName("followers") val followers: Int, // 粉丝
    @SerializedName("backgroundUrl") val backgroundUrl: String?,
    @SerializedName("signature") val signature: String?
)

@Keep
data class UserPlaylistData(
    val more: Boolean, // 是否有更多
    val playlist: ArrayList<PlaylistData>
)

@Keep
data class ToplistInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("list") val list: List<PlaylistData>
)

@Keep
data class PlaylistData(
    @SerializedName("coverImgUrl") val coverImgUrl: String, // 歌单图片
    @SerializedName("name") val name: String, // 歌单名称
    @SerializedName("trackCount") val trackCount: Int, // 歌单歌曲数量
    @SerializedName("id") val id: Long, // 歌单 id
    @SerializedName("playCount") val playCount: Long,
    @SerializedName("description") val description: String, // 歌单 id
    @SerializedName("updateFrequency") val updateFrequency: String
)

@Keep
data class DetailPlaylistData(
    @SerializedName("code") val code: Int,
    @SerializedName("playlist") val playlist: DetailPlaylistInnerData?
)

@Keep
data class DetailPlaylistInnerData(
    @SerializedName("tracks") val tracks: List<TracksData>,
    @SerializedName("trackIds") val trackIds: List<TrackIdsData>,
    @SerializedName("coverImgUrl") val coverImgUrl: String?, // 歌单图片
    @SerializedName("name") val name: String?, // 歌单名字
    @SerializedName("description") val description: String? // 描述
)

@Keep
data class TracksData(
    val name: String, // 歌曲名称
    val id: Long // 歌曲 id
)

@Keep
data class TrackIdsData(
    val id: Long // 歌曲 id
)

@Keep
data class MusicData(
    @SerializedName("code") val code: Int,
    @SerializedName("songs") val songs: List<SongsInnerData>?
)

@Keep
@Parcelize
data class SongsInnerData(
    @SerializedName("name") val name: String?, // 歌曲名字
    @SerializedName("id") val id: Long, //歌曲id
    @SerializedName("ar") val artists: List<ArtistsData>, //歌曲演唱者
    @SerializedName("al") val album: AlbumData, //歌曲所属专辑信息
    @SerializedName("mv") val mv: Long?, //mv的id
    @SerializedName("reason") val reason: String?
): Parcelable {

    @Parcelize
    data class ArtistsData(
        @SerializedName("id") val id: Long, //歌手id
        @SerializedName("name") val name: String?, // 歌手名字
        @SerializedName("picUrl") val picUrl: String?,
        @SerializedName("albumSize") val albumSize: Int?,
        @SerializedName("trans") val trans: String?,
        @SerializedName("mvSize") val mvSize: Int?
    ): Parcelable

    @Parcelize
    data class AlbumData(
        @SerializedName("id") val id: Long, //专辑id
        @SerializedName("name") val name: String?, // 专辑名字
        @SerializedName("picUrl") val picUrl: String?  //专辑封面
    ): Parcelable
}

@Keep
data class DailySongsInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: DailySongsData
)

@Keep
data class DailySongsData(
    @SerializedName("dailySongs") val dailySongs: List<SongsInnerData>
)

@Keep
data class DailyPlaylistInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("recommend") val recommend: List<DailyPlaylistData>
)

@Keep
data class DailyPlaylistData(
    @SerializedName("picUrl") val picUrl: String, // 歌单图片
    @SerializedName("name") val name: String, // 歌单名称
    @SerializedName("trackCount") val trackCount: Int, // 歌单歌曲数量
    @SerializedName("id") val id: Long, // 歌单 id
    @SerializedName("playcount") val playCount: Long,
    @SerializedName("copywriter") val copywriter: String
)

@Keep
data class SongUrl(
    @SerializedName("code") val code: Int,
    @SerializedName("data") val data: List<SongUrlInnerData>
)

@Keep
data class SongUrlInnerData(
    @SerializedName("id") val id: Long,
    @SerializedName("url") var url: String,
    @SerializedName("br") val br: Long,
    @SerializedName("md5") val md5: String,
    @SerializedName("type") val type: String
)

@Keep
data class LoginInfo(
    val phone: String, // 电话
    val countryCode: Int, // 国家代码
    val md5_password: String // 密码
)

@Keep
data class LyricViewData(
    val lyric: String,
    val secondLyric: String
)

@Keep
data class LyricInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("lrc") var lrc: Lrc,
    @SerializedName("tlyric") val tlyric: Tlyric
)

@Keep
data class Lrc(
    @SerializedName("version") val version: Int,
    @SerializedName("lyric") var lyric: String
)

@Keep
data class Tlyric(
    @SerializedName("version") val version: Int,
    @SerializedName("lyric") var lyric: String
)

@Keep
data class BannerInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("banners") var banners: List<Banners>
)

@Keep
data class Banners(
    @SerializedName("pic") val pic: String,
    @SerializedName("typeTitle") val typeTitle: String,
    @SerializedName("url") val url: String?,
    @SerializedName("bannerId") val bannerId: String,
    @SerializedName("song") val song: SongsInnerData?
)

@Keep
data class MvInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("data") var data: MvData
)

@Keep
data class MvData(
    @SerializedName("id") val id: Long,
    @SerializedName("url") var url: String
)

@Keep
data class SearchDefaultInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("data") var data: SearchDefaultData
)

@Keep
data class SearchDefaultData(
    @SerializedName("showKeyword") val showKeyword: String,
    @SerializedName("realkeyword") var realkeyword: String
)

@Keep
data class SearchHotDetailInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("data") var data: List<SearchHotDetailData>
)

@Keep
data class SearchHotDetailData(
    @SerializedName("searchWord") val searchWord: String,
    @SerializedName("score") var score: Long,
    @SerializedName("content") val content: String,
    @SerializedName("iconUrl") val iconUrl: String
)

@Keep
data class SearchInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("result") var result: SearchResult
)

@Keep
data class SearchResult(
    @SerializedName("songs") val songs: List<SongsInnerData>?,
    @SerializedName("songCount") var songCount: Int?,
    @SerializedName("artists") val artists: List<SongsInnerData.ArtistsData>?,
    @SerializedName("artistCount") var artistCount: Int?,
)

@Keep
data class RecommendPlaylistInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("more") var more: Boolean,
    @SerializedName("total") val total: Int,
    @SerializedName("playlists") var playlists: List<RecommendPlaylistData>
)

@Keep
data class RecommendPlaylistData(
    @SerializedName("name") val name: String,
    @SerializedName("id") val id: Long,
    @SerializedName("coverImgUrl") val coverImgUrl: String,
    @SerializedName("description") val description: String,
    @SerializedName("playCount") val playCount: Long,
    @SerializedName("tag") val tag: String?,
    @SerializedName("trackCount") val trackCount: Int,
    @SerializedName("subscribedCount") val subscribedCount: Int?,
    @SerializedName("copywriter") val copywriter: String?
)

// 歌曲评论
@Keep
data class CommentInfo(
    @SerializedName("code") val code: Int,
    @SerializedName("data") var data: CommentData
)

@Keep
data class CommentData(
    @SerializedName("comments") val comments: List<CommentInnerData>, // 热门评论
    @SerializedName("totalCount") val totalCount: Long,
    @SerializedName("hasMore") val hasMore: Boolean,
    @SerializedName("cursor") val cursor: String
)

@Keep
data class CommentInnerData(
    @SerializedName("commentId") val commentId: Long,
    @SerializedName("content") val content: String,
    @SerializedName("time") val time: Long,
    @SerializedName("likedCount") val likedCount: Long,
    @SerializedName("liked") val liked: Boolean,
    @SerializedName("user") val user: CommentUser
)

@Keep
data class CommentUser(
    @SerializedName("userId") val userId: Long,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("avatarUrl") val avatarUrl: String
)