package net.thechance.mena.dukan.presentation.screen.createDukan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import net.thechance.mena.dukan.presentation.screen.createDukan.content.CreateDukanContent
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateDukanScreen(
    viewModel: CreateDukanViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()


    CreateDukanContent(
        state = state,
        listener = viewModel
    )
}