package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Unique

data class Teacher(
        @Expose() @SerializedName("teacherId") @Unique var id: Int,
        @Expose() @SerializedName("teacherName") var name: String
) : SugarRecord()