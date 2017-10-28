package com.sharpdroid.registroelettronico.api.v2.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class LongDeserializer : JsonDeserializer<Long> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long {
        val primitive = json?.asJsonPrimitive!!
        return if (primitive.isString) primitive.asString?.substring(1)?.toLong() ?: -1 else primitive.asLong
    }
}