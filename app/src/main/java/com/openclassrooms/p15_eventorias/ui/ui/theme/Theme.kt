package com.openclassrooms.p15_eventorias.ui.ui.theme


import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


//// Pas de thème sombre dans le cahier des charges
//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80,
//    background = Color(0xFF1d1b20)
//)

private val LightColorScheme = lightColorScheme(

    primary = ColorButtonBackground,    // Fond des boutons par défaut
//    secondary = ColorButtonBackground,
//    tertiary = ColorButtonBackground,
    background = ColorBackground,
 //   surface = ColorBackground


)

@Composable
fun P15EventoriasTheme(
    //darkTheme: Boolean = isSystemInDarkTheme(),
    //dynamicColor: Boolean = true, // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {

    // Pas de thème sombre dans le cahier des charges

//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

object MyButtonStyle {
    @Composable
    fun buttonColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            disabledContainerColor = Color.Gray,  // Couleur de fond quand désactivé
            disabledContentColor = Color.Black   // Couleur du texte quand désactivé
        )
    }
}