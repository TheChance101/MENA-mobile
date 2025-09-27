package net.thechance.mena.dukan.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.foreground_image
import net.thechance.mena.designsystem.presentation.component.image.Image
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ImageWithTextContainer(
    blurImageRes: DrawableResource,
    foregroundImageRes: DrawableResource,
    header: @Composable () -> Unit,
    bodyText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(Theme.spacing._24),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .blur(radius = 35.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .background(
                        color = Theme.colorScheme.primary.primary,
                        shape = RoundedCornerShape(Theme.radius.full)
                    )
                    .align(Alignment.BottomCenter)
            )
            Image(
                painter = painterResource(foregroundImageRes),
                contentDescription = stringResource(Res.string.foreground_image)
            )
        }

        Box(modifier = Modifier.padding(top = Theme.spacing._12)) {
            header()
        }

        Text(
            text = bodyText,
            style = Theme.typography.body.small,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(Theme.spacing._2)
        )
    }
}