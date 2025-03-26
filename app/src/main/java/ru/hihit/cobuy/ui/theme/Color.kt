package ru.hihit.cobuy.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

val LightBackground = Color(0xFFEFEAE3)
val LightPrimaryContainer = Color(0xFFF4EEE8)
val LightPrimary = Color(0xFFC7C1B8)
val LightSecondary = Color(0xFFB4B0AD)
val LightText = Color(0xFF2D2D2D)
val LightLightText = Color(0xFF6F6F6F)
val LightDivider = Color(0xFFC7C1B8)
val LightError = Color(0xFFEF8888)
val LightInfo = Color(0xFF5EDEDE)

// Dark theme colors
val DarkBackground = Color(0xFF585653)
val DarkPrimaryContainer = Color(0xFF4B4A48)
val DarkPrimary = Color(0xFF888178)
val DarkSecondary = Color(0xFFC5C5C5)
val DarkText = Color(0xFFF4F4F4)
val DarkLightText = Color(0xFFC5C5C5)
val DarkDivider = Color(0xFF888178)
val DarkError = Color(0xFFC66666)
val DarkInfo = Color(0xFF59A3A3)

val productsColors = arrayOf(
    Color(0xFFD9D9D9),
    Color(0xFF808080),
    Color(0xFFE2E48D),
    Color(0xFF989978),
    Color(0xFFECA0C9),
    Color(0xFFB17093),
    Color(0xFF70C9DC),
    Color(0xFF3C6F7A),
    Color(0xFF6394CD),
    Color(0xFF5A7AA0),
    Color(0xFFEF8888),
    Color(0xFFC66666),
    Color(0xFF63CD81),
    Color(0xFF3C724B),
    Color(0xFFBB70D5),
    Color(0xFF875F95)
)


fun getColorByHash(input: String): Color {
    val hash = abs(input.hashCode())
    val index = hash % productsColors.size
    return productsColors[index]
}


/**
light_theme:
background: #EFEAE3
primary-container: #F4EEE8
primary: #C7C1B8
secondary: #B4B0AD
text: #2D2D2D
light-text: #6F6F6F

dark_theme:
background: #585653
primary-container: #4B4A48
primary: #888178
secondary: #C5C5C5
text: #F4F4F4
light-text: #C5C5C5
 */



/**

 */


