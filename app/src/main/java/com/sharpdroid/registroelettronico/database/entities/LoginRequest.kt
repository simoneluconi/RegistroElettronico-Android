package com.sharpdroid.registroelettronico.database.entities

import com.google.gson.annotations.Expose
import java.util.*

data class LoginRequest(
        @Expose private val pass: String = "",
        @Expose private val uid: String = "",
        @Expose val ident: String
) {
    override fun toString(): String {
        return String.format(Locale.getDefault(), "{ \"uid\": \"%s\", \"pass\": \"%s\", \"ident\":\"%s\"}", uid, pass, ident)
    }
}