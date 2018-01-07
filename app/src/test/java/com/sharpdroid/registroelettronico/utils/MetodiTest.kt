package com.sharpdroid.registroelettronico.utils

import com.sharpdroid.registroelettronico.database.pojos.GeniusTimetable
import org.junit.Assert.assertEquals
import org.junit.Test

class MetodiTest {

    @Test
    fun calculateScholasticCredits() {
        assertEquals(Metodi.calculateScholasticCredits(4, 6.77f), 4)
        assertEquals(Metodi.calculateScholasticCredits(5, 6.77f), 5)
    }

    @Test
    fun geniusTimetable() {
        val subjects = arrayOf(1, 2, 3)
        val colors = Metodi.mapColorsToSubjects(subjects.asList())
        val originalGenius = arrayListOf(
                GeniusTimetable(0, 1305412, 1, 8, 9),
                GeniusTimetable(0, 1305412, 1, 9, 10),
                GeniusTimetable(0, 1390962, 3, 10, 11),
                GeniusTimetable(2, 1305488, 2, 8, 9),
                GeniusTimetable(2, 1305488, 2, 9, 10),
                GeniusTimetable(2, 1305379, 1, 10, 11)
        )
        val converted = Metodi.convertGeniusToTimetable(0, originalGenius, colors)

        assertEquals(converted.size, 4)
        assertEquals(converted[0].end, 10f)
    }
}