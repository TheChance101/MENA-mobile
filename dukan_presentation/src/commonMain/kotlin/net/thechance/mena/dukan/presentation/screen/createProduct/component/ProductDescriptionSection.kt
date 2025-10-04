package net.thechance.mena.dukan.presentation.screen.createProduct.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.description
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.MultiLineTextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.descriptionSection(
    description: String,
    isTextFieldEnabled: Boolean,
    onDescriptionChange: (String) -> Unit
) {
    item {
        Text(
            text = stringResource(Res.string.description),
            style = Theme.typography.title.medium,
            modifier = Modifier.padding(top = Theme.spacing._12)
                .padding(horizontal = Theme.spacing._16)

        )
        MultiLineTextField(
            modifier = Modifier
                .padding(top = Theme.spacing._4)
                .padding(horizontal = Theme.spacing._16)
                .height(96.dp),
            value = description,
            onValueChanged = onDescriptionChange,
            enabled = isTextFieldEnabled,
            hint = "",
        )
    }
}