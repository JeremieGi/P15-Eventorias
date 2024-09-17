package com.openclassrooms.p15_eventorias.model

import android.content.Context
import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.utils.getCoordinatesFromAddress


data class Event (

    /**
     * Unique identifier for the event.
     */
    val id: String = "",

    val sTitle : String = "",

    val sDescription : String = "",

    // pas de champ Datetime dans Firebase => Long
    val lDatetime : Long = 0,

    /**
     * URL (Firestore) de l'image de l'évènement.
     */
    val sURLEventPicture : String = "",

    /**
     * Adresse de l'évènement.
     */
    val sAdress : String = "",
    // On va stocker les coordonnées GPS à la création de l'évènement (çà évitera d'appeler geocode à chaque affichage de l'écran de détail)
    var coordGPS  : CoordinatesGPS? = null,

    /**
     * User object representing the creator of the event.
     */
    //val userCreatorEvent : User? = null,
    // Seule la photo est utile
    // De plus dans Firebase, on expose pas tous les champs d'un profil
    val sURLPhotoAuthor : String = ""

) {

    /**
     * Géolocation de l'adresse.
     * Si l'adresse est géolocalisé, renvoie "" et les coordonnées GPS sont insérés dans l'Event.
     * Sinon un message d'erreur est retourné
     */
    suspend fun geolocate(context: Context): String {

        // Vérification que l'adresse textuelle est remplie
        if (this.sAdress.isEmpty()){
            return context.getString(R.string.eventNoAddress)
        }
        else{

            try{
                // Appel à l'APi Google
                val coorGPS = getCoordinatesFromAddress(this.sAdress)
                if (coorGPS!=null){
                    this.coordGPS = coorGPS
                }
                return ""
            }
            catch (e: Exception) {
                return e.message.toString()
            }

        }

    }
}