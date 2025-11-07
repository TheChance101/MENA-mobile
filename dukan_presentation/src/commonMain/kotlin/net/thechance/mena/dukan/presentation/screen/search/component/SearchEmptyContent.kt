package net.thechance.mena.dukan.presentation.screen.search.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.img_not_found_search
import mena.dukan_presentation.generated.resources.img_start_search
import mena.dukan_presentation.generated.resources.no_result_found
import mena.dukan_presentation.generated.resources.no_result_found_body
import mena.dukan_presentation.generated.resources.search_icon
import mena.dukan_presentation.generated.resources.start_search
import mena.dukan_presentation.generated.resources.start_search_body
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SearchEmptyContent(
    icon: Painter,
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(horizontal = Theme.spacing._16)
        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = icon,
            contentDescription = stringResource(resource = Res.string.search_icon),
            modifier = Modifier.size(128.dp),
        )
        Text(
            text = title,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = body,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Theme.spacing._4)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StartSearchContentPreview(){
    MenaTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchEmptyContent(
                icon = painterResource(resource = Res.drawable.img_start_search),
                title = stringResource(resource = Res.string.start_search),
                body = stringResource(resource = Res.string.start_search_body)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchNotFoundContentPreview(){
    MenaTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchEmptyContent(
                icon = painterResource(resource = Res.drawable.img_not_found_search),
                title = stringResource(resource = Res.string.no_result_found),
                body = stringResource(resource = Res.string.no_result_found_body)
            )
        }
    }
}
