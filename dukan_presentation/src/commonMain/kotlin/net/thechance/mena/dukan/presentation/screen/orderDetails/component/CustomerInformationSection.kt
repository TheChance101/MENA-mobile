package net.thechance.mena.dukan.presentation.screen.orderDetails.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.customer_information
import mena.dukan_presentation.generated.resources.ic_profile
import mena.dukan_presentation.generated.resources.profile_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewOrderDetailsUiState
import net.thechance.mena.dukan.presentation.util.visualTransformation.LengthBasedPhoneVisualTransformation
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun CustomerInformationSection(
    userName: String,
    userPhoneNumber: String,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = Theme.spacing._8),
            text = stringResource(Res.string.customer_information),
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(SquircleShape(Theme.spacing._12))
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(Theme.spacing._8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserProfileIcon()
            UserInformation(
                modifier = Modifier.weight(1f),
                name = userName,
                phoneNumber = userPhoneNumber
            )
        }
    }
}

@Composable
private fun UserProfileIcon() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(SquircleShape(Theme.spacing._12))
            .background(Theme.colorScheme.background.surfaceHigh)
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(Res.drawable.ic_profile),
            contentDescription = stringResource(Res.string.profile_icon),
            tint = Theme.colorScheme.primary.primary
        )
    }
}

@Composable
private fun UserInformation(
    name: String,
    phoneNumber: String,
    modifier: Modifier = Modifier
) {
    val formattedPhoneNumber = remember(phoneNumber) {
        val visualTransformation = LengthBasedPhoneVisualTransformation(LengthBasedPhoneVisualTransformation.phoneNumberMasks)
        visualTransformation.filter(AnnotatedString(phoneNumber))
    }

    Column(
        modifier = modifier
            .padding(start = Theme.spacing._8),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = name,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 1
        )
        Text(
            text = "+${formattedPhoneNumber.text.text}",
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary,
            maxLines = 1
        )
    }
}

@Preview
@Composable
private fun CustomerInformationSectionPreview() {
    MenaTheme {
        CustomerInformationSection(
            userName = PreviewOrderDetailsUiState.orderDetailsUiState.orderUiState.customerName,
            userPhoneNumber = PreviewOrderDetailsUiState.orderDetailsUiState.orderUiState.customerPhone,
            modifier = Modifier.padding(16.dp)
        )
    }
}