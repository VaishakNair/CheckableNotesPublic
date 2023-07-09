package `in`.v89bhp.checkablenotes.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import `in`.v89bhp.checkablenotes.R


val fontFamily = FontFamily(
    listOf(
        Font(R.font.coco_gothic_light, FontWeight.Light),
        Font(R.font.coco_gothic_regular, FontWeight.Normal),
        Font(R.font.coco_gothic_bold, FontWeight.Bold)
    ),
)

// Set of Material typography styles to start with
val typography = Typography(
    displayLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
    displayMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
    displaySmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold),

    headlineLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontFamily = fontFamily),

    titleLarge = TextStyle(fontFamily = fontFamily),
    titleMedium = TextStyle(fontFamily = fontFamily),
    titleSmall = TextStyle(fontFamily = fontFamily),

    bodyLarge = TextStyle(fontFamily = fontFamily),
    bodyMedium = TextStyle(fontFamily = fontFamily),
    bodySmall = TextStyle(fontFamily = fontFamily),

    labelLarge = TextStyle(fontFamily = fontFamily),
    labelMedium = TextStyle(fontFamily = fontFamily),
    labelSmall = TextStyle(fontFamily = fontFamily),
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp
//    )

)