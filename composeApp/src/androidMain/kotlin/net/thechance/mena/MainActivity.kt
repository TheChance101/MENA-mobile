package net.thechance.mena

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import net.thechance.mena.faith.data.di.faithDataModule
import net.thechance.mena.faith.presentation.di.faithPresentationModule
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahScreen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}