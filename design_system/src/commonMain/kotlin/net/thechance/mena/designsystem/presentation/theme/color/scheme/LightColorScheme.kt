package net.thechance.mena.designsystem.presentation.theme.color.scheme

import net.thechance.mena.designsystem.presentation.theme.color.Black
import net.thechance.mena.designsystem.presentation.theme.color.White
import net.thechance.mena.designsystem.presentation.theme.color.White38
import net.thechance.mena.designsystem.presentation.theme.color.White60
import net.thechance.mena.designsystem.presentation.theme.color.colorPalette

internal val LightColorScheme = ColorScheme(
    brand = ColorScheme.Brand(
        brand = colorPalette.navy.shade900,
        brandVariant = colorPalette.navy.shade50,
        onBrand = White
    ),
    primary = ColorScheme.Primary(
        primary = Black,
        onPrimary = White,
        onPrimaryBody = White60,
        onPrimaryHint = White38
    ),
    secondary = ColorScheme.Secondary(
        secondary = colorPalette.coffee.shade800,
        secondaryText = colorPalette.coffee.shade600,
        secondaryVariant = colorPalette.coffee.shade200
    ),
    border = ColorScheme.Border(
        disabled = colorPalette.gray.shade300,
        brand = colorPalette.navy.shade900,
        error = colorPalette.red.shade700,
        success = colorPalette.green.shade700
    ),
    background = ColorScheme.Background(
        surfaceLow = colorPalette.gray.shade50,
        surface = colorPalette.gray.shade200,
        surfaceHigh = colorPalette.gray.shade300,
        bgError = colorPalette.red.shade50,
        bgWarning = colorPalette.yellow.shade50,
        bgSuccess = colorPalette.green.shade50
    ),
    shadePrimary = colorPalette.gray.shade800,
    shadeSecondary = colorPalette.gray.shade600,
    shadeTertiary = colorPalette.gray.shade500,
    stroke = colorPalette.gray.shade300,
    textDisabled = colorPalette.gray.shade300,
    disabled = colorPalette.gray.shade400,
    error = colorPalette.red.shade700,
    warning = colorPalette.yellow.shade600,
    success = colorPalette.green.shade600
)