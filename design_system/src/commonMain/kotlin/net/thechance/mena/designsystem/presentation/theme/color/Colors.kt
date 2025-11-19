package net.thechance.mena.designsystem.presentation.theme.color

import androidx.compose.ui.graphics.Color
import net.thechance.mena.designsystem.presentation.theme.color.palette.ColorPalette
import net.thechance.mena.designsystem.presentation.theme.color.palette.ColorPalette.ColorScale

val Black: Color = Color(0xFF000000)
val colorPalette = ColorPalette(
    navy = ColorScale(
        shade50 = Color(0xFFE6EBFA),
        shade100 = Color(0xFFc3cfe1),
        shade200 = Color(0xFFA1AEC4),
        shade300 = Color(0xFF7F8EA8),
        shade400 = Color(0xFF677793),
        shade500 = Color(0xFF4E6180),
        shade600 = Color(0xFF415470),
        shade700 = Color(0xFF32425A),
        shade800 = Color(0xFF233045),
        shade900 = Color(0xFF111D2E)
    ),
    coffee = ColorScale(
        shade50 = Color(0xFFFCF9F5),
        shade100 = Color(0xFFF5F0E9),
        shade200 = Color(0xFFF0E7DA),
        shade300 = Color(0xFFE0D1BC),
        shade400 = Color(0xFFCCB89B),
        shade500 = Color(0xFFB29B79),
        shade600 = Color(0xFF997D56),
        shade700 = Color(0xFF806338),
        shade800 = Color(0xFF66481F),
        shade900 = Color(0xFF4D330F)
    ),
    gray = ColorScale(
        shade50 = Color(0xFFFFFFFF),
        shade100 = Color(0xFFF8FAFC),
        shade200 = Color(0xFFFF2F4F7),
        shade300 = Color(0xFFEAECF0),
        shade400 = Color(0xFFBEC0CC),
        shade500 = Color(0xFF818599),
        shade600 = Color(0xFF3E4252),
        shade700 = Color(0xFF12141C),
        shade800 = Color(0xFF0E1017),
        shade900 = Color(0xFF000000),
    ),
    red = ColorScale(
        shade50 = Color(0xFFFEE4E2),
        shade100 = Color(0xFFFEE4E2),
        shade200 = Color(0xFFFECDCA),
        shade300 = Color(0xFFFDA29B),
        shade400 = Color(0xFFF97066),
        shade500 = Color(0xFFF04438),
        shade600 = Color(0xFFD92D20),
        shade700 = Color(0xFFB42318),
        shade800 = Color(0xFF912018),
        shade900 = Color(0xFF7A271A)
    ),
    yellow = ColorScale(
        shade50 = Color(0xFFFFFAEB),
        shade100 = Color(0xFFFEF0C7),
        shade200 = Color(0xFFFEDF89),
        shade300 = Color(0xFFFEC84B),
        shade400 = Color(0xFFFDB022),
        shade500 = Color(0xFFF79009),
        shade600 = Color(0xFFDC6803),
        shade700 = Color(0xFFB54708),
        shade800 = Color(0xFF93370D),
        shade900 = Color(0xFF7A2E0E)
    ),
    green = ColorScale(
        shade50 = Color(0xFFE6F6EA),
        shade100 = Color(0xFFC3E8CB),
        shade200 = Color(0xFF9BD9A9),
        shade300 = Color(0xFF71CB87),
        shade400 = Color(0xFF4EBF6D),
        shade500 = Color(0xFF23B353),
        shade600 = Color(0xFF19A44A),
        shade700 = Color(0xFF06923E),
        shade800 = Color(0xFF008133),
        shade900 = Color(0xFF00621F)
    )
)
internal val White = Color(0xFFFFFFFF)
internal val White60 = White.copy(alpha = 0.6f)
internal val White38 = White.copy(alpha = 0.38f)