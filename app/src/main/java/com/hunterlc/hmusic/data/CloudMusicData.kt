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
data class PlaylistData(
    @SerializedName("coverImgUrl") val coverImgUrl: String, // 歌单图片
    @SerializedName("name") val name: String, // 歌单名称
    @SerializedName("trackCount") val trackCount: Int, // 歌单歌曲数量
    @SerializedName("id") val id: Long // 歌单 id
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
    @SerializedName("mv") val mv: Long? //mv的id
): Parcelable {

    @Parcelize
    data class ArtistsData(
        @SerializedName("id") val id: Long, //歌手id
        @SerializedName("name") val name: String? // 歌手名字
    ): Parcelable

    @Parcelize
    data class AlbumData(
        @SerializedName("id") val id: Long, //专辑id
        @SerializedName("name") val name: String?, // 专辑名字
        @SerializedName("picUrl") val picUrl: String?  //专辑封面
    ): Parcelable
}

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

// 歌曲评论
@Keep
data class CommentData(
    val hotComments: List<HotComment>, // 热门评论
    val total: Long // 总评论
)

@Keep
data class HotComment(
    val user: CommentUser,
    val content: String, // 评论内容
    val time: Long, // 评论时间
    val likedCount: Long // 点赞数
)

@Keep
data class CommentUser(
    val avatarUrl: String, // 头像
    val nickname: String, // 昵称
    val userId: Long //
)

@Keep
data class LoginInfo(
    val phone: String, // 电话
    val countrycode: Int, // 国家代码
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