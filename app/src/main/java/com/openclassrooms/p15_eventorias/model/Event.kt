package com.openclassrooms.p15_eventorias.model


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
    val coordGPS  : CoordinatesGPS? = null,

    /**
     * User object representing the creator of the event.
     */
    //val userCreatorEvent : User? = null,
    // Seule la photo est utile
    // De plus dans Firebase, on expose pas tous les champs d'un profil
    val sURLPhotoAuthor : String = ""

)