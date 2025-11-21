package net.thechance.mena.designsystem.presentation.theme.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.madimi_one_regular
import mena.design_system.generated.resources.ibm_plex_sans_arabic_medium
import mena.design_system.generated.resources.ibm_plex_sans_arabic_regular
import mena.design_system.generated.resources.ibm_plex_sans_arabic_semi_bold
import mena.design_system.generated.resources.poppins_medium
import mena.design_system.generated.resources.poppins_regular
import mena.design_system.generated.resources.poppins_semi_bold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.AppLanguage
import org.jetbrains.compose.resources.Font

@Composable
fun createThemeTypography(appLanguage: String): Typography {
    val poppinsFontFamily = FontFamily(
        Font(resource = Res.font.poppins_regular, FontWeight.Normal),
        Font(resource = Res.font.poppins_medium, FontWeight.Medium),
        Font(resource = Res.font.poppins_semi_bold, FontWeight.SemiBold),
    )
    val notoFontFamily = FontFamily(
        Font(resource = Res.font.ibm_plex_sans_arabic_regular, FontWeight.Normal),
        Font(resource = Res.font.ibm_plex_sans_arabic_medium, FontWeight.Medium),
        Font(resource = Res.font.ibm_plex_sans_arabic_semi_bold, FontWeight.SemiBold),
    )
    val fontFamily = when (appLanguage) {
        AppLanguage.English.iso -> poppinsFontFamily
        AppLanguage.Arabic.iso -> notoFontFamily
        else -> poppinsFontFamily
    }
    val madimiOneFontFamily = FontFamily(
        Font(
            resource = Res.font.madimi_one_regular,
            FontWeight.Normal
        )
    )

    return Typography(
        appName = TextStyle.Default.copy(
            fontSize = 28.sp,
            fontFamily = madimiOneFontFamily,
            color = Theme.colorScheme.brand.brand
        ),
        headline = Typography.Headline(
            large = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                lineHeight = 42.sp
            ),
            medium = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 36.sp
            ),
            small = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        ),
        title = Typography.Title(
            large = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 30.sp
            ),
            medium = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 28.sp
            ),
            small = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        ),
        body = Typography.Body(
            large = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 28.sp
            ),
            medium = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            small = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        ),
        label = Typography.Label(
            large = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            medium = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 22.sp
            ),
            small = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                lineHeight = 16.sp
            ),
            extraSmall = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                lineHeight = 16.sp
            )
        )
    )
}