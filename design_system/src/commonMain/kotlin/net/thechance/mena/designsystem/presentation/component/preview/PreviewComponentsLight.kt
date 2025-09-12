package net.thechance.mena.designsystem.presentation.component.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mena.design_system.generated.resources.Res
import mena.design_system.generated.resources.checkmark
import mena.design_system.generated.resources.ic_arrow_left
import mena.design_system.generated.resources.ic_check_circle
import mena.design_system.generated.resources.ic_cheese_cake
import mena.design_system.generated.resources.ic_chip
import mena.design_system.generated.resources.ic_close_circle
import mena.design_system.generated.resources.ic_iraq
import mena.design_system.generated.resources.ic_profile
import mena.design_system.generated.resources.ic_user
import mena.design_system.generated.resources.silver_tc
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.bottomNavigation.MeanBottomNavigationBar
import net.thechance.mena.designsystem.presentation.component.button.FabButton
import net.thechance.mena.designsystem.presentation.component.button.NegativeButton
import net.thechance.mena.designsystem.presentation.component.button.NegativeTextButton
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.checkBox.Checkbox
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.component.section.Section
import net.thechance.mena.designsystem.presentation.component.segment.Segment
import net.thechance.mena.designsystem.presentation.component.segment.SegmentButton
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.component.switches.Switch
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.component.textField.MobileNumberLeadingContent
import net.thechance.mena.designsystem.presentation.component.textField.MobileNumberTextField
import net.thechance.mena.designsystem.presentation.component.textField.MultiLineTextField
import net.thechance.mena.designsystem.presentation.component.textField.OtpInputField
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun PreviewComponentsLight() {
    var primaryButtonLoading by remember { mutableStateOf(false) }
    val (isSwitchChecked, onCheckedChange) = remember { mutableStateOf(false) }

    LaunchedEffect(primaryButtonLoading) {
        if (primaryButtonLoading) {
            launch {
                delay(1000)
                primaryButtonLoading = false
            }
        }
    }

    MenaTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .background(Color(0xFFF2F4F7))
                .padding(12.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PreviewComponent(
                isScrollable = true,
                title = "Primary button"
            ) {
                PrimaryButton(
                    text = "Button",
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    modifier = Modifier
                )
                PrimaryButton(
                    text = "Click me to test loading",
                    isLoading = primaryButtonLoading,
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = { primaryButtonLoading = !primaryButtonLoading },
                )
                PrimaryButton(
                    text = "Button",
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    isEnabled = false,
                    modifier = Modifier
                )
            }

            PreviewComponent(
                isScrollable = true,
                title = "Negative button"
            ) {
                NegativeButton(
                    text = "Button",
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    modifier = Modifier
                )
                NegativeButton(
                    text = "Button",
                    isLoading = true,
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    modifier = Modifier
                )
                NegativeButton(
                    text = "Button",
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    isEnabled = false,
                    modifier = Modifier
                )
            }

            PreviewComponent(
                isScrollable = true,
                title = "Outlined button"
            ) {
                OutlinedButton(
                    text = "Button",
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    modifier = Modifier
                )
                OutlinedButton(
                    text = "Button",
                    isLoading = true,
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    modifier = Modifier
                )
                OutlinedButton(
                    text = "Button",
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    isEnabled = false,
                    modifier = Modifier
                )
            }

            PreviewComponent(
                isScrollable = true,
                title = "Text button"
            ) {
                TextButton(
                    text = "Button",
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    modifier = Modifier
                )
                TextButton(
                    text = "Button",
                    isLoading = true,
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    modifier = Modifier
                )
                TextButton(
                    text = "Button",
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    isEnabled = false,
                    modifier = Modifier
                )
            }

            PreviewComponent(
                isScrollable = true,
                title = "Negative Text button"
            ) {
                NegativeTextButton(
                    text = "Button",
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    modifier = Modifier
                )
                NegativeTextButton(
                    text = "Button",
                    trailingIcon = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    isEnabled = false,
                    modifier = Modifier
                )
            }

            PreviewComponent(
                title = "Fab button"
            ) {
                FabButton(
                    painter = painterResource(resource = Res.drawable.ic_cheese_cake),
                    onClick = {},
                    modifier = Modifier
                )
            }

            PreviewTextFieldComponent()


            PreviewComponent(
                title = "check box"
            ) {
                Checkbox(
                    checkedState = ToggleableState.Off,
                    onCheckedChange = {}
                )

                Checkbox(
                    checkedState = ToggleableState.Off,
                    isEnabled = false,
                    onCheckedChange = {}
                )

                Checkbox(
                    checkedState = ToggleableState.On,
                    onCheckedChange = {}
                )

                Checkbox(
                    checkedState = ToggleableState.On,
                    isEnabled = false,
                    onCheckedChange = {}
                )

                Checkbox(
                    checkedState = ToggleableState.Indeterminate,
                    onCheckedChange = {}
                )

                Checkbox(
                    checkedState = ToggleableState.Indeterminate,
                    isEnabled = false,
                    onCheckedChange = {}
                )
            }

            PreviewComponent(
                title = "Switch"
            ) {
                Switch(
                    isChecked = isSwitchChecked,
                    onCheckedChange = onCheckedChange
                )

                Switch(
                    isChecked = false,
                    isEnabled = false,
                    onCheckedChange = onCheckedChange
                )

                Switch(
                    isChecked = true,
                    onCheckedChange = onCheckedChange
                )

                Switch(
                    isChecked = true,
                    isEnabled = false,
                    onCheckedChange = onCheckedChange
                )
            }

            PreviewComponent(
                title = "Segment buttons"
            ) {
                Segment(contentPadding = PaddingValues(top = 8.dp)) {
                    item("Option1") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(Theme.radius.md))
                                .background(Theme.colorScheme.background.surfaceHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Option1")
                        }
                    }
                    item("Option2") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(Theme.radius.md))
                                .background(Theme.colorScheme.background.surfaceHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Option2")
                        }
                    }
                }
            }

            PreviewComponent(
                title = "OTP"
            ) {
                OtpInputField(
                    number = null,
                    onNumberChanged = { },
                )
                OtpInputField(
                    number = 1,
                    onNumberChanged = { },
                )
                OtpInputField(
                    number = 2,
                    onNumberChanged = { },
                )
            }

            PreviewComponent(
                title = "Chips"
            ) {
                Chip(
                    text = "Chips",
                    painter = painterResource(Res.drawable.ic_chip),
                    isSelected = false,
                    onClick = {}
                )

                Chip(
                    text = "Chips",
                    painter = painterResource(Res.drawable.ic_chip),
                    isSelected = true,
                    onClick = {}
                )

                Chip(
                    text = "Chips",
                    painter = painterResource(Res.drawable.ic_chip),
                    isSelected = true,
                    isEnabled = false,
                    onClick = {}
                )
            }

            PreviewComponent(
                title = "App bar",
                isScrollable = false
            ) {
                AppBar(
                    leadingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_left),
                            contentDescription = null
                        )
                    },
                    title = "Screen title",
                    trailingContent = {
                        AppBarOptionContainer(
                            isBadgeVisible = true,
                            badgeColor = Theme.colorScheme.error
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_user),
                                contentDescription = null
                            )
                        }
                        AppBarOptionContainer(
                            isBadgeVisible = true,
                            badgeColor = Theme.colorScheme.primary.primary
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.checkmark),
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            PreviewComponent(
                title = "navigation bar",
                isScrollable = false
            ) {
                MeanBottomNavigationBar(
                    onItemClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }

            PreviewSnackBarComponent()

            PreviewComponent(
                title = "Section"
            ) {
                Section(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Section name",
                    actionName = "see all",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun PreviewComponent(
    title: String,
    isScrollable: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        MenaText(
            text = title,
            color = Theme.colorScheme.shadeTertiary,
            style = Theme.typography.headline.small
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
                .then(
                    if (isScrollable) Modifier.horizontalScroll(rememberScrollState())
                    else Modifier
                )
        ) {
            content()
        }
    }
}


@Composable
private fun PreviewTextFieldComponent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        MenaText(
            text = "Text field",
            color = Theme.colorScheme.shadeTertiary,
            style = Theme.typography.headline.small
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = "adad",
                onValueChanged = {},
                placeholder = "Placeholder",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = painterResource(Res.drawable.ic_profile)
            )

            TextField(
                value = "",
                onValueChanged = {},
                placeholder = "Placeholder",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = painterResource(Res.drawable.ic_profile),
                trailingIcon = painterResource(Res.drawable.silver_tc)
            )

            TextField(
                value = "",
                onValueChanged = {},
                placeholder = "Placeholder",
                leadingIcon = painterResource(Res.drawable.ic_profile),
                isError = true,
                errorMessage = "error message",
                modifier = Modifier.fillMaxWidth(),
            )

            MultiLineTextField(
                value = "",
                onValueChanged = {},
                placeholder = "Placeholder",
                modifier = Modifier.fillMaxWidth()
            )

            MobileNumberTextField(
                value = "",
                onValueChanged = { },
                title = "title",
                placeholder = "value",
                leadingIcon = painterResource(Res.drawable.ic_profile),
                leadingContent = {
                    MobileNumberLeadingContent(
                        countryCode = "+964",
                        countryPainter = painterResource(Res.drawable.ic_iraq),
                        onClick = {}
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PreviewSnackBarComponent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        MenaText(
            text = "SnackBar",
            color = Theme.colorScheme.shadeTertiary,
            style = Theme.typography.headline.small
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SnackBar(
                title = "Success",
                message = "message description",
                leadingIcon = painterResource(Res.drawable.ic_check_circle),
                modifier = Modifier.fillMaxWidth()
            )

            SnackBar(
                title = "Error",
                message = "message description",
                leadingIcon = painterResource(Res.drawable.ic_close_circle),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}