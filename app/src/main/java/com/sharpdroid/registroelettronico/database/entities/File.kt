package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/*
{
    "contents": [
        {
            "contentId": 3017197,
            "contentName": "teoria cinetica dei gas.pdf",
            "objectId": 1833862,
            "objectType": "file",
            "shareDT": "2017-09-21T10:28:29+02:00"
        }
    ]
}

{
    "item": {
        "link": "http://www.youtube.com/watch?v=dmpYfs5X0AE"
    }
}

 */
@Table
@Entity(tableName = "FILE")
data class File(
        @ColumnInfo(name = "ID") @PrimaryKey @Expose @SerializedName("contentId") @Unique var id: Long = -1L,
        @ColumnInfo(name = "CONTENT_NAME") @Expose @SerializedName("contentName") var contentName: String = "",
        @ColumnInfo(name = "OBJECT_ID") @Expose @SerializedName("objectId") var objectId: Long = -1L,
        @ColumnInfo(name = "TYPE") @Expose @SerializedName("objectType") var type: String = "",
        @ColumnInfo(name = "DATE") @Expose @SerializedName("shareDT") var date: Date = Date(0),
        @ColumnInfo(name = "FOLDER") var folder: Long = -1L,
        @ColumnInfo(name = "TEACHER") var teacher: Long = -1L,
        @ColumnInfo(name = "PROFILE") var profile: Long
) {
    constructor() : this(0, "", 0, "", Date(), 0, 0, 0)
}

@Table
data class FileInfo(@Unique val id: Long = -1L, var path: String) {
    constructor() : this(-1L, "")
}

data class DownloadURL(@Expose @SerializedName("link") val link: String)
data class DownloadUrlAPI(@Expose @SerializedName("item") val item: DownloadURL)
data class DownloadTXT(@Expose @SerializedName("text") val text: String)
data class DownloadTxtAPI(@Expose @SerializedName("item") val item: DownloadTXT)