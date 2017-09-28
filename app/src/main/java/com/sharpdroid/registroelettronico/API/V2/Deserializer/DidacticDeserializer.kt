package com.sharpdroid.registroelettronico.API.V2.Deserializer

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.sharpdroid.registroelettronico.Databases.Entities.Didactic
import java.lang.reflect.Type

class DidacticDeserializer : JsonDeserializer<List<Didactic>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<Didactic> {
        return Gson().fromJson(json?.asJsonObject?.getAsJsonArray("didactics"), object : TypeToken<List<Didactic>>() {}.type)
    }

}