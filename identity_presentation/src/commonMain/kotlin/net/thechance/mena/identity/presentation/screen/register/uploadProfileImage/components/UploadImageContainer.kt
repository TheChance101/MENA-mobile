package net.thechance.mena.identity.presentation.screen.register.uploadProfileImage.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.click_to_upload
import mena.identity_presentation.generated.resources.ic_add_image
import mena.identity_presentation.generated.resources.pencil_edit
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.presentation.util.dashedBorder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UploadImageContainer(
    onClick: () -> Unit,
    image: ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    val borderColor = Theme.colorScheme.brand.brand
    val radius = Theme.radius.xl

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(radius))
                .background(Theme.colorScheme.background.surfaceLow)
                .dashedBorder(color = borderColor, shape = RoundedCornerShape(radius))
                .clickable { if (image == null) onClick() },
            contentAlignment = Alignment.Center
        ) {

            if (image == null) {
                UploadPlaceholder()
            } else {
                UploadedImage(image = image)
            }
        }
        image?.let {
            EditIcon(modifier = Modifier.align(Alignment.BottomCenter).clickable { onClick() })
        }
    }
}

@Composable
private fun EditIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .offset(y = 20.dp)
            .clip(shape = RoundedCornerShape(Theme.radius.full))
            .background(Theme.colorScheme.primary.primary)
            .border(
                width = 1.dp,
                color = Theme.colorScheme.background.surface,
                shape = RoundedCornerShape(Theme.radius.xl)
            )
    )
    {
        Icon(
            painter = painterResource(Res.drawable.pencil_edit),
            contentDescription = "Bottom Action",
            tint = Theme.colorScheme.primary.onPrimary,
            modifier = Modifier.size(20.dp).align(Alignment.Center)
        )
    }
}

@Composable
private fun UploadPlaceholder() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(Res.drawable.ic_add_image),
            contentDescription = null
        )
        Text(
            text = stringResource(Res.string.click_to_upload),
            color = Theme.colorScheme.primary.primary,
            style = Theme.typography.label.medium
        )
    }
}

@Composable
private fun BoxScope.UploadedImage(
    onClick: () -> Unit = {},
    image: ImageBitmap,
) {
    Image(
        bitmap = image,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}


@Preview
@Composable
private fun Preview() {
    MenaTheme {
        UploadImageContainer(
            onClick = {},
            image = null,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun Preview2() {
    MenaTheme {
        UploadImageContainer(
            onClick = {},
            image = ImageBitmap(1, 1),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}