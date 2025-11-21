package net.thechance.mena.designsystem.presentation.component.bottomSheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.scaffold.ScaffoldScope
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

enum class BottomSheetValue {
    HIDDEN, COLLAPSED, EXPANDED
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScaffoldScope.BottomSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    containerColor: Color = Theme.colorScheme.background.surface,
    scrimColor: Color = Color.Black.copy(0.55f),
    cornerShape: Shape = RoundedCornerShape(
        topStart = Theme.radius.xl,
        topEnd = Theme.radius.xl
    ),
    paddingFromTop: Dp = 64.dp,
    skipPartiallyExpanded: Boolean = false,
    stickyFooterContent: @Composable BoxScope.() -> Unit = {},
    sheetContent: @Composable ColumnScope.() -> Unit
) {

    if (!isVisible)
        return

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    var showScrim by remember { mutableStateOf(true) }
    var sheetSize by remember { mutableStateOf(IntSize.Zero) }
    var referenceHeight by remember { mutableStateOf(0) }
    val sheetCollapsedHeightPx = with(density) { 340.dp.toPx() }
    val dragState = rememberDragState()

    LaunchedEffect(sheetSize) {
        if (sheetSize != IntSize.Zero) {
            val containerHeight = sheetSize.height.toFloat()
            val collapsedOffset =
                if (skipPartiallyExpanded) 0f else containerHeight - sheetCollapsedHeightPx

            dragState.updateAnchors(
                DraggableAnchors {
                    BottomSheetValue.EXPANDED at 0f
                    if (containerHeight >= sheetCollapsedHeightPx)
                        BottomSheetValue.COLLAPSED at collapsedOffset
                    BottomSheetValue.HIDDEN at containerHeight
                }
            )

            dragState.animateTo(
                if (containerHeight >= sheetCollapsedHeightPx)
                    BottomSheetValue.COLLAPSED
                else
                    BottomSheetValue.EXPANDED,
                tween(500)
            )
        }
    }

    LaunchedEffect(dragState.targetValue) {
        if (dragState.targetValue == BottomSheetValue.HIDDEN) {
            try {
                showScrim = false
                dragState.animateTo(
                    BottomSheetValue.HIDDEN,
                    tween(250)
                )
            } finally {
                onDismissRequest()
            }
        }
    }

    val nestedConnection = remember(dragState) {
        sheetNestedScrollConnection(dragState, Orientation.Vertical)
    }

    Box(modifier = modifier.fillMaxSize()) {
        BackHandler(dismissOnBackPress) { onDismissRequest() }

        AnimatedVisibility(
            visible = showScrim,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = scrimColor)
                    .clickable(
                        enabled = dismissOnClickOutside,
                        onClick = {
                            showScrim = false
                            scope.launch {
                                val durationMillis = 250
                                dragState.animateTo(
                                    BottomSheetValue.HIDDEN,
                                    tween(durationMillis),
                                )
                                onDismissRequest()
                            }
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )
        }

        Box(
            modifier = Modifier
                .padding(top = paddingFromTop)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset { IntOffset(0, dragState.requireOffset().roundToInt()) }
                .nestedScroll(nestedConnection)
                .anchoredDraggable(
                    state = dragState,
                    orientation = Orientation.Vertical
                )
                .onSizeChanged { sheetSize = it }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(containerColor, cornerShape)
                    .clip(cornerShape)
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 6.dp)
                        .size(width = 39.dp, height = 2.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(
                            color = Theme.colorScheme.shadeTertiary,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                sheetContent()
                AnimatedVisibility(
                    visible = dragState.currentValue != BottomSheetValue.HIDDEN && sheetSize != IntSize.Zero,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    Box(
                        modifier = Modifier.background(containerColor)
                            .offset(
                                y = WindowInsets.navigationBars
                                    .asPaddingValues()
                                    .calculateBottomPadding()
                            )
                            .height(with(LocalDensity.current) { referenceHeight.toDp() })
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .offset(
                    y = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
                .background(containerColor)
                .clickable(false) {}
                .align(Alignment.BottomCenter)
                .onGloballyPositioned { coordinates ->
                    referenceHeight = coordinates.size.height
                }
        ) {
            AnimatedVisibility(
                visible = dragState.currentValue != BottomSheetValue.HIDDEN && sheetSize != IntSize.Zero,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                stickyFooterContent()
            }
        }
    }
}

@Composable
private fun rememberDragState() = remember {
    AnchoredDraggableState(
        initialValue = BottomSheetValue.HIDDEN,
        anchors = DraggableAnchors {
            BottomSheetValue.HIDDEN at Float.MAX_VALUE
            BottomSheetValue.COLLAPSED at Float.MAX_VALUE
            BottomSheetValue.EXPANDED at Float.MAX_VALUE
        }
    )
}

@Preview
@Composable
private fun SimpleBottomSheetPreview() {
    var visible by remember { mutableStateOf(false) }

    MenaTheme {
        Scaffold(
            overlays = {
                bottomSheet(isVisible = visible) { isVisible ->
                    BottomSheet(
                        isVisible = isVisible,
                        onDismissRequest = { visible = false },
                        stickyFooterContent = {
                            PrimaryButton(
                                text = "Test",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                onClick = {},
                            )
                        },
                        sheetContent = {
                            Row(
                                modifier = Modifier
                                    .background(Theme.colorScheme.stroke)
                                    .height(150.dp)
                            ) {
                                TestList()
                                TestList()
                                TestList()
                                TestList()
                                TestList()
                            }
                        })
                }
            }
        ) {
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

@Composable
private fun RowScope.TestList() {
    LazyColumn(modifier = Modifier.weight(1f)) {
        items(30) { index ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Theme.colorScheme.background.surfaceLow)
                    .padding(10.dp),
                text = "Test number: ($index)",
                style = Theme.typography.body.medium
            )
        }
    }
}

