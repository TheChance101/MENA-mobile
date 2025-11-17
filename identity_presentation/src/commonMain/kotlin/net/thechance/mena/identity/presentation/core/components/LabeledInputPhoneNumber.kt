package net.thechance.mena.identity.presentation.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.phone_number
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LabeledInputPhoneNumber(
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    countryCode: String,
    countryFlag: Painter,
    onClickCountry: () -> Unit,
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
                .padding(bottom = Theme.spacing._4)
        )
        PhoneNumberInput(
            countryCode = countryCode,
            countryFlag = countryFlag,
            onCountryClick = onClickCountry,
            phoneNumber = phoneNumber,
            onPhoneChange = onPhoneChange
        )
    }
}