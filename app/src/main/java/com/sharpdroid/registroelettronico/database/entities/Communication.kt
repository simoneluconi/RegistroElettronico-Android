package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
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
@Entity(tableName = "COMMUNICATION")
data class Communication(
        @PrimaryKey @ColumnInfo(name = "ID") @Expose @SerializedName("pubId") @Unique var id: Long = -1L,
        @ColumnInfo(name = "DATE") @Expose @SerializedName("pubDT") var date: Date = Date(0),
        @ColumnInfo(name = "IS_READ") @Expose @SerializedName("readStatus") var isRead: Boolean = false,
        @ColumnInfo(name = "EVT_CODE") @Expose @SerializedName("evtCode") var evtCode: String = "",
        @ColumnInfo(name = "MY_ID") @Expose @SerializedName("cntId") var myId: Long = -1L,
        @ColumnInfo(name = "TITLE") @Expose @SerializedName("cntTitle") var title: String = "",
        @android.arch.persistence.room.Ignore @Expose @SerializedName("cntStatus") @Ignore var cntStatus: String = "",
        @ColumnInfo(name = "CATEGORY") @Expose @SerializedName("cntCategory") var category: String = "",
        @ColumnInfo(name = "HAS_ATTACHMENT") @Expose @SerializedName("cntHasAttach") var hasAttachment: Boolean = false,
        @ColumnInfo(name = "PROFILE") var profile: Long
) {
    constructor() : this(0, Date(), false, "", 0, "", "", "", false, -1)
}

data class CommunicationAPI(@Expose @SerializedName("items") private var communications: List<Communication>) {
    fun getCommunications(profile: Profile): List<Communication> {
        val id = profile.id
        communications.forEach { it.profile = id }
        return communications
    }
}

/*
 *{
    "item": {
        "layout_verifica": "CIR. N. 22  LIBRI IN COMODATO D’USO.",
        "text": "Restituzione alla Sig.ra Maria Rosa presso la segreteria didattica dal prossimo Mercoledì 4 Ottobre a Sabato 7 Ottobre.\r\nRichiesta libri comodato d'uso anno corrente: consegnare nello stesso periodo la relativa richiesta.(VEDEI CIRCOLARE NEL SITO)"
    },
    "reply": {
        "replJoin": false,
        "replText": null,
        "replFile": null
    }
}
 */
@Table
data class CommunicationInfo(@Unique var id: Long = -1L,
                             @Expose @SerializedName("title") var title: String = "",
                             @Expose @SerializedName("text") var content: String = "",
                             var path: String) {
    constructor() : this(0L, "", "", "")
}

data class ReadResponse(@Expose @SerializedName("item") var item: CommunicationInfo)