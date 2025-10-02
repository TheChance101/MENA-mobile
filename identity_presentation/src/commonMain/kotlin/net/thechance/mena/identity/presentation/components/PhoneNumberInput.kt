package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_phone
import net.thechance.mena.designsystem.presentation.component.textField.MobileNumberLeadingContent
import net.thechance.mena.designsystem.presentation.component.textField.MobileNumberTextField
import net.thechance.mena.identity.presentation.components.util.LengthBasedPhoneVisualTransformation
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun PhoneNumberInput(
    countryCode: String,
    countryFlag: Painter,
    onCountryClick: () -> Unit,
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MobileNumberLeadingContent(
            countryCode = countryCode,
            countryPainter = countryFlag,
            onClick = onCountryClick
        )

        MobileNumberTextField(
            value = phoneNumber,
            leadingIcon = painterResource(Res.drawable.ic_phone),
            onValueChanged = { new ->
                val filtered = new.filter { it.isDigit() }
                onPhoneChange(filtered)
            },
            visualTransformation = LengthBasedPhoneVisualTransformation(
                masks = mapOf(
                    8 to "## ### ###",
                    9 to "### ### ###",
                    10 to "### ### ####",
                    11 to "#### #### ###",
                )
            ),
            hint = "",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.weight(1f)
        )
    }
}