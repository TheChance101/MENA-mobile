package net.thechance.mena.designsystem.presentation.theme.color.scheme

import net.thechance.mena.designsystem.presentation.theme.color.palette.Black
import net.thechance.mena.designsystem.presentation.theme.color.palette.Coffee600
import net.thechance.mena.designsystem.presentation.theme.color.palette.Coffee900
import net.thechance.mena.designsystem.presentation.theme.color.palette.Red400
import net.thechance.mena.designsystem.presentation.theme.color.palette.Red500
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray100
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray300
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray400
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray500
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray700
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray800
import net.thechance.mena.designsystem.presentation.theme.color.palette.Gray900
import net.thechance.mena.designsystem.presentation.theme.color.palette.Navy900
import net.thechance.mena.designsystem.presentation.theme.color.palette.Green400
import net.thechance.mena.designsystem.presentation.theme.color.palette.Green500
import net.thechance.mena.designsystem.presentation.theme.color.palette.Yellow400
import net.thechance.mena.designsystem.presentation.theme.color.palette.Yellow500

internal val DarkColorScheme = ColorScheme(
    brand = ColorScheme.Brand(
        brand = Gray700,
        brandVariant = Gray800,
        onBrand = Gray800
    ),
    primary = ColorScheme.Primary(
        primary = Black,
        onPrimary = Black,
        onPrimaryBody = Black,
        onPrimaryHint = Black
    ),
    secondary = ColorScheme.Secondary(
        secondary = Coffee600,
        secondaryText = Coffee600,
        secondaryVariant = Coffee900
    ),
    border = ColorScheme.Border(
        disabled = Gray700,
        brand = Navy900,
        error = Red500,
        success = Green500
    ),
    background = ColorScheme.Background(
        surfaceLow = Gray900,
        surface = Gray800,
        surfaceHigh = Gray700,
        bgError = Red500,
        bgWarning = Yellow500,
        bgSuccess = Green500
    ),
    shadePrimary = Gray100,
    shadeSecondary = Gray300,
    shadeTertiary = Gray400,
    stroke = Gray400,
    textDisabled = Gray500,
    disabled = Gray500,
    error = Red400,
    warning = Yellow400,
    success = Green400,
)