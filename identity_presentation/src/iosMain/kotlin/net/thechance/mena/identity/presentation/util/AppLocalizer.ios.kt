package net.thechance.mena.identity.presentation.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.thechance.mena.identity.domain.service.LocalizationService
import platform.Foundation.NSUserDefaults

actual class AppLocalizer(
    localizationService: LocalizationService
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    init {
        scope.launch {
            localizationService.observeLanguage().collectLatest { iso ->
                val defaults = NSUserDefaults.standardUserDefaults
                defaults.setObject(listOf(iso), forKey = "app_language")
                defaults.synchronize()
            }
        }
    }
}
