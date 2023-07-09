package `in`.v89bhp.checkablenotes.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import `in`.v89bhp.checkablenotes.R


val comfortaaFontFamily = FontFamily(
    listOf(
        Font(R.font.comfortaa_light, FontWeight.Light),
        Font(R.font.comfortaa_regular, FontWeight.Normal),
        Font(R.font.comfortaa_bold, FontWeight.Bold)
    ),
)

// Custom typography:
val typography = Typography(
    displayLarge = TextStyle(
        fontFamily = comfortaaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp
    ),
    displayMedium = TextStyle(
        fontFamily = comfortaaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp
    ),
    displaySmall = TextStyle(
        fontFamily = comfortaaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),


    headlineLarge = TextStyle(
        fontFamily = comfortaaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = comfortaaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(fontFamily = comfortaaFontFamily, fontSize = 24.sp),


    titleLarge = TextStyle(fontFamily = comfortaaFontFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleMedium = TextStyle(
        fontFamily = comfortaaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(fontFamily = comfortaaFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp),


    bodyLarge = TextStyle(fontFamily = comfortaaFontFamily, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = comfortaaFontFamily, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = comfortaaFontFamily, fontSize = 12.sp),


    labelLarge = TextStyle(fontFamily = comfortaaFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    labelMedium = TextStyle(
        fontFamily = comfortaaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(fontFamily = comfortaaFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp),
)