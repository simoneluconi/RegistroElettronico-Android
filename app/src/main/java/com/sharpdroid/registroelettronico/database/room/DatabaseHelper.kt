package com.sharpdroid.registroelettronico.database.room

import android.arch.persistence.room.Room
import android.content.Context
import com.sharpdroid.registroelettronico.database.Migrations.MIGRATION_10_11
import com.sharpdroid.registroelettronico.database.Migrations.MIGRATION_11_12
import com.sharpdroid.registroelettronico.database.Migrations.MIGRATION_4_5
import com.sharpdroid.registroelettronico.database.Migrations.MIGRATION_5_6
import com.sharpdroid.registroelettronico.database.Migrations.MIGRATION_6_7
import com.sharpdroid.registroelettronico.database.Migrations.MIGRATION_7_8
import com.sharpdroid.registroelettronico.database.Migrations.MIGRATION_8_9
import com.sharpdroid.registroelettronico.database.Migrations.MIGRATION_9_10
import java.util.concurrent.atomic.AtomicBoolean
object DatabaseHelper {

    lateinit var database: RoomDB

    private val initialized = AtomicBoolean(false)

    fun createDb(context: Context) {
        if (initialized.compareAndSet(false, true)) {
            database = Room.databaseBuilder(context, RoomDB::class.java, "registro.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addMigrations(
                            MIGRATION_4_5,
                            MIGRATION_5_6,
                            MIGRATION_6_7,
                            MIGRATION_7_8,
                            MIGRATION_8_9,
                            MIGRATION_9_10,
                            MIGRATION_10_11,
                            MIGRATION_11_12
                    )
                    .build()
        }
    }
}