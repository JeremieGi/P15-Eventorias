package com.openclassrooms.p15_eventorias.repository.user

import com.google.firebase.firestore.PropertyName
import com.openclassrooms.p15_eventorias.model.User

/**
 * Je fais une classe DTO car la casse de certains membres de User ne semble pas respecté dans la base de données
 * Par exemple sEmail => semail en base
 * De plus faire un DTO laisse la classe Model indépendante, et ne fera pas de regression si jamais on modifie le nom d'un membre
 */

data class FirebaseUserDTO (

    @PropertyName("id")
    val id: String = "", // paramètre pas défaut pour pouvoir utiliser toObject

    /**
     * User's name.
     */
    @PropertyName("sname")
    val sName: String = "",


    /**
     * User's email.
     */
    @PropertyName("semail")
    val sEmail: String = "",


    /**
     * avatar.
     */
    @PropertyName("surlavatar")
    val sURLAvatar : String = "",

    /**
     * Activation des notifications
     */
    @get:PropertyName(FIELD_NOTIFICATION_ENABLED)
    @set:PropertyName(FIELD_NOTIFICATION_ENABLED)
    var bNotificationEnabled : Boolean = false


){

    companion object {

        // De préférence uniquement en minuscule (ici même nom que dans le DTO)
        const val FIELD_NOTIFICATION_ENABLED: String = "bnotificationenabled"

    }

    fun toModel(): User {

        return User(
            id = this.id,
            sName = this.sName,
            sEmail = this.sEmail,
            sURLAvatar = this.sURLAvatar,
            bNotificationEnabled = this.bNotificationEnabled
        )

    }
}

