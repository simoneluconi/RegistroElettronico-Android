package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord

data class Teacher(
        @Expose() @SerializedName("teacherId") var teacherId: Int,
        @Expose() @SerializedName("teacherName") var teacherName: String = ""
) : SugarRecord()