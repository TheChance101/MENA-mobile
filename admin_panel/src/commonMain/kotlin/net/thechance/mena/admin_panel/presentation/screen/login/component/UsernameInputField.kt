package net.thechance.mena.admin_panel.presentation.screen.login.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_user
import net.thechance.mena.admin_panel.resources.username
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun UsernameInputField(
    username: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = stringResource(Res.string.username),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .heightIn(min = 48.dp),
            value = username,
            hint = "",
            onValueChanged = { onValueChange(it) },
            leadingIcon = painterResource(Res.drawable.ic_user),
            showTrailingDivider = false,
            visualTransformation = if (username.isNotEmpty()) AtPrefixTransformation
            else VisualTransformation.None
        )
    }
}

private object AtPrefixTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformedText = AnnotatedString("@" + text.text)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = offset + 1
            override fun transformedToOriginal(offset: Int): Int = (offset - 1).coerceAtLeast(0)
        }

        return TransformedText(transformedText, offsetMapping)
    }
}