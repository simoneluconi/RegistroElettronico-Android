package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Unique
import java.util.*

data class Day(@Expose @SerializedName("dayDate") @Unique val dayDate: Date,
               @Expose @SerializedName("dayOfWeek") val dayOfWeek: Int,
               @Expose @SerializedName("dayStatus") val dayStatus: String,
               var profile: Profile?) : SugarRecord()

data class Calendar(@Expose @SerializedName("calendar") val calendar: List<Day>) {
    fun getCalendar(p: Profile): List<Day> {
        calendar.forEach { it.profile = p }
        return calendar
    }
}