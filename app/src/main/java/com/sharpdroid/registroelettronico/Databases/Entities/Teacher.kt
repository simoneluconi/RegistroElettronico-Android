package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.orm.SugarRecord
import com.orm.dsl.Ignore
import java.lang.reflect.Type


@JsonAdapter(TeacherAdapter::class)
data class Teacher(
        @Expose var teacherId: Int,
        @Expose var teacherName: String,
        @Expose @Ignore var folders: List<Folder>? //not present in /subjects
) : SugarRecord() {
    constructor() : this(0, "", emptyList())
}

data class DidacticAPI(@Expose @SerializedName("didactics") private val didactics: List<Teacher>)

class TeacherAdapter : JsonDeserializer<Teacher> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Teacher {
        val el = json?.asJsonObject
        val folders = Gson().fromJson<List<Folder>>(el?.getAsJsonArray("folders"), object : TypeToken<List<Folder>>() {}.type)

        return Teacher(el?.get("teacherId")?.asString.toString().substring(1).toInt(), el?.get("teacherName")?.asString.toString(), folders)
    }

}
