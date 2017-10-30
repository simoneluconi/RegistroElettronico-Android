package com.sharpdroid.registroelettronico.database.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Ignore
import java.io.Serializable
import java.util.*

/*
{
    "folders": [
        {
            "folderId": 0,
            "folderName": "Uncategorized",
            "lastShareDT": "2017-09-21T10:30:10+02:00",
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
    ]
}
 */

data class Folder(
        @Expose @SerializedName("folderId") var folderId: Int,
        @Expose @SerializedName("folderName") var name: String,
        @Expose @SerializedName("lastShareDT") var lastUpdate: Date,
        @Expose @SerializedName("contents") @Ignore var files: List<File>,
        var teacher: Long,
        var profile: Long
) : SugarRecord(), Serializable {
    constructor() : this(0, "", Date(), emptyList(), 0, -1)
}