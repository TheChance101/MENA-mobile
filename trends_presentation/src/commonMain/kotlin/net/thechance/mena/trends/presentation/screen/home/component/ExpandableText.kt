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
import androidx.compose.ui.unit.dp
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.show_less
import mena.trends_presentation.generated.resources.show_more
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.shared.component.modifier.noRippleClickable
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExpandableText(
    text: String,
    maxLines: Int = 2,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    onExpandedChange: (Boolean) -> Unit,
    moreText: String = stringResource(Res.string.show_more),
    lessText: String = stringResource(Res.string.show_less)
) {
    var isTextTruncated by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                fadeIn(animationSpec = tween(500, easing = LinearOutSlowInEasing)) togetherWith
                        fadeOut(animationSpec = tween(500, easing = LinearOutSlowInEasing))
            }
        ) { expanded ->
            Text(
                text = text,
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadePrimary,
                maxLines = if (expanded) Int.MAX_VALUE else maxLines,
                overflow = TextOverflow.Clip,
                onTextLayout = { result ->
                    if (!expanded) isTextTruncated = result.hasVisualOverflow
                },
                modifier = Modifier.noRippleClickable {
                    if (isTextTruncated) onExpandedChange(!expanded)
                }
            )
        }

        if (isTextTruncated) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                Text(
                    text = if (isExpanded) lessText else moreText,
                    style = Theme.typography.body.small,
                    color = Theme.colorScheme.shadeTertiary,
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .noRippleClickable { onExpandedChange(!isExpanded) }
                )
            }
        }
    }
}