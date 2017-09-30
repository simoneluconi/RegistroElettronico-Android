package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
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
data class Period(
        @Expose @SerializedName("periodCode") val mCode: String,
        @Expose @SerializedName("periodDesc") val mDescription: String,
        @Expose @SerializedName("miurDivisionCode") val mDivisionCode: String,
        @Expose @SerializedName("dateEnd") val mEnd: Date,
        @Expose @SerializedName("isFinal") val mFinal: Boolean,
        @Expose @SerializedName("periodPos") val mPosition: Int,
        @Expose @SerializedName("dateStart") val mStart: Date,
        var profile: Profile?
) : SugarRecord()

data class PeriodAPI(@Expose @SerializedName("periods") val periods: List<Period>)