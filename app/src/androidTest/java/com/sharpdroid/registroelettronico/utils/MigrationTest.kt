package com.sharpdroid.registroelettronico.utils

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.testing.MigrationTestHelper
import android.database.sqlite.SQLiteConstraintException
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.sharpdroid.registroelettronico.database.room.RoomDB
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
            RoomDB::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory())

    private val TEST_DB_NAME = "TEST_DB"

    @Test(expected = SQLiteConstraintException::class)
    fun communicationCategoryNull9() {
        val db = helper.createDatabase(TEST_DB_NAME, 9)
        db.execSQL("insert into COMMUNICATION values(1,1,1,'CF',1,'circ. n. 2', NULL, 1, 1)")
    }

    @Test()
    fun communicationCategoryNull10() {
        val db = helper.createDatabase(TEST_DB_NAME, 10)
        db.execSQL("insert into COMMUNICATION values(1,1,1,'CF',1,'circ. n. 2', NULL, 1, 1)")
    }
}