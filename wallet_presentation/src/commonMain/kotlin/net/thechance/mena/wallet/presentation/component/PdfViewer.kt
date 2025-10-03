package net.thechance.mena.wallet.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.rendering_pdf_message
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.utils.PdfHandler
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun PdfViewer(
    pdf: ByteArray,
    pdfHandler: PdfHandler = koinInject()
) {
    var pages by remember { mutableStateOf(emptyList<ByteArray>()) }
    var finishedSplittingThePdfToPages by remember { mutableStateOf(false) }
    LaunchedEffect(pdf) {
        pages = pdfHandler.splitToPagesOfPngs(pdfData = pdf)
        finishedSplittingThePdfToPages = true
    }

    if (finishedSplittingThePdfToPages.not()) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = stringResource(Res.string.rendering_pdf_message),
            style = Theme.typography.body.medium,
            textAlign = TextAlign.Center,
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
        ) {
            itemsIndexed(pages) { index, page ->
                AsyncImage(
                    model = page,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.7071f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White),
                )
            }
        }
    }
}
