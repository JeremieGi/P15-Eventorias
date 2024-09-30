package com.openclassrooms.p15_eventorias.model

data class User(

    /**
     * Unique identifier for the User.
     */
    val id: String,

    /**
     * User's name.
     */
    val sName: String,


    /**
     * User's email.
     */
    val sEmail: String,


    /**
     * avatar.
     */
    val sURLAvatar : String,

    /**
     * Activation des notifications
     */
    var bNotificationEnabled : Boolean

)