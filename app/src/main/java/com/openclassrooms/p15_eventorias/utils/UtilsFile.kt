package com.openclassrooms.p15_eventorias.utils

import android.annotation.SuppressLint
import android.content.Context
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


const val CST_REP_IMG_PHOTO = "imgPhoto"

fun Context.getPathCacheImgPhoto(): File {
    return File(cacheDir, CST_REP_IMG_PHOTO)
}

/**
 * renvoie un objet File avec un nom de fichier unique qui peut être utilisé pour stocker l'image capturée.
 * Elle crée un fichier avec le préfixe « JPEG_ » et un horodatage,
 * et le stocke dans le répertoire de cache externe avec une extension « .jpg ».
 */
@SuppressLint("SimpleDateFormat")
fun Context.createImageFile(): File { // Foncion d'extension de la classe Context

    // Exemple cache interne (utilisé ici) : /data/user/0/com.openclassrooms.p15_eventorias/cache/JPEG_20240919_110610_5159691113524491717.jpg
    // mais sur l'émulateur : /data/data/com.openclassrooms.p15_eventorias/cache

    // Exemple cache externe (pas ici) : /storage/emulated/0/Android/data/com.openclassrooms.p15_eventorias/cache/JPEG_20240919_095757_3904348470821284962.jpg

    val imgPhotoDir = getPathCacheImgPhoto() // Besoin du contact pour cacheDir
    if (!imgPhotoDir.exists()) {
        if (!imgPhotoDir.mkdirs()) {
            throw IOException("Impossible de créer le répertoire imgPhoto")
        }
    }

    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "camera" + timeStamp + "_"
    val fileImage = File.createTempFile(
        imageFileName,      /* prefix */
        ".jpg",       /* suffix */
        imgPhotoDir
    )
    return fileImage



}

// Nettoie le cache (où sont stockées temporairement les photos prises avec l'appareil)
fun Context.clearCachePhoto() {
    val cacheDir = getPathCacheImgPhoto()
    if (cacheDir.isDirectory) {
        deleteDir(cacheDir)
    }
}

// Supprime un répertoire
fun deleteDir(dir: File?): Boolean {
    if (dir != null && dir.isDirectory) {

        val children = dir.list()
        if (children != null) {
            for (child in children) {
                val success = deleteDir(File(dir, child))
                if (!success) {
                    return false
                }
            }
        }
    }
    // Si le fichier n'est pas un dossier, il est supprimé directement
    return dir?.delete() ?: false
}