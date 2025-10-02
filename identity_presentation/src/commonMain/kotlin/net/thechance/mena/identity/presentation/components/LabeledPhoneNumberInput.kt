package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.phone_number
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LabeledPhoneNumberInput(
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    countryCode: String,
    countryFlag: Painter,
    onCountryClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(Res.string.phone_number)
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        PhoneNumberInput(
            countryCode = countryCode,
            countryFlag = countryFlag,
            onCountryClick = onCountryClick,
            phoneNumber = phoneNumber,
            onPhoneChange = onPhoneChange
        )
    }
}