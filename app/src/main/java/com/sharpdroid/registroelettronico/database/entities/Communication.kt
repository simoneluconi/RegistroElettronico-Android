package com.sharpdroid.registroelettronico.database.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.dsl.Ignore
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
        @Expose @SerializedName("readStatus") var isRead: Boolean,
        @Expose @SerializedName("evtCode") val evtCode: String,
        @Expose @SerializedName("cntId") val myId: Long,
        @Expose @SerializedName("cntTitle") val title: String,
        @Expose @SerializedName("cntStatus") @Ignore val cntStatus: String,
        @Expose @SerializedName("cntCategory") val category: String,
        @Expose @SerializedName("cntHasAttach") val hasAttachment: Boolean,
        var profile: Long
) {
    constructor() : this(0, Date(), false, "", 0, "", "", "", false, -1)
}

data class CommunicationAPI(@Expose @SerializedName("items") private val communications: List<Communication>) {
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
data class CommunicationInfo(@Unique var id: Long,
                             @Expose @SerializedName("title") var title: String,
                             @Expose @SerializedName("text") var content: String,
                             var path: String) {
    constructor() : this(0L, "", "", "")
}

data class ReadResponse(@Expose @SerializedName("item") val item: CommunicationInfo)