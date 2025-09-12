package net.thechance.mena.dukan.presentation.screen.createDukan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import net.thechance.mena.dukan.presentation.screen.createDukan.content.CreateDukanContent
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanViewModel

@Composable
fun CreateDukanScreen() {

    val viewModel: CreateDukanViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {

            else -> {}
        }
    }

    CreateDukanContent(
        state = state,
        listener = viewModel
    )
}