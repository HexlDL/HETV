package com.tv.libcommon

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class M3U8Data(
    @SerializedName("group-desc")
    val groupDesc: String?,
    @SerializedName("group-title")
    val groupTitle: String?,
    @SerializedName("tvg-id")
    val tvgId: String?,
    @SerializedName("tvg-log")
    val tvgLogo: String?,
    @SerializedName("tvg-name")
    val tvgName: String?,
    @SerializedName("tvg-url")
    val tvgUrl: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun equals(other: Any?): Boolean {
        return when (other) {
            !is M3U8Data -> false
            else -> this === other || tvgName == other.tvgName
        }
    }

    override fun toString(): String {
        return "groupTitle = $groupTitle,tvgId = $tvgId,tvgName = $tvgName,tvgUrl = $tvgUrl"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(groupDesc)
        parcel.writeString(groupTitle)
        parcel.writeString(tvgId)
        parcel.writeString(tvgLogo)
        parcel.writeString(tvgName)
        parcel.writeString(tvgUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<M3U8Data> {
        override fun createFromParcel(parcel: Parcel): M3U8Data {
            return M3U8Data(parcel)
        }

        override fun newArray(size: Int): Array<M3U8Data?> {
            return arrayOfNulls(size)
        }
    }
}
