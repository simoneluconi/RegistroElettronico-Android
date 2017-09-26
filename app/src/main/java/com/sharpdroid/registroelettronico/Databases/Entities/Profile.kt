package com.sharpdroid.registroelettronico.Databases.Entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "profiles")
data class Profile(@PrimaryKey() val username: String,
                   val name: String?,
                   val `class`: String?)