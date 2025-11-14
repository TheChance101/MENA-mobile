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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_profile
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.visualTransformation.LengthBasedPhoneVisualTransformation
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CustomerInformationSection(
    userName:String,
    userPhoneNumber:String,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = Theme.spacing._8),
            text = "Customer Information", // Todo add string resource
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(Theme.spacing._12))
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
            .clip(RoundedCornerShape(Theme.spacing._12))
            .background(Theme.colorScheme.background.surface)
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(Res.drawable.ic_profile),
            contentDescription = ""
        )
    }
}

@Composable
private fun UserInformation(
    name: String,
    phoneNumber: String,
    modifier: Modifier = Modifier
) {
    val phoneVisualTransformation = LengthBasedPhoneVisualTransformation(phoneNumberMasks)
    val formattedPhoneNumber = phoneVisualTransformation.filter(AnnotatedString(phoneNumber))

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
            userName = "John Doe",
            userPhoneNumber = "+1234567890",
            modifier = Modifier.padding(16.dp)
        )
    }
}

private val phoneNumberMasks = mapOf(
    8 to "##\u00A0###\u00A0###",
    11 to "###\u00A0####\u00A0####",
    12 to "##\u00A0###\u00A0###\u00A0####",
    13 to "###\u00A0###\u00A0####\u00A0###",
    14 to "###\u00A0###\u00A0####\u00A0####",
)
