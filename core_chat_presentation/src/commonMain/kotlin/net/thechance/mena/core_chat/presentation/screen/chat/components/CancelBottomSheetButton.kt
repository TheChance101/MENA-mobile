package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.cancel
import mena.core_chat_presentation.generated.resources.ic_cancel
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun CancelBottomSheetButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .clip(shape = RoundedCornerShape(Theme.spacing._12))
            .border(
                width = 1.dp,
                color = Theme.colorScheme.stroke,
                shape = RoundedCornerShape(Theme.spacing._12)
            )
            .padding(Theme.spacing._16),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(Res.drawable.ic_cancel),
            tint = Theme.colorScheme.primary.primary,
            contentDescription = stringResource(Res.string.cancel),
        )


    }
}
