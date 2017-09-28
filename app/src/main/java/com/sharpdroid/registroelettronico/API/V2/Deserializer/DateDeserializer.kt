package com.sharpdroid.registroelettronico.API.V2.Deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class DateDeserializer : JsonDeserializer<Date> {
    private val final = arrayOf("yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd\'T\'HH:mm:ssZZZZZ")
    private val pattern = arrayOf("\\d{4}-\\d{2}-\\d{2}", "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}")

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        val dateText = json?.asString ?: ""
        (0..pattern.size - 1)
                .filter { Pattern.matches(pattern[it], dateText) }
                .forEach {
                    try {
                        return SimpleDateFormat(final[it], Locale.getDefault()).parse(dateText)
                    } catch (err: Exception) {

                    }
                }
        throw Exception("DateParser - Could not parse date")
    }
}