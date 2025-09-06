package net.thechance.mena.designsystem.presentation.theme.color.scheme

import net.thechance.mena.designsystem.presentation.theme.color.palette.Black
import net.thechance.mena.designsystem.presentation.theme.color.palette.Coffee200
import net.thechance.mena.designsystem.presentation.theme.color.palette.Coffee600
import net.thechance.mena.designsystem.presentation.theme.color.palette.Coffee800
import net.thechance.mena.designsystem.presentation.theme.color.palette.Red50
import net.thechance.mena.designsystem.presentation.theme.color.palette.Red700
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray200
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray300
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray400
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray50
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray500
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray600
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray800
import net.thechance.mena.designsystem.presentation.theme.color.palette.Navy50
import net.thechance.mena.designsystem.presentation.theme.color.palette.Navy900
import net.thechance.mena.designsystem.presentation.theme.color.palette.Green50
import net.thechance.mena.designsystem.presentation.theme.color.palette.Green600
import net.thechance.mena.designsystem.presentation.theme.color.palette.Green700
import net.thechance.mena.designsystem.presentation.theme.color.palette.Yellow50
import net.thechance.mena.designsystem.presentation.theme.color.palette.Yellow600
import net.thechance.mena.designsystem.presentation.theme.color.palette.White
import net.thechance.mena.designsystem.presentation.theme.color.palette.White38
import net.thechance.mena.designsystem.presentation.theme.color.palette.White60

internal val LightColorScheme = ColorScheme(
    brand = ColorScheme.Brand(
        brand = Navy900,
        brandVariant = Navy50,
        onBrand = White
    ),
    primary = ColorScheme.Primary(
        primary = Black,
        onPrimary = White,
        onPrimaryBody = White60,
        onPrimaryHint = White38
    ),
    secondary = ColorScheme.Secondary(
        secondary = Coffee800,
        secondaryText = Coffee600,
        secondaryVariant = Coffee200
    ),
    border = ColorScheme.Border(
        disabled = Gray300,
        brand = Navy900,
        error = Red700,
        success = Green700
    ),
    background = ColorScheme.Background(
        surfaceLow = Gray50,
        surface = Gray200,
        surfaceHigh = Gray300,
        bgError = Red50,
        bgWarning = Yellow50,
        bgSuccess = Green50
    ),
    shadePrimary = Gray800,
    shadeSecondary = Gray600,
    shadeTertiary = Gray500,
    stroke = Gray300,
    textDisabled = Gray400,
    disabled = Gray300,
    error = Red700,
    warning = Yellow600,
    success = Green600
)