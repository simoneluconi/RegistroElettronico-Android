package com.sharpdroid.registroelettronico.Databases.DAOs

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.database.Observable

@Dao interface ProfileDAO {
    @Query("SELECT class FROM profiles WHERE username = :username LIMIT 1")
    fun getClassDescription(username: String): Observable<String>
}