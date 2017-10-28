package com.sharpdroid.registroelettronico.database.entities

import com.google.gson.annotations.Expose
import java.util.*

data class LoginResponse(@Expose val ident: String?,
                         @Expose val firstName: String?,
                         @Expose val lastName: String?,
                         @Expose val token: String?,
                         @Expose val expire: Date?,
                         @Expose val choices: MutableList<Choice>?
)

data class Choice(
        @Expose val cid: String,
        @Expose val ident: String,
        @Expose val name: String,
        @Expose val school: String
)