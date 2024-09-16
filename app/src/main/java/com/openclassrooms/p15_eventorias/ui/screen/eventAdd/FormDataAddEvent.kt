package com.openclassrooms.p15_eventorias.ui.screen.eventAdd

import java.sql.Time
import java.util.Date

sealed class FormDataAddEvent {

    data class TitleChanged(val title: String) : FormDataAddEvent()
    data class DescriptionChanged(val description: String) : FormDataAddEvent()
    data class DateChanged(val date: Date) : FormDataAddEvent()
    data class TimeChanged(val date: Time) : FormDataAddEvent()
    data class AdressChanged(val adress: String) : FormDataAddEvent()
    data class PhotoChanged(val url: String) : FormDataAddEvent()


}