package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.dsl.Table
import com.orm.dsl.Unique
import java.util.*

/*
{
    "events": [     //Le assenze da 2 o più giorni vengono rappresentate in oggetti differenti, come se fossero assenze singole
        {
            "evtId": 364771,
            "evtCode": "ABA0",
            "evtDate": "2017-09-26",
            "evtHPos": null,
            "evtValue": null,
            "isJustified": false,
            "justifReasonCode": null,
            "justifReasonDesc": null
        },
        {
            "evtId": 443115,
            "evtCode": "ABA0",
            "evtDate": "2017-09-27",
            "evtHPos": null,
            "evtValue": null,
            "isJustified": true,
            "justifReasonCode": "A",
            "justifReasonDesc": "Motivi di salute"
        }
    ]
}
evtCode:
    ABA0 assenza
    ABU0 uscita
    ABR0 ritardo
    ABR1 ritardo breve

 */
@Table
data class Absence(
        @Expose @SerializedName("evtId") @Unique val id: Long,
        @Expose @SerializedName("evtCode") val type: String,
        @Expose @SerializedName("evtDate") val date: Date,
        @Expose @SerializedName("isJustified") val justified: Boolean,
        @Expose @SerializedName("justifReasonCode") val reasonCode: String?,
        @Expose @SerializedName("justifReasonDesc") val reasonDesc: String?,
        var profile: Profile?,
        @Expose @SerializedName("evtHPos") val hPos: Int,
        @Expose @SerializedName("evtValue") val value: Int
) {
    constructor() : this(0, "", Date(), false, null, null, null, 0, 0)
}

data class AbsenceAPI(@Expose @SerializedName("events") val events: List<Absence>) {
    fun getEvents(profile: Profile): List<Absence> {
        events.forEach { it.profile = profile }
        return events
    }
}