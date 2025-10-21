package net.thechance.mena.wallet.presentation.screen.statement_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.wallet.presentation.component.ThreeDotsLoadingIndicator
import net.thechance.mena.wallet.presentation.utils.splitPdfToPngs

@Composable
fun PdfViewer(
    pdf: ByteArray,
) {
    var pages by remember { mutableStateOf(emptyList<ByteArray>()) }
    var finishedSplittingThePdfToPages by remember { mutableStateOf(false) }
    LaunchedEffect(pdf) {
        pages = splitPdfToPngs(pdfData = pdf)
        finishedSplittingThePdfToPages = true
    }

    if (finishedSplittingThePdfToPages.not()) {
        Box(modifier = Modifier.fillMaxSize()) {
            ThreeDotsLoadingIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Theme.spacing._8),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = Theme.spacing._16, bottom = 88.dp)
        ) {
            itemsIndexed(pages) { index, page ->
                AsyncImage(
                    model = page,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.7071f)
                        .clip(RoundedCornerShape(Theme.spacing._8))
                        .background(Color.White),
                )
            }
        }
    }
}
