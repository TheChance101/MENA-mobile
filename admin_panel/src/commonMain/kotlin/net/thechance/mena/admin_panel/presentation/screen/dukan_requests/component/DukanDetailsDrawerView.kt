package net.thechance.mena.admin_panel.presentation.screen.dukan_requests.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import net.thechance.mena.admin_panel.presentation.screen.dukan_requests.DukanRequestsScreenState
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.approve
import net.thechance.mena.admin_panel.resources.cancel
import net.thechance.mena.admin_panel.resources.dukan_details
import net.thechance.mena.admin_panel.resources.ic_cancel
import net.thechance.mena.admin_panel.resources.reject
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DukanDetailsDrawerView(
    isOpen: Boolean,
    selectedDukanItem: DukanRequestsScreenState.DukanItem,
    onDismiss: () -> Unit,
    onRejectDukanClicked: () -> Unit,
    onApproveDukanClicked: () -> Unit,
    scrimColor: Color = Theme.colorScheme.primary.primary.copy(0.55f)
) {
    if (isOpen) {
        Popup(
            alignment = Alignment.TopStart,
            onDismissRequest = onDismiss
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(scrimColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDismiss
                        )
                )

                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutLinearInEasing
                        )
                    ),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Surface(
                        modifier = Modifier
                            .width(586.dp)
                            .fillMaxHeight(),
                        color = Theme.colorScheme.background.surfaceLow
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            CloseButton(onDismiss = onDismiss)

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 8.dp)
                                    .background(color = Theme.colorScheme.background.surface)
                            ) {
                                DrawerTitle()

                                PendingDukanDetailsContent(
                                    selectedDukanItem = selectedDukanItem,
                                    modifier = Modifier.weight(1f).fillMaxWidth()
                                )

                                DrawerActions(
                                    onApproveDukanClicked = onApproveDukanClicked,
                                    onRejectDukanClicked = onRejectDukanClicked
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerTitle() {
    Text(
        text = stringResource(Res.string.dukan_details),
        style = Theme.typography.title.medium,
        color = Theme.colorScheme.shadePrimary,
        modifier = Modifier.padding(top = 16.dp, start = 12.dp, bottom = 12.dp)
    )
}

@Composable
private fun CloseButton(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(top = 12.dp, start = 8.dp)
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.colorScheme.background.surfaceHigh)
            .clickable(onClick = onDismiss)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_cancel),
            contentDescription = stringResource(Res.string.cancel),
            tint = Theme.colorScheme.primary.primary
        )
    }
}

@Composable
private fun DrawerActions(
    onApproveDukanClicked: () -> Unit,
    onRejectDukanClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Theme.colorScheme.background.surfaceLow)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            modifier = Modifier.sizeIn(minWidth = 76.dp, minHeight = 48.dp),
            text = stringResource(resource = Res.string.reject),
            onClick = onRejectDukanClicked
        )

        PrimaryButton(
            modifier = Modifier.sizeIn(minWidth = 92.dp, minHeight = 48.dp),
            text = stringResource(resource = Res.string.approve),
            onClick = onApproveDukanClicked
        )
    }
}