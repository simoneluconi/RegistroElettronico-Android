package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.dsl.Table
import com.orm.dsl.Unique
import java.util.*

/*
{
    "items": [
        {
            "pubId": 1156184,
            "pubDT": "2017-09-26T11:02:05+02:00",
            "readStatus": true,
            "evtCode": "CF", -------------------------you need this to download attachment
            "cntId": 373940,
            "cntValidFrom": "2017-09-26",
            "cntValidTo": "2017-09-27",
            "cntValidInRange": true,
            "cntStatus": "active", -------------------or "deleted"
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
@Table
data class Communication(
        @Expose @SerializedName("pubId") @Unique val id: Long,
        @Expose @SerializedName("pubDT") val date: Date,
        @Expose @SerializedName("readStatus") val isRead: Boolean,
        @Expose @SerializedName("evtCode") val evtCode: String,
        @Expose @SerializedName("cntId") val myId: Int,
        @Expose @SerializedName("cntTitle") val title: String,
        @Expose @SerializedName("cntCategory") val category: String,
        @Expose @SerializedName("cntHasAttach") val hasAttachment: String,
        var profile: Profile?
) {
    constructor() : this(0, Date(), false, "", 0, "", "", "", null)
}

data class CommunicationAPI(@Expose @SerializedName("items") val communications: List<Communication>) {
    fun getCommunications(profile: Profile): List<Communication> {
        communications.forEach { it.profile = profile }
        return communications
    }
}