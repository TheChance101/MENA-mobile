package net.thechance.mena.dukan.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.dukan.presentation.home.components.TopAppBar
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen() {
    val viewModel = HomeViewModel()
    HomeContent(
        homeInteractionListener = viewModel
    )
}

@Composable
fun HomeContent(
    homeInteractionListener: HomeInteractionListener
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(color = Color(0xFF2F4F7))
    ) {
        TopAppBar(
            onAddDukanButtonClicked = homeInteractionListener::onAddDukanButtonClicked
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}