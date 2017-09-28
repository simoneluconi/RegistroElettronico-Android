package com.sharpdroid.registroelettronico.Databases.Entities

import java.util.*

data class LoginRequest(
        val pass: String,
        val uid: String
) {
    override fun toString(): String {
        return String.format(Locale.getDefault(), "{ \"uid\": \"%s\", \"pass\": \"%s\"}", uid, pass)
    }
}