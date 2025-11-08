package net.thechance.mena.di.utils

import net.thechance.mena.appEntryPoint.MainEntryViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class DIHelper: KoinComponent {
    fun getMainEntryViewModel(): MainEntryViewModel = this.get()
}