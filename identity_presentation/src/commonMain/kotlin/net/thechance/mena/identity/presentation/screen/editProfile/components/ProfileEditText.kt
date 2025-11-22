package net.thechance.mena.identity.presentation.screen.editProfile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.user_rounded
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource


@Composable
fun ProfileEditText(
    title: String,
    value: String, onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = Modifier.padding(top = Theme.spacing._16)) {
        TextField(
            modifier = Modifier.padding(top = Theme.spacing._4),
            value = value,
            hint = "",
            title = title,
            onValueChanged = onValueChange,
            leadingIcon = painterResource(Res.drawable.user_rounded),
            visualTransformation = visualTransformation,
            maxCharacters = 32
        )
    }
}


object AtPrefixTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformedText = AnnotatedString("@" + text.text)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = offset + 1
            override fun transformedToOriginal(offset: Int): Int = (offset - 1).coerceAtLeast(0)
        }

        return TransformedText(transformedText, offsetMapping)
    }
}