package net.thechance.mena.identity.presentation.screen.editProfile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.gender
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.presentation.components.GenderToggle
import org.jetbrains.compose.resources.stringResource

@Composable
fun GenderToggleSection(gender: Gender?, onChangeGender: (Gender) -> Unit) {
    Column(modifier = Modifier.padding(top = Theme.spacing._16)) {
        Text(text = stringResource(Res.string.gender), style = Theme.typography.title.small)
        GenderToggle(gender, onChangeGender, modifier = Modifier.padding(top = Theme.spacing._4))
    }
}
