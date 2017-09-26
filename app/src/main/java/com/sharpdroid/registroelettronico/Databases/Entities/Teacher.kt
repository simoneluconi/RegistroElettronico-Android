package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord

data class Teacher(
        @Expose() @SerializedName("teacherId") var id: Int,
        @Expose() @SerializedName("teacherName") var name: String = ""
) : SugarRecord()