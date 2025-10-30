package net.thechance.mena.identity.presentation.util

import android.content.Context
import android.os.LocaleList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.service.LocalizationService
import java.util.Locale
actual class AppLocalizer(
    context: Context,
    localizationService: LocalizationService
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch {
            localizationService.observeLanguage().collectLatest {
                val locale = Locale(it)
                Locale.setDefault(locale)
                val config = context.resources.configuration
                config.setLocales(LocaleList(locale))
            }
        }
    }
}
