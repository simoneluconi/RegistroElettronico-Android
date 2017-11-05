package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
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
@Entity(tableName = "ABSENCE")
class Absence(
        @ColumnInfo(name = "ID") @PrimaryKey @Expose @SerializedName("evtId") var id: Long = 0L,
        @ColumnInfo(name = "TYPE") @Expose @SerializedName("evtCode") var type: String = "",
        @ColumnInfo(name = "DATE") @Expose @SerializedName("evtDate") var date: Date = Date(0),
        @ColumnInfo(name = "JUSTIFIED") @Expose @SerializedName("isJustified") var justified: Boolean = false,
        @ColumnInfo(name = "REASON_CODE") @Expose @SerializedName("justifReasonCode") var reasonCode: String = "",
        @ColumnInfo(name = "REASON_DESC") @Expose @SerializedName("justifReasonDesc") var reasonDesc: String = "",
        @ColumnInfo(name = "PROFILE") var profile: Long = -1L,
        @ColumnInfo(name = "H_POS") @Expose @SerializedName("evtHPos") var hPos: Int = 0,
        @ColumnInfo(name = "VALUE") @Expose @SerializedName("evtValue") var value: Int = 0
) {
    companion object {
        fun getAbsences(p: Long): HashMap<Absence, Int> /*<ABSENCE, N_DAYS>*/ {
            val map = HashMap<Absence, Int>()

            val absencesInSchoolDays = DatabaseHelper.database.absencesDao().getAbsences(p)
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
                        map.put(startAbsence, days)
                        startAbsence = null
                        continue@loop
                    }
                    timeDifference == 72L -> {
                        //is friday->monday CONTINUE else SPLIT
                        if (current.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && next.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                            days++
                        } else {
                            map.put(startAbsence, days)
                            startAbsence = null
                            continue@loop
                        }
                    }
                    timeDifference == 48L -> {
                        //is saturday->monday CONTINUE else SPLIT
                        if (current.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && next.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                            days++
                        } else {
                            map.put(startAbsence, days)
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
                    map.put(startAbsence, days)
                    startAbsence = null
                }
            }

            return map
        }

    }
}

class AbsenceAPI(@Expose @SerializedName("events") private val events: List<Absence>) {
    fun getEvents(profile: Profile): List<Absence> {
        val id = profile.id
        events.forEach { it.profile = id }
        return events
    }
}

class MyAbsence(val absence: Absence, val days: Int)