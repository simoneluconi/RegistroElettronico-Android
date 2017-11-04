package com.sharpdroid.registroelettronico.database.entities

import com.google.gson.Gson
import com.google.gson.annotations.Expose

class LoginRequest(
        @Expose private val pass: String = "",
        @Expose private val uid: String = "",
        @Expose val ident: String
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}