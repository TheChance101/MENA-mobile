package net.thechance.mena.designsystem.presentation.component.appBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.app_coin_icon
import mena.design_system.generated.resources.app_name
import mena.design_system.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun HomeAppBar(
    coins: String,
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.app_name),
    trailingContent: (@Composable (String) -> Unit)? = null,
    titleColor: Color = Theme.colorScheme.shadePrimary,
    homeCoinTextColor: Color = Theme.colorScheme.shadeSecondary,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 18.dp)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        net.thechance.mena.designsystem.presentation.component.text.Text(
            text = title,
            color = titleColor,
            style = Theme.typography.title.medium
        )
        trailingContent?.invoke(
            coins,
        ) ?: DefaultHomeTrailingIcon(
            coins = coins,
            coinSoldTextColor = homeCoinTextColor
        )
    }
}


@Composable
private fun DefaultHomeTrailingIcon(
    coins: String,
    coinSoldTextColor: Color,
    modifier: Modifier = Modifier,
    coinRippleShape: Shape = RoundedCornerShape(24.dp),
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Text(
            text = coins,
            style = Theme.typography.label.small,
            color = coinSoldTextColor,
            modifier = Modifier
                .padding(end = 4.dp)
                .padding(vertical = 4.dp)
        )
        Image(
            painter = painterResource(Res.drawable.silver_tc),
            contentDescription = stringResource(Res.string.app_coin_icon),
            modifier = Modifier
                .size(24.dp)
                .clip(coinRippleShape)
                .clickable(onClick = onClick)
        )
    }

}

@Preview
@Composable
private fun HomeAppBarPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.Center
        ) {
            HomeAppBar("132")
        }
    }
}