package net.thechance.mena

import androidx.compose.runtime.Composable
import net.thechance.mena.identity.domain.util.AppTheme

@Composable
expect fun SetStatusBarAppearance(appTheme: AppTheme)
@Composable
expect fun SetNavigationBarAppearance(appTheme: AppTheme)
