package net.thechance.mena.designsystem.presentation.component.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.ic_arrow_right
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Section(
    title: String,
    actionName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = Theme.typography.title.small,
            modifier = Modifier.weight(1f)
        )

        TextButton(
            text = actionName,
            trailingIcon = painterResource(Res.drawable.ic_arrow_right),
            onClick = onClick
        )
    }
}

@Preview(showBackground = true,backgroundColor = 0xFFF2F4F7)

@Composable
private fun PreviewSection() {
    MenaTheme {
        Section(
            modifier = Modifier.fillMaxWidth(),
            title = "Section name",
            actionName = "see all",
            onClick = {}
        )
    }
}