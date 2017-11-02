package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
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
@Entity(tableName = "FOLDER")
data class Folder(
        @ColumnInfo(name = "TITLE") @Expose @SerializedName("folderId") var folderId: Int = -1,
        @ColumnInfo(name = "NAME") @Expose @SerializedName("folderName") var name: String = "",
        @ColumnInfo(name = "LAST_UPDATE") @Expose @SerializedName("lastShareDT") var lastUpdate: Date = Date(0),
        @android.arch.persistence.room.Ignore @Expose @SerializedName("contents") @Ignore var files: List<File>,
        @ColumnInfo(name = "TEACHER") var teacher: Long = -1L,
        @ColumnInfo(name = "PROFILE") var profile: Long
) : Serializable {
    @ColumnInfo(name = "ID")
    @PrimaryKey(autoGenerate = true)
    @Ignore
    var id = -1L

    constructor() : this(0, "", Date(), emptyList(), 0, -1)
}