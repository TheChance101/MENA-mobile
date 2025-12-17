package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.amount
import mena.core_chat_presentation.generated.resources.ic_coin
import mena.core_chat_presentation.generated.resources.ic_wallet_add
import mena.core_chat_presentation.generated.resources.send
import mena.core_chat_presentation.generated.resources.send_a_money
import net.thechance.mena.core_chat.presentation.screen.chat.AttachmentsSendMoneyInteractionListener
import net.thechance.mena.designsystem.presentation.component.bottomSheet.BottomSheet
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun ScaffoldScope.attachmentsSendMoneyBottomSheet(
    isVisible: Boolean,
    value: String,
    isLoading: Boolean,
    isSendMoneyButtonEnabled: Boolean,
    attachmentsInteractionListener: AttachmentsSendMoneyInteractionListener,
    modifier: Modifier = Modifier,
) {
    bottomSheet(isVisible) { showBottomSheet ->
        BottomSheet(
            isVisible = showBottomSheet,
            onDismissRequest = attachmentsInteractionListener::onDismissSendMoneyDialog,
            modifier = modifier
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(
                        color = Theme.colorScheme.background.surface,
                        shape = RoundedCornerShape(
                            topStart = Theme.radius.xl,
                            topEnd = Theme.radius.xl
                        )
                    )
                    .padding(horizontal = Theme.spacing._16),
            ) {
                Text(
                    modifier = Modifier.padding(vertical = Theme.spacing._16),
                    text = stringResource(Res.string.send_a_money),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary
                )
                Text(
                    modifier = Modifier.padding(bottom = Theme.spacing._4),
                    text = stringResource(Res.string.amount),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary
                )
                TextField(
                    value = value,
                    hint = "",
                    onValueChanged = { input ->
                        val numericRegex = Regex("^\\d*([.])?\\d*$")
                        if (input.isEmpty() || numericRegex.matches(input)) {
                            attachmentsInteractionListener.onValueChanged(input)
                        }
                    },
                    modifier = Modifier.padding(bottom = Theme.spacing._16).fillMaxWidth(),
                    leadingIcon = painterResource(Res.drawable.ic_wallet_add),
                    trailingIcon = painterResource(Res.drawable.ic_coin),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                PrimaryButton(
                    text = stringResource(Res.string.send),
                    onClick = attachmentsInteractionListener::onSendClicked,
                    modifier = Modifier.padding(bottom = Theme.spacing._24).fillMaxWidth(),
                    isEnabled = isSendMoneyButtonEnabled,
                    isLoading = isLoading
                )
            }
        }
    }
}

