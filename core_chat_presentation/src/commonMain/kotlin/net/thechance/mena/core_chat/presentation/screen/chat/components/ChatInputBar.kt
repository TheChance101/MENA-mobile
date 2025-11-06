package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_add
import mena.core_chat_presentation.generated.resources.message_holder
import mena.core_chat_presentation.generated.resources.ic_mic
import mena.core_chat_presentation.generated.resources.ic_send
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.textField.MultiLineTextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatInputBar(
    userInput: String,
    onTextChange: (String) -> Unit,
    onSendButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    onVoiceRecordClick: () -> Unit = {},
    onAttachButtonClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._2),
    ) {
        Row(
            modifier = Modifier
                .background(
                    shape = RoundedCornerShape(Theme.radius.md),
                    color = Theme.colorScheme.background.surfaceLow
                )
                .padding(end = Theme.spacing._12).defaultMinSize(minHeight = 48.dp).weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MultiLineTextField(
                value = userInput,
                onValueChanged = onTextChange,
                hint = stringResource(Res.string.message_holder),
                modifier = Modifier.weight(1f),
                minLines = 1,
                maxLines = 5
            )
            AnimatedVisibility(
                visible = userInput.isBlank(),
                enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth * 2 },
                    animationSpec = tween(400)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth * 2 },
                    animationSpec = tween(400)
                )
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_mic),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).clickable(
                        onClick = onVoiceRecordClick, indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    )
                )
            }
        }
        Crossfade(targetState = userInput.isBlank(), modifier = Modifier.align(Alignment.Bottom)) {
            if (it)
                AttachButton(onAttachButtonClick)
            else
                FabButton(
                    painter = painterResource(Res.drawable.ic_send),
                    modifier = Modifier.size(52.dp, 48.dp),
                    onClick = onSendButtonClick,
                    iconSize = 20.dp
                )
        }

    }
}

@Composable
private fun AttachButton(onClick: () -> Unit) {
    Box(
        Modifier.size(52.dp, 48.dp).border(
            width = 1.dp, color = Theme.colorScheme.border
                .disabled,
            shape = RoundedCornerShape(Theme.radius.md)
        ).clip(RoundedCornerShape(Theme.radius.md))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_add),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}


@Preview
@Composable
fun ChatInputBarPreview() {
    MenaTheme {
        Column {
            ChatInputBar("", {}, {})
            ChatInputBar("Hi Noor", {}, {})
            ChatInputBar(" 1 \n 2 \n 3 \n 4 \n 5 \n 6", {}, {})
        }
    }
}
