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
 */
@Table
data class File(
        @Expose @SerializedName("contentId") @Unique val id: Long,
        @Expose @SerializedName("contentName") val contentName: String,
        @Expose @SerializedName("objectId") val objectId: Int,
        @Expose @SerializedName("objectType") val type: String,
        @Expose @SerializedName("shareDT") val date: Date,
        var folder: Long,
        var teacher: Long
) {
    constructor() : this(0, "", 0, "", Date(), 0, 0)
}