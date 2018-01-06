package com.sharpdroid.registroelettronico.utils

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.sharpdroid.registroelettronico.database.entities.RemoteAgenda
import com.sharpdroid.registroelettronico.database.entities.TimetableItem
import com.sharpdroid.registroelettronico.database.room.RoomDB
import junit.framework.Assert.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomTest {
    private lateinit var db: RoomDB

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getContext()
        db = Room.inMemoryDatabaseBuilder(context, RoomDB::class.java).build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertEventInAgenda() {
        db.eventsDao().insertRemote(arrayListOf(RemoteAgenda()))
        val inserted = db.eventsDao().getRemoteList(-1).getOrNull(0)
        assertNotNull(inserted)
    }

    @Test
    fun insertTimetable() {
        db.timetableDao().insert(TimetableItem())
        val inserted = db.timetableDao().findById(1)
        assertNotNull(inserted)
    }

    @Test
    fun excludedMarks() {

    }

}