package com.sharpdroid.registroelettronico.Databases.Entities

import java.util.*

data class LoginResponse(val ident: String,
                         val firstName: String,
                         val lastName: String,
                         val token: String,
                         val expire: Date
)