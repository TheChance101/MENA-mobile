package net.thechance.mena.identity.presentation.screen.editProfile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.util.animation.shimmerLoading
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditProfileShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shimmerLoading(isLoading = true)
            .padding(horizontal = Theme.spacing._16)
            .padding(bottom = Theme.spacing._16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(vertical = 14.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceHigh)
                .shimmerLoading(isLoading = true)
        )
        
        ProfileImageShimmer(
            modifier = Modifier
                .padding(top = Theme.spacing._16, bottom = Theme.spacing._16)
        )
        
        repeat(3) {
            TextFieldShimmer(
                modifier = Modifier.padding(top = Theme.spacing._16)
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._16)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .background(Theme.colorScheme.background.surfaceHigh)
                    .shimmerLoading(isLoading = true)
            )
        }
        
        DatePickerShimmer(
            modifier = Modifier.padding(top = Theme.spacing._16)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._16)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.25f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .background(Theme.colorScheme.background.surfaceHigh)
                    .shimmerLoading(isLoading = true)
            )
        }
        
        GenderToggleShimmer(
            modifier = Modifier.padding(top = Theme.spacing._16)
        )
        
        Spacer(modifier = Modifier.height(Theme.spacing._24))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceHigh)
                .shimmerLoading(isLoading = true)
        )
        
        Spacer(modifier = Modifier.height(Theme.spacing._8))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceHigh)
                .shimmerLoading(isLoading = true)
        )
    }
}

@Composable
private fun ProfileImageShimmer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(88.dp)
            .clip(CircleShape)
            .background(Theme.colorScheme.background.surfaceHigh)
            .shimmerLoading(isLoading = true)
    )
}

@Composable
private fun TextFieldShimmer(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(14.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceHigh)
                .shimmerLoading(isLoading = true)
        )
        
        Spacer(modifier = Modifier.height(Theme.spacing._4))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(Theme.radius.md))
                .background(Theme.colorScheme.background.surfaceHigh)
                .shimmerLoading(isLoading = true)
        )
    }
}

@Composable
private fun DatePickerShimmer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceHigh)
            .shimmerLoading(isLoading = true)
    )
}

@Composable
private fun GenderToggleShimmer(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._16)
    ) {
        repeat(2) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
                    .clip(RoundedCornerShape(Theme.radius.xl))
                    .background(Theme.colorScheme.background.surfaceHigh)
                    .shimmerLoading(isLoading = true)
            )
        }
    }
}

@Preview
@Composable
private fun EditProfileShimmerPreview() {
    MenaTheme {
        EditProfileShimmer()
    }
}