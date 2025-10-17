package net.thechance.mena.identity.presentation.screen.editProfile.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.female
import mena.identity_presentation.generated.resources.gender
import mena.identity_presentation.generated.resources.male
import mena.identity_presentation.generated.resources.options
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.entity.Gender
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun GenderToggle(gender: Gender, onGenderChange: (Gender) -> Unit) {
    Column(modifier = Modifier.padding(top = Theme.spacing._16)) {
        Text(text = stringResource(Res.string.gender), style = Theme.typography.title.small)

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing._16),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._16)
        ) {
            ToggleOption(
                printer = painterResource(Res.drawable.male),
                isSelected = gender == Gender.MALE,
                onChange = { onGenderChange(Gender.MALE) }
            )

            ToggleOption(
                printer = painterResource(Res.drawable.female),
                isSelected = gender == Gender.FEMALE,
                onChange = { onGenderChange(Gender.FEMALE) }
            )
        }
    }
}


@Composable
private fun RowScope.ToggleOption(
    printer: Painter,
    isSelected: Boolean,
    onChange: () -> Unit
) {
    val animateBackground by animateColorAsState(
        if (isSelected)
            Theme.colorScheme.brand.brand
        else
            Theme.colorScheme.background.surfaceHigh
    )

    val animateIconColor by animateColorAsState(
        if (isSelected) Theme.colorScheme.primary.onPrimary
        else
            Theme.colorScheme.primary.primary
    )
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(animateBackground)
            .clickable(onClick = { onChange() })
            .padding(10.dp)
            .size(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(64.dp),
            painter = printer,
            contentDescription = stringResource(Res.string.options),
            tint = animateIconColor
        )
    }
}
