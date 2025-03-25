package com.example.movieapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailMovie(

    @field:SerializedName("msg")
    val msg: String? = null,

    @field:SerializedName("movie")
    val movie: Movie? = null,

    @field:SerializedName("episodes")
    val episodes: List<Episodes>? = null,

    @field:SerializedName("status")
    val status: Boolean? = null,
) : Parcelable


data class ServerData(
    @field:SerializedName("filename")
    val filename: String? = null,

    @field:SerializedName("link_embed")
    val linkEmbed: String? = null,

    @field:SerializedName("link_m3u8")
    val linkM3u8: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("slug")
    val slug: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        filename = parcel.readString(),
        linkEmbed = parcel.readString(),
        linkM3u8 = parcel.readString(),
        name = parcel.readString(),
        slug = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(filename)
        parcel.writeString(linkEmbed)
        parcel.writeString(linkM3u8)
        parcel.writeString(name)
        parcel.writeString(slug)
    }


    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<ServerData> {
        override fun createFromParcel(parcel: Parcel): ServerData {
            return ServerData(parcel)
        }

        override fun newArray(size: Int): Array<ServerData?> {
            return arrayOfNulls(size)
        }
    }
}

@Parcelize
data class Episodes(

    @field:SerializedName("server_name")
    val serverName: String? = null,

    @field:SerializedName("server_data")
    val serverData: ArrayList<ServerData>
) : Parcelable

