package com.openclassrooms.p15_eventorias.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

/**
 * renvoie un objet File avec un nom de fichier unique qui peut être utilisé pour stocker l'image capturée.
 * Elle crée un fichier avec le préfixe « JPEG_ » et un horodatage,
 * et le stocke dans le répertoire de cache externe avec une extension « .jpg ».
 */
fun Context.createImageFile(): File { // Foncion d'extension de la classe Context

    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val fileImage = File.createTempFile(
        imageFileName,      /* prefix */
        ".jpg",       /* suffix */
        externalCacheDir    /* directory (ici on a besoin du Context) */
    )
    return fileImage

}