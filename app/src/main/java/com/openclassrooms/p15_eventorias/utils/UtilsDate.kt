package com.openclassrooms.p15_eventorias.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Convertit un long en date textuelle (TODO JG : Test unitaire possible à faire)
 * @param timestampInMs : Date in long format
 * @param sPattern : Pattern Ex "MMMM dd, yyyy" ou "HH:mm" :  https://developer.android.com/reference/android/icu/text/SimpleDateFormat
 */
fun longToFormatedString(timestampInMs: Long, sPattern : String): String {

    // Si timestamp = 0
    if (timestampInMs==0L){
        // On affiche chaine vide, au lieu de 1er janvier 1970 12h
        return ""
    }
    else{
        // Créer un objet Date à partir du timestamp
        val date = Date(timestampInMs)

        // Formater la date selon le modèle souhaité
        val formatter = SimpleDateFormat(sPattern, Locale.US)

        return formatter.format(date)
    }


}

/**
 * Renvoie vrai si la date est dans le futur
 */
fun isDateInFuture(dateMillis: Long): Boolean {
    val currentTimeMillis = System.currentTimeMillis()
    return dateMillis > currentTimeMillis
}
