package net.thechance.mena.dukan.presentation.screen.createProduct.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.description
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.MultiLineTextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DescriptionSection(
    description: String,
    isTextFieldEnabled: Boolean,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.description),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(horizontal = Theme.spacing._16)
        )
        MultiLineTextField(
            modifier = Modifier
                .padding(top = Theme.spacing._4)
                .padding(horizontal = Theme.spacing._16),
            value = description,
            onValueChanged = onDescriptionChange,
            enabled = isTextFieldEnabled,
            hint = "",
            maxLines = 6,
            minLines = 3
        )
    }
}

@Preview
@Composable
private fun DescriptionSectionPreview() {
    MenaTheme {
        DescriptionSection(
            description = "This is a description",
            isTextFieldEnabled = true,
            onDescriptionChange = {}
        )
    }
}