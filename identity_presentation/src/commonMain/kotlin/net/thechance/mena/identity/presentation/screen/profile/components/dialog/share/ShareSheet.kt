package net.thechance.mena.identity.presentation.screen.profile.components.dialog.share

import androidx.compose.runtime.Composable

@Composable
expect fun ShareSheet(title: String, message: String, shareLink: String, onDismiss: () -> Unit)
