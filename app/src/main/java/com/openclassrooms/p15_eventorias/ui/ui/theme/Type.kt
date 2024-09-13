package com.openclassrooms.p15_eventorias.ui.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/*
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val bodyFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Roboto"),
        fontProvider = provider,
    )
)

val displayFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Inter"),
        fontProvider = provider,
    )
)
*/

val fontProject = FontFamily.Default // TODO Denis : Comment mettre la font "Inter" ici ? (Voir tentative ci-dessus)


// Set of Material typography styles to start with
val Typography = Typography(



    // Titre des fenêtres
    titleLarge = TextStyle(
        fontFamily = fontProject,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.2.sp,
        letterSpacing = 0.sp,
        color = Color.White
    ),

    // Titre des évènements dans la liste d'évènements
    titleMedium = TextStyle(
        fontFamily = fontProject,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = ColorCustomGrey
    ),


    // Date des évènements dans la liste d'évènements
    bodyMedium = TextStyle(
        fontFamily = fontProject,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
        color = ColorCustomGrey
    ),

    // Adresse des évènements / Date
    bodyLarge = TextStyle(
        fontFamily = fontProject,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = Color.White
    ),

    // Contenu des champs de saisie
    labelLarge = TextStyle(
        fontFamily = fontProject,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
        color = ColorCustomGrey
    ),

    // Entete des champs de saisie
    labelMedium = TextStyle(
        fontFamily = fontProject,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
        color = ColorInputHead
    ),

    // Bouton
    displayMedium =TextStyle(
        fontFamily = fontProject,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 19.36.sp,
        letterSpacing = 0.sp,
        color = Color.White
    )




)