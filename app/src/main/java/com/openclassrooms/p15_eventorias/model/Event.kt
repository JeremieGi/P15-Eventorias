package com.openclassrooms.p15_eventorias.model

import java.util.Date

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
    val sAdress : String = "", // TODO Denis : Type ?

    /**
     * User object representing the creator of the event.
     */
    val userCreatorEvent : User? = null,

)