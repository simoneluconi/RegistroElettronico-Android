package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
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
data class File(
        @Expose @SerializedName("contentId") val contentId: Int,
        @Expose @SerializedName("contentName") val contentName: String = "",
        @Expose @SerializedName("objectId") val objectId: Int,
        @Expose @SerializedName("objectType") val type: String = "",
        @Expose @SerializedName("shareDT") val date: Date = Date()
) : SugarRecord()