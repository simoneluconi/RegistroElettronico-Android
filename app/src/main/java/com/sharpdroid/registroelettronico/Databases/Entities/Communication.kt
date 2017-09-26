package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord

/*
{
    "items": [
        {
            "pubId": 1156184,
            "pubDT": "2017-09-26T11:02:05+02:00",
            "readStatus": true,
            "evtCode": "CF",
            "cntId": 373940,
            "cntValidFrom": "2017-09-26",
            "cntValidTo": "2017-09-27",
            "cntValidInRange": true,
            "cntStatus": "active",
            "cntTitle": "mercoledì 27 settembre 4FSA alla 3^ ora supplenza in A305",
            "cntCategory": "News",
            "cntHasChanged": false,
            "cntHasAttach": false,
            "needJoin": false,
            "needReply": false,
            "needFile": false
        }
    ]
}
 */
data class Communication(
        @Expose @SerializedName("pubId") val pubId: Int,
        @Expose @SerializedName("pubDT") val date: String,
        @Expose @SerializedName("readStatus") val isRead: Boolean,
        @Expose @SerializedName("evtCode") val evtCode: String, //use only to mark as read
        @Expose @SerializedName("cntId") val myId: Int,
        @Expose @SerializedName("cntTitle") val title: String,
        @Expose @SerializedName("cntCategory") val category: String,
        @Expose @SerializedName("cntHasAttach") val hasAttachment: String,
        val username: String?
) : SugarRecord()