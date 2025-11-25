package net.thechance.mena

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import net.thechance.mena.appEntryPoint.DeepLink
import net.thechance.mena.appEntryPoint.MainEntryViewModel
import net.thechance.mena.faith.presentation.utils.permission.AndroidFaithPermissionsManagerImpl
import net.thechance.mena.identity.presentation.util.AppLocalizer
import net.thechance.mena.identity.presentation.util.PermissionManager
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val mainEntryViewModel: MainEntryViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        PermissionManager.init(this)
        AndroidFaithPermissionsManagerImpl.init(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FileKit.init(this)

        val localizer: AppLocalizer by inject()
        localizer.applyLocaleToContext()

        mainEntryViewModel.onDeepLinkChange(
            deepLink = parseDeepLinkFromIntent(intent)
        )

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        mainEntryViewModel.onDeepLinkChange(
            deepLink = parseDeepLinkFromIntent(intent)
        )
    }

    private fun parseDeepLinkFromIntent(intent: Intent): DeepLink {
        val appLinkData: Uri? = intent.data
        return DeepLink(
            userId = appLinkData?.getQueryParameter("userId"),
        )
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}