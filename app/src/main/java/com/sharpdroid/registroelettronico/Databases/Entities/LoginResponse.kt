package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import java.util.*

data class LoginResponse(@Expose val ident: String,
                         @Expose val firstName: String,
                         @Expose val lastName: String,
                         @Expose val token: String,
                         @Expose val expire: Date
)