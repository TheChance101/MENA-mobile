package net.thechance.mena.identity.presentation.util

import android.content.Context
import android.os.LocaleList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.util.AppLanguage

actual class AppLocalizer(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var currentLanguage: String = AppLanguage.DEFAULT.iso

    init {
        coroutineScope.launch {
            settingsRepository.observeAppLanguage().collectLatest { lang ->
                currentLanguage = lang.iso.ifEmpty {
                    val deviceLocale = androidx.core.os.LocaleListCompat.getDefault()[0]
                    val deviceIso = deviceLocale?.language ?: AppLanguage.ENGLISH.iso
                    settingsRepository.applyLanguage(AppLanguage.fromIso(deviceIso))
                    applyLocaleToContext(deviceIso)
                    deviceIso
                }
                applyLocaleToContext(currentLanguage)
            }
        }
    }

    fun applyLocaleToContext(iso: String = currentLanguage) {
        val localeList = LocaleList.forLanguageTags(iso)
        LocaleList.setDefault(localeList)
        val config = context.resources.configuration
        config.setLocales(localeList)
    }
}

