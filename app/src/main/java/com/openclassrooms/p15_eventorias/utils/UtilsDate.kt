package com.openclassrooms.p15_eventorias.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Convertit un long en date textuelle (TODO JG : Test unitaire possible à faire)
 * @param timestamp : Date in long format
 * @param sPattern : Pattern Ex "MMMM dd, yyyy" ou "hh:mm" :  https://developer.android.com/reference/android/icu/text/SimpleDateFormat
 */
fun longToFormatedString(timestamp: Long, sPattern : String): String {

    // Créer un objet Date à partir du timestamp
    val date = Date(timestamp * 1000) // Multiplier par 1000 car le timestamp est en seconde, et Date utilise les millisecondes

    // Formater la date selon le modèle souhaité
    val formatter = SimpleDateFormat(sPattern, Locale.US)

    return formatter.format(date)

}

