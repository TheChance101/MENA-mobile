package net.thechance.mena.identity.presentation.screen.profile.components.share

import androidx.compose.runtime.Composable

@Composable
expect fun ShareSheet(title: String, url: String, onDismiss: () -> Unit)
