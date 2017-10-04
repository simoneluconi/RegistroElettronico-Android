package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.dsl.Table
import com.orm.dsl.Unique
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
data class File(
        @Expose @SerializedName("contentId") @Unique val id: Long,
        @Expose @SerializedName("contentName") val contentName: String,
        @Expose @SerializedName("objectId") val objectId: Int,
        @Expose @SerializedName("objectType") val type: String,
        @Expose @SerializedName("shareDT") val date: Date,
        var folder: Long,
        var teacher: Long,
        var profile: Long
) {
    constructor() : this(0, "", 0, "", Date(), 0, 0, 0)
}

@Table
data class FileInfo(@Unique val id: Long, var path: String) {
    constructor() : this(0L, "")
}

data class DownloadURL(@Expose @SerializedName("link") val link: String)
data class DownloadUrlAPI(@Expose @SerializedName("item") val item: DownloadURL)
data class DownloadTXT(@Expose @SerializedName("text") val text: String)
data class DownloadTxtAPI(@Expose @SerializedName("item") val item: DownloadTXT)