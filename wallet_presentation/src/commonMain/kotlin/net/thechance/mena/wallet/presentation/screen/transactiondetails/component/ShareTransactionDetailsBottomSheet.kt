package net.thechance.mena.wallet.presentation.screen.transactiondetails.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.computer_phone_sync_icon
import mena.wallet_presentation.generated.resources.ic_computer_phone_sync
import mena.wallet_presentation.generated.resources.send_to_your_device
import mena.wallet_presentation.generated.resources.transaction_details_header
import mena.wallet_presentation.generated.resources.transaction_details_screenshot
import net.thechance.mena.designsystem.presentation.component.bottomSheet.BottomSheet
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

internal fun ScaffoldScope.shareTransactionDetailsBottomSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onSendToDeviceBtnClicked: () -> Unit,
     image: ImageBitmap,
){
    bottomSheet(isVisible = isVisible){
        BottomSheet(
            onDismissRequest = onDismissRequest,
            sheetContent = {
                ShareTransactionDetailsBottomSheetContent(
                    image = image,
                    onSendToDeviceBtnClicked = onSendToDeviceBtnClicked
                )
            }
        )
    }
}
@Composable
private fun ShareTransactionDetailsBottomSheetContent(
    image: ImageBitmap,
    onSendToDeviceBtnClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.transaction_details_header),
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Start
        )
        Image(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .width(160.dp)
                .height(190.dp)
                .clip(RoundedCornerShape(8.dp)),
            bitmap = image,
            contentDescription = stringResource(Res.string.transaction_details_screenshot),
        )
        Button(
            modifier = Modifier.align(Alignment.Start),
            onClick = onSendToDeviceBtnClicked,
            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
            containerColor = Theme.colorScheme.background.surfaceHigh,
            contentColor = Theme.colorScheme.primary.primary,
            shape = RoundedCornerShape(Theme.radius.full),
            content = {
                Icon(
                    modifier = Modifier.padding(end = 4.dp).size(12.dp),
                    painter = painterResource(Res.drawable.ic_computer_phone_sync),
                    contentDescription = stringResource(Res.string.computer_phone_sync_icon),
                    tint = Theme.colorScheme.primary.primary
                )
                Text(
                    text = stringResource(Res.string.send_to_your_device),
                    style = Theme.typography.label.small
                )
            }
        )
        Box(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .height(0.5.dp)
                .fillMaxWidth()
                .background(color = Theme.colorScheme.shadeTertiary)
        )
    }
}

@Preview
@Composable
private fun ShareTransactionDetailsBottomSheetContentPreview() {
    MenaTheme {
        ShareTransactionDetailsBottomSheetContent(
            image = ImageBitmap(100,100),
            onSendToDeviceBtnClicked = {}
        )
    }
}