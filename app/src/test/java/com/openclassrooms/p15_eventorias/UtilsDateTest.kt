package com.openclassrooms.p15_eventorias

import com.openclassrooms.p15_eventorias.utils.isDateInFuture
import com.openclassrooms.p15_eventorias.utils.longToFormatedString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests des fonctions utilitaires de Date
 */

class UtilsDateTest {

    @Test
    fun testLongToFormattedString_withZeroTimestamp() {
        val result = longToFormatedString(0L, "dd/MM/yyyy")
        assertEquals("", result) // Vérifie que la chaîne vide est retournée
    }

    @Test
    fun testLongToFormattedString_withDatePattern() {
        // Un timestamp pour le 1er janvier 2022
        val timestamp = 1640995200000L // Correspond à 01/01/2022 01:00:00
        val expectedFormat = "01/01/2022"

        val result = longToFormatedString(timestamp, "dd/MM/yyyy")
        assertEquals(expectedFormat, result) // Vérifie que le format est correct
    }

    @Test
    fun testLongToFormattedString_withHourPattern() {
        val timestamp = 1640995200000L // Correspond à 01/01/2022 01:00:00
        val expectedFormat = "01:00"

        val result = longToFormatedString(timestamp, "HH:mm")
        assertEquals(expectedFormat, result) // Vérifie que le format est correct
    }

    @Test
    fun isDateInFuture_true(){
        // Ajoute 1 jour (en millisecondes) à l'heure actuelle
        val futureDateMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000L
        assertTrue(isDateInFuture(futureDateMillis)) // La date est dans le futur
    }


    @Test
    fun isDateInFuture_false(){
        // Soustrait 1 jour (en millisecondes) à l'heure actuelle
        val pastDateMillis = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
        assertFalse(isDateInFuture(pastDateMillis)) // La date est dans le passé
    }


}