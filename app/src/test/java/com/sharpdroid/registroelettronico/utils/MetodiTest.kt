package com.sharpdroid.registroelettronico.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class MetodiTest {

    @Test
    fun calculateScholasticCredits() {
        assertEquals(Metodi.calculateScholasticCredits(4, 6.77f), 4)
        assertEquals(Metodi.calculateScholasticCredits(5, 6.77f), 5)
    }
}