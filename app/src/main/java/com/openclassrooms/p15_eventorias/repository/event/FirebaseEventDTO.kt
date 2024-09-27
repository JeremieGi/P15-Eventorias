package com.openclassrooms.p15_eventorias.repository.event

import com.google.firebase.firestore.PropertyName
import com.openclassrooms.p15_eventorias.model.CoordinatesGPS
import com.openclassrooms.p15_eventorias.model.Event

data class FirebaseEventDTO (

    // J'ai été obligé de mettre les propriétés en var + @get:PropertyName et  @set:PropertyName ...

    @get:PropertyName(COLLECTION_EVENT_ID)
    @set:PropertyName(COLLECTION_EVENT_ID)
    var id: String = "",

    //@PropertyName("title")
    @get:PropertyName(COLLECTION_EVENT_TITLE)
    @set:PropertyName(COLLECTION_EVENT_TITLE)
    var sTitle : String = "",

    //@get:PropertyName("description")
    @get:PropertyName("description")
    @set:PropertyName("description")
    var sDescription : String = "",

    //@get:PropertyName("datetime")
    @get:PropertyName(COLLECTION_EVENT_DATETIME)
    @set:PropertyName(COLLECTION_EVENT_DATETIME)
    var lDatetime : Long = 0,

    @get:PropertyName("urleventpicture")
    @set:PropertyName("urleventpicture")
    var sURLEventPicture : String = "",

    @get:PropertyName("address")
    @set:PropertyName("address")
    var sAddressTest : String = "",

    @get:PropertyName("coordGPS")
    @set:PropertyName("coordGPS")
    var coordGPS  : CoordinatesGPS? = null,

    @get:PropertyName("urlphotoauthor")
    @set:PropertyName("urlphotoauthor")
    var sURLPhotoAuthor : String = ""

){

    companion object {

        // Pour ne pas maintenir ces noms de champs avec les @property du DTO + dans EventFirestoreAPI
        const val COLLECTION_EVENT_ID: String = "id"
        const val COLLECTION_EVENT_TITLE: String = "title"
        const val COLLECTION_EVENT_DATETIME: String = "datetime"

    }


    constructor(eventModeP : Event) : this (
        id = eventModeP.id,
        sTitle = eventModeP.sTitle,
        sDescription = eventModeP.sDescription,
        lDatetime = eventModeP.lDatetime,
        sURLEventPicture = eventModeP.sURLEventPicture,
        sAddressTest = eventModeP.sAddress,
        coordGPS = eventModeP.coordGPS,
        sURLPhotoAuthor = eventModeP.sURLPhotoAuthor,
    )


    fun toModel(): Event {

        return Event(
            id = this.id,
            sTitle = this.sTitle,
            sDescription = this.sDescription,
            lDatetime = this.lDatetime,
            sURLEventPicture = this.sURLEventPicture,
            sAddress = this.sAddressTest,
            coordGPS = this.coordGPS,
            sURLPhotoAuthor = this.sURLPhotoAuthor,
        )

    }
}