package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import java.util.*

data class LoginRequest(
        @Expose val pass: String,
        @Expose val uid: String,
        @Expose val ident: String
) {
    override fun toString(): String {
        return String.format(Locale.getDefault(), "{ \"uid\": \"%s\", \"pass\": \"%s\"}", uid, pass)
    }
}