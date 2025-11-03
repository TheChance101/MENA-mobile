package net.thechance.mena.identity.presentation.util

import android.content.Context
import android.os.LocaleList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.service.LocalizationService

actual class AppLocalizer(
    context: Context,
    localizationService: LocalizationService
) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        coroutineScope.launch {
            localizationService.observeLanguage().collectLatest {
                val locale = LocaleList.forLanguageTags(it).get(0)
                LocaleList.setDefault(LocaleList(locale))
                val config = context.resources.configuration
                config.setLocales(LocaleList(locale))
                context.createConfigurationContext(config)

            }
        }
    }
}
