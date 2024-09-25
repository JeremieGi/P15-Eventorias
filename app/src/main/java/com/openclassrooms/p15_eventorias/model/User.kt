package com.openclassrooms.p15_eventorias.model

// TODO Denis : Je me demande si je garde les rubriques qui existent déjà dans FibaseUser => par exemple : nom, email, avatar. Ca peut faire des souçis de doublon

data class User(
    /**
     * Unique identifier for the User.
     */
    val id: String = "",

    /**
     * User's name.
     */
    val sName: String = "",


    /**
     * User's email.
     */
    val sEmail: String = "",


    /**
     * avatar.
     */
    val sURLAvatar : String = "",

    /**
     * Activation des notifications
     */
    var bNotificationEnabled : Boolean = false

)