package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Table
import com.orm.dsl.Unique
import java.util.*
import kotlin.collections.HashMap

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
        @Expose @SerializedName("justifReasonCode") val reasonCode: String,
        @Expose @SerializedName("justifReasonDesc") val reasonDesc: String,
        var profile: Long,
        @Expose @SerializedName("evtHPos") val hPos: Int,
        @Expose @SerializedName("evtValue") val value: Int
) {
    constructor() : this(0, "", Date(0), false, "", "", -1, 0, 0)


    companion object {
        fun getAbsences(p: Profile): HashMap<Absence, Int> /*<ABSENCE, N_DAYS>*/ {
            val map = HashMap<Absence, Int>()

            val absencesInSchoolDays = SugarRecord.find(Absence::class.java, "PROFILE=? AND TYPE='ABA0' ORDER BY DATE DESC ", p.id.toString())
            var startAbsence: Absence? = null
            var days = 0

            if (absencesInSchoolDays.size == 1) {
                map.put(absencesInSchoolDays[0], 1)
                return map
            }

            loop@ for (i in 0 until absencesInSchoolDays.size - 1) {
                val timeDifference = (absencesInSchoolDays[i].date.time - absencesInSchoolDays[i + 1].date.time) / 3600000
                if (startAbsence == null) {
                    startAbsence = absencesInSchoolDays[i]
                    days = 1
                }
                val current = Calendar.getInstance()
                val next = Calendar.getInstance() //previous in time (e.g. current=monday; next=saturday)

                current.time = absencesInSchoolDays[i].date
                next.time = absencesInSchoolDays[i + 1].date

                when {
                    timeDifference > 72 -> {
                        //SPLIT absences
                        map.put(startAbsence!!, days)
                        startAbsence = null
                        continue@loop
                    }
                    timeDifference == 72L -> {
                        //is friday->monday CONTINUE else SPLIT
                        if (current.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && next.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                            days++
                        } else {
                            map.put(startAbsence!!, days)
                            startAbsence = null
                            continue@loop
                        }
                    }
                    timeDifference == 48L -> {
                        //is saturday->monday CONTINUE else SPLIT
                        if (current.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && next.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                            days++
                        } else {
                            map.put(startAbsence!!, days)
                            startAbsence = null
                            continue@loop
                        }
                    }
                    timeDifference == 24L -> {
                        //CONTINUE
                        days++
                    }
                }
                if (i == absencesInSchoolDays.size - 2) {
                    map.put(startAbsence!!, days)
                    startAbsence = null
                }
            }

            return map
        }

    }
}

data class AbsenceAPI(@Expose @SerializedName("events") val events: List<Absence>) {
    fun getEvents(profile: Profile): List<Absence> {
        val id = profile.id
        events.forEach { it.profile = id }
        return events
    }
}

data class MyAbsence(val absence: Absence, val days: Int)