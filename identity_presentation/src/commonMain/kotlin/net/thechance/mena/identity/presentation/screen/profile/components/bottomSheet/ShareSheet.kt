package net.thechance.mena.identity.presentation.screen.profile.components.bottomSheet

import androidx.compose.runtime.Composable

@Composable
expect fun ShareSheet(title: String, url: String, onDismiss: () -> Unit)
