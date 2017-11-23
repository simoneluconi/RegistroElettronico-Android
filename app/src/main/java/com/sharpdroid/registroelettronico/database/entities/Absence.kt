package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
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
@Entity(tableName = "ABSENCE")
class Absence(
        @ColumnInfo(name = "ID") @PrimaryKey @Expose @SerializedName("evtId") var id: Long = 0L,
        @ColumnInfo(name = "TYPE") @Expose @SerializedName("evtCode") var type: String = "",
        @ColumnInfo(name = "DATE") @Expose @SerializedName("evtDate") var date: Date = Date(0),
        @ColumnInfo(name = "JUSTIFIED") @Expose @SerializedName("isJustified") var justified: Boolean = false,
        @ColumnInfo(name = "REASON_CODE") @Expose @SerializedName("justifReasonCode") var reasonCode: String?,
        @ColumnInfo(name = "REASON_DESC") @Expose @SerializedName("justifReasonDesc") var reasonDesc: String?,
        @ColumnInfo(name = "PROFILE") var profile: Long = -1L,
        @ColumnInfo(name = "H_POS") @Expose @SerializedName("evtHPos") var hPos: Int = 0,
        @ColumnInfo(name = "VALUE") @Expose @SerializedName("evtValue") var value: Int = 0
) {
    companion object {
        fun getAbsences(p: Long): Map<Absence, Int> /*<ABSENCE, N_DAYS>*/ {
            val map = mutableMapOf<Absence, Int>()

            val absences = DatabaseHelper.database.absencesDao().getAbsences(p).sortedBy { it.date }
            var start: Absence? = null
            var days = 1

            if (absences.size == 1) {
                map.put(absences[0], 1)
                return map
            }

            for (i in 0 until absences.size) {
                if (start == null) {
                    start = absences[i]
                    days = 1
                }

                val current = Calendar.getInstance()
                val next = Calendar.getInstance()

                var delta = 0L
                if (absences.size > i + 1) {
                    current.time = absences[i].date
                    next.time = absences[i + 1].date

                    delta = (next.time.time - current.time.time) / 3600000
                }

                when {
                    delta > 72 -> {
                        //SPLIT absences
                        map.put(start, days)
                        start = null
                    }
                    delta == 72L -> {
                        //is friday->monday CONTINUE else SPLIT
                        if (current.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY &&
                                next.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                            days++
                        } else {
                            map.put(start, days)
                            start = null
                        }
                    }
                    delta == 48L -> {
                        //is saturday->monday CONTINUE else SPLIT
                        if (current.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY &&
                                next.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                            days++
                        } else {
                            map.put(start, days)
                            start = null
                        }
                    }
                    delta == 24L -> {
                        //CONTINUE
                        days++
                    }
                    else -> {
                        map.put(start, days)
                        start = null
                    }
                }
            }

            return map
        }
    }
}

class AbsenceAPI(@Expose @SerializedName("events") private val events: List<Absence>) {
    fun getEvents(profile: Profile): List<Absence> {
        val id = profile.id
        events.forEach {
            it.profile = id
        }
        return events
    }
}

class MyAbsence(val absence: Absence, val days: Int)