package net.thechance.mena.designsystem.presentation.theme.color.scheme

import net.thechance.mena.designsystem.presentation.theme.color.White
import net.thechance.mena.designsystem.presentation.theme.color.colorPalette

internal val DarkColorScheme = ColorScheme(
    brand = ColorScheme.Brand(
        brand = colorPalette.navy.shade600,
        brandVariant = colorPalette.navy.shade500,
        onBrand = White
    ),
    primary = ColorScheme.Primary(
        primary = White,
        onPrimary = colorPalette.gray.shade700,
        onPrimaryBody = colorPalette.gray.shade600,
        onPrimaryHint = colorPalette.gray.shade400
    ),
    secondary = ColorScheme.Secondary(
        secondary = colorPalette.coffee.shade800,
        secondaryText = colorPalette.coffee.shade600,
        secondaryVariant = colorPalette.coffee.shade200
    ),
    border = ColorScheme.Border(
        disabled = colorPalette.gray.shade700,
        brand = colorPalette.navy.shade900,
        error = colorPalette.red.shade500,
        success = colorPalette.green.shade500
    ),
    background = ColorScheme.Background(
        surfaceLow = colorPalette.gray.shade800,
        surface = colorPalette.gray.shade900,
        surfaceHigh = colorPalette.gray.shade700,
        bgError = colorPalette.red.shade900,
        bgWarning = colorPalette.yellow.shade800,
        bgSuccess = colorPalette.green.shade800
    ),
    shadePrimary = colorPalette.gray.shade100,
    shadeSecondary = colorPalette.gray.shade400,
    shadeTertiary = colorPalette.gray.shade500,
    stroke = colorPalette.gray.shade700,
    textDisabled = colorPalette.gray.shade500,
    disabled = colorPalette.gray.shade600,
    error = colorPalette.red.shade300,
    warning = colorPalette.yellow.shade300,
    success = colorPalette.green.shade300
)