package net.thechance.mena.dukan.presentation.screen.orderDetails

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.orderDetails.component.CustomerInformationSection
import net.thechance.mena.dukan.presentation.screen.orderDetails.component.DeliveryAddressSection
import net.thechance.mena.dukan.presentation.screen.orderDetails.component.OrderDetailsList
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OrderDetailsScreen() {
    OrderDetailsContent()
}

@Composable
private fun OrderDetailsContent() {
    Scaffold(
        topBar = {
            AppBar(
                title = "Order #", // Todo add order number Id
                onLeadingClick = {}, // Todo add back action
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._16,
                    vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_arrow),
                        tint = Theme.colorScheme.primary.primary
                    )
                },
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = Theme.spacing._16,
                vertical = Theme.spacing._12
            )
        ) {
            item {
                OrderDetailsList()
            }
            item {
                DeliveryAddressSection(
                    address = "123 Main St, City, Country", // Todo add real address
                    onClick = {}, // Todo add change address action
                    modifier = Modifier
                        .padding(top = Theme.spacing._24)
                )
            }
            item {
                CustomerInformationSection(
                    userName = "John Doe", // Todo add real user name
                    userPhoneNumber = "+1234567890", // Todo add real phone number
                    modifier = Modifier
                        .padding(top = Theme.spacing._12)
                )
            }
        }
    }
}

@Preview
@Composable
private fun OrderDetailsScreenPreview() {
    MenaTheme {
        OrderDetailsContent()
    }
}