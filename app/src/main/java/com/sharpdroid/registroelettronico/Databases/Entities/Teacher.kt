package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Ignore

data class Teacher(
        @Expose @SerializedName("teacherId") var teacherId: Int,
        @Expose @SerializedName("teacherName") var teacherName: String = "",
        @Expose @SerializedName("folders") @Ignore val folders: List<Folder>? //not present in /subjects
) : SugarRecord()

data class DidacticAPI(@Expose @SerializedName("didactics") private val didactics: List<Teacher>)