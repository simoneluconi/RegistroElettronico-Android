package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/*
{
    "periods": [
        {
            "periodCode": "Q1",
            "periodPos": 1,
            "periodDesc": "Trimestre",
            "isFinal": false,
            "dateStart": "2017-09-01",
            "dateEnd": "2017-12-23",
            "miurDivisionCode": null
        },
        {
            "periodCode": "Q3",
            "periodPos": 3,
            "periodDesc": "Pentamestre",
            "isFinal": true,
            "dateStart": "2017-12-24",
            "dateEnd": "2018-06-30",
            "miurDivisionCode": null
        }
    ]
}
 */
@Entity(tableName = "PERIOD")
class Period(
        @ColumnInfo(name = "M_CODE") @Expose @SerializedName("periodCode") var mCode: String = "",
        @ColumnInfo(name = "M_DESCRIPTION") @Expose @SerializedName("periodDesc") var mDescription: String = "",
        @ColumnInfo(name = "M_END") @Expose @SerializedName("dateEnd") var mEnd: Date,
        @ColumnInfo(name = "M_FINAL") @Expose @SerializedName("isFinal") var mFinal: Boolean = false,
        @ColumnInfo(name = "M_POSITION") @Expose @SerializedName("periodPos") var mPosition: Int = -1,
        @ColumnInfo(name = "M_START") @Expose @SerializedName("dateStart") var mStart: Date,
        @ColumnInfo(name = "PROFILE") var profile: Long
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    var id = 0L

    constructor() : this("", "", Date(0), false, 0, Date(0), 0L)

}

class PeriodAPI(@Expose @SerializedName("periods") private val periods: List<Period>) {
    fun getPeriods(profile: Profile): List<Period> {
        val id = profile.id
        periods.forEach { it.profile = id }
        return periods
    }
}