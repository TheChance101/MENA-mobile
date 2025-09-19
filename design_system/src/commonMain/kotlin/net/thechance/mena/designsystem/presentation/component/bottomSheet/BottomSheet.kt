package net.thechance.mena.designsystem.presentation.component.bottomSheet

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScaffoldScope.BottomSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    dismissOnBackPress: Boolean = true,
    stickyContent: @Composable BoxScope.() -> Unit = {},
    sheetContent: LazyListScope.() -> Unit
) {
    if (isVisible.not()) return

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val sheetFullHeightPx = with(density) { maxHeight.toPx() }
        val collapsedOffset = sheetFullHeightPx.coerceAtLeast(0f)
        val offset = remember { Animatable(collapsedOffset) }
        var referenceHeight by remember { mutableStateOf(0) }

        BackHandler {
            animateClose(scope, offset, collapsedOffset, onDismissRequest)
        }

        LaunchedEffect(isVisible) {
            if (isVisible) {
                offset.animateTo(
                    targetValue = 0f, animationSpec = spring(stiffness = Spring.StiffnessMedium)
                )
            } else {
                offset.animateTo(
                    targetValue = collapsedOffset,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                )
            }
        }

        val innerLazyListState: LazyListState = rememberLazyListState()
        val nestedConnection = rememberNestedScroll(
            innerLazyListState = innerLazyListState,
            collapsedOffset = collapsedOffset,
            offset = offset,
            onDismissRequest = onDismissRequest,
        )

        val scrimAlpha by derivedStateOf {
            val progress = 1f - (offset.value / collapsedOffset.coerceAtLeast(1f))
            (progress * 0.5f).coerceIn(0f, 0.5f)
        }

        Box {
            if (scrimAlpha > 0f) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(color = Theme.colorScheme.primary.primary.copy(0.55f))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    if (dismissOnBackPress) {
                                        animateClose(
                                            scope,
                                            offset,
                                            collapsedOffset,
                                            onDismissRequest
                                        )
                                    }
                                })
                        })
            }

            Box(
                modifier = Modifier
                    .padding(top = 100.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter)
                    .nestedScroll(nestedConnection)
                    .offset { IntOffset(0, offset.value.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {},
                            onDragEnd = {
                                animateCollapse(
                                    scope = scope,
                                    offset = offset,
                                    collapsedOffset = collapsedOffset,
                                    onDismissRequest = onDismissRequest,
                                )
                            },
                            onDragCancel = {},
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    val new =
                                        (offset.value + dragAmount.y).coerceIn(0f, collapsedOffset)
                                    offset.snapTo(new)
                                }
                            })
                    }
            ) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = Theme.radius.xl,
                        topEnd = Theme.radius.xl
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    color = Theme.colorScheme.background.surface,
                ) {
                    Column {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp)
                                .size(width = 39.dp, height = 2.dp)
                                .align(Alignment.CenterHorizontally).background(
                                    color = Theme.colorScheme.shadeTertiary,
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )

                        LazyColumn(
                            modifier = Modifier.weight(1f, fill = false),
                            state = innerLazyListState,
                            content = sheetContent,
                            contentPadding = contentPadding
                        )

                        Box(
                            modifier = Modifier.background(Color(0xFFFFFF00))
                                .height(with(LocalDensity.current) { referenceHeight.toDp() })
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .onGloballyPositioned { coordinates ->
                            referenceHeight = coordinates.size.height
                        }
                ) {
                    stickyContent()
                }
            }
        }
    }
}

private fun animateClose(
    scope: CoroutineScope,
    offset: Animatable<Float, AnimationVector1D>,
    collapsedOffset: Float,
    onDismissRequest: () -> Unit
) {
    scope.launch {
        offset.animateTo(
            targetValue = collapsedOffset,
            animationSpec = spring(stiffness = Spring.StiffnessMedium)
        )
    }.invokeOnCompletion {
        onDismissRequest()
    }
}

@Composable
private fun rememberNestedScroll(
    innerLazyListState: LazyListState,
    collapsedOffset: Float,
    offset: Animatable<Float, AnimationVector1D>,
    onDismissRequest: () -> Unit,
): NestedScrollConnection {
    val scope = rememberCoroutineScope()
    var isScrollBottomSheet by remember { mutableStateOf(false) }
    return remember(innerLazyListState, collapsedOffset) {
        object : NestedScrollConnection {
            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                if (isScrollBottomSheet) {
                    animateCollapse(
                        scope = scope,
                        offset = offset,
                        collapsedOffset = collapsedOffset,
                        onDismissRequest = onDismissRequest,
                    )
                }
                isScrollBottomSheet = false
                return super.onPostFling(consumed, available)
            }

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0f || isScrollBottomSheet) {
                    val atTop = innerLazyListState.firstVisibleItemIndex == 0 &&
                            innerLazyListState.firstVisibleItemScrollOffset == 0

                    if (atTop) {
                        isScrollBottomSheet = true
                        val delta = available.y
                        scope.launch {
                            val new = (offset.value + delta).coerceIn(0f, collapsedOffset)
                            offset.snapTo(new)
                        }
                        return Offset(0f, available.y)
                    }
                }
                return Offset.Zero
            }
        }
    }
}

private fun animateCollapse(
    scope: CoroutineScope,
    offset: Animatable<Float, AnimationVector1D>,
    collapsedOffset: Float,
    onDismissRequest: () -> Unit
) {
    scope.launch {
        val shouldCollapse =
            offset.value > collapsedOffset - (collapsedOffset / 3f)
        if (shouldCollapse) {
            offset.animateTo(
                targetValue = collapsedOffset,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
            onDismissRequest()
        } else {
            offset.animateTo(
                targetValue = 0f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        }
    }
}

@Preview
@Composable
private fun SimpleBottomSheetPreview() {
    var visible by remember { mutableStateOf(false) }

    MenaTheme() {
        Scaffold(overlays = {
            bottomSheet(isVisible = visible) {
                BottomSheet(
                    isVisible = visible,
                    onDismissRequest = { visible = false },
                    stickyContent = {
                        PrimaryButton(
                            text = "Test",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            onClick = {},
                        )
                    },
                    sheetContent = {
                        items(3) { index ->
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Theme.colorScheme.background.surfaceLow)
                                    .padding(10.dp),
                                text = "Test($index)",
                                style = Theme.typography.body.medium
                            )
                        }
                    })
            }
        }) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                PrimaryButton(
                    text = "Show BottomSheet",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    onClick = { visible = true },
                )
            }
        }
    }
}

