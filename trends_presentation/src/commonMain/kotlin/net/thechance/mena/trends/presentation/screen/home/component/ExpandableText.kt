package net.thechance.mena.trends.presentation.screen.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.show_less
import mena.trends_presentation.generated.resources.show_more
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.shared.component.modifier.noRippleClickable
import org.jetbrains.compose.resources.stringResource

const val animationDuration = 500

@Composable
internal fun ExpandableText(
    text: String,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    maxLines: Int = 3,
    onExpandedChange: () -> Unit,
    moreText: String = stringResource(Res.string.show_more),
    lessText: String = stringResource(Res.string.show_less),
) {
    var isTextTruncated by remember { mutableStateOf(false) }
    val shortTextAnimation =
        fadeIn(animationSpec = tween(animationDuration, easing = LinearOutSlowInEasing)) togetherWith
                fadeOut(animationSpec = tween(animationDuration, easing = LinearOutSlowInEasing))

    Column(
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        modifier = modifier.noRippleClickable { onExpandedChange() }
    ) {

        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = { shortTextAnimation }
        ) { expanded ->
            Text(
                text = text,
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadePrimary,
                maxLines = if (expanded) Int.MAX_VALUE else maxLines,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { if (expanded.not()) isTextTruncated = it.hasVisualOverflow },
            )
        }

        AnimatedVisibility(
            visible = isTextTruncated,
            enter = fadeIn(animationSpec = tween(animationDuration)),
            exit = fadeOut(animationSpec = tween(animationDuration))
        ) {
            Text(
                text = if (isExpanded) lessText else moreText,
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeTertiary,
                modifier = Modifier.padding(vertical = Theme.spacing._2)
            )
        }
    }
}