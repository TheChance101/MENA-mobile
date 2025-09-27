package net.thechance.mena.trends.presentation.screen.video_description

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.add_video_description_subtitle
import mena.trends_presentation.generated.resources.add_video_description_title
import mena.trends_presentation.generated.resources.back_arrow
import mena.trends_presentation.generated.resources.characters
import mena.trends_presentation.generated.resources.characters_color_animation_label
import mena.trends_presentation.generated.resources.hint
import mena.trends_presentation.generated.resources.ic_arrow_left
import mena.trends_presentation.generated.resources.new_trend
import mena.trends_presentation.generated.resources.next
import mena.trends_presentation.generated.resources.page_number
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.MultiLineTextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun VideoDescriptionScreen(
    viewModel: VideoDescriptionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            is VideoDescriptionEffect.NavigateBack -> navController.navigateUp()
            is VideoDescriptionEffect.NavigateToSelectCategories -> {} ///TODO navigate to category screen
        }
    }

    VideoDescriptionContent(
        state = state,
        listener = viewModel,
    )
}

@Composable
private fun VideoDescriptionContent(
    state: VideoDescriptionScreenState,
    listener: VideoDescriptionInteractionListener,
) {
    val charactersLimitColor by animateColorAsState(
        targetValue =
            if (state.currentNumberOfCharacters <= state.maxNumberOfCharacters)
                Theme.colorScheme.shadeTertiary
            else
                Theme.colorScheme.error,
        label = stringResource(Res.string.characters_color_animation_label)
    )

    Scaffold(
        topBar = { DescriptionAppBar(onBackClick = listener::onBackClick) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.add_video_description_title),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.padding(top = Theme.spacing._16, start = Theme.spacing._16, end = Theme.spacing._16)
            )

            Text(
                text = stringResource(Res.string.add_video_description_subtitle),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeSecondary,
                modifier = Modifier.padding(top = Theme.spacing._4, start = Theme.spacing._16, end = Theme.spacing._16)
            )

            DescriptionBox(
                value = state.description,
                onValueChanged = { listener.onDescriptionChanged(it) },
                currentNumberOfCharacters = state.currentNumberOfCharacters,
                maxNumberOfCharacters = state.maxNumberOfCharacters,
                currentNumberOfCharactersColor = charactersLimitColor,
                modifier = Modifier
                    .padding(top = Theme.spacing._24, start = Theme.spacing._16, end = Theme.spacing._16)
                    .height(187.dp)
                    .clip(shape = RoundedCornerShape(Theme.radius.md))
                    .background(Theme.colorScheme.background.surfaceLow)
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(Res.string.next),
                onClick = listener::onNextClick,
                contentPadding = PaddingValues(vertical = Theme.spacing._8),
                isEnabled = state.isButtonEnabled,
                modifier = Modifier.fillMaxWidth().padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._16)
                    .height(48.dp)
            )
        }
    }
}

@Composable
private fun DescriptionAppBar(onBackClick: () -> Unit) {
    AppBar(
        title = stringResource(Res.string.new_trend),
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow)
            )
        },
        trailingContent = {
            Chip(
                text = stringResource(Res.string.page_number, 2, 3),
                isSelected = false,
                isEnabled = true,
                onClick = {},
            )
        },
        onLeadingClick = onBackClick
    )
}

@Composable
private fun DescriptionBox(
    value: String,
    onValueChanged: (String) -> Unit,
    currentNumberOfCharacters: Int,
    maxNumberOfCharacters: Int,
    currentNumberOfCharactersColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        MultiLineTextField(
            value = value,
            onValueChanged = onValueChanged,
            hint = stringResource(Res.string.hint),
            maxLines = Int.MAX_VALUE,
            modifier = Modifier.align(Alignment.TopCenter)
                .heightIn(max = 190.dp)
                .verticalScroll(rememberScrollState())
        )

        Text(
            text = stringResource(
                Res.string.characters,
                currentNumberOfCharacters,
                maxNumberOfCharacters
            ),
            style = Theme.typography.label.small,
            color = currentNumberOfCharactersColor,
            modifier = Modifier.align(Alignment.BottomEnd).padding(Theme.spacing._12)
        )
    }
}

@Preview
@Composable
private fun VideoDescriptionScreenPreview() {
    MenaTheme{
        VideoDescriptionContent(
            state = VideoDescriptionScreenState(),
            listener = object : VideoDescriptionInteractionListener {
                override fun onBackClick() {}
                override fun onNextClick() {}
                override fun onDescriptionChanged(newValue: String) {}
            }
        )
    }
}