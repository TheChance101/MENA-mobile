package net.thechance.mena

import android.app.Application
import net.thechance.mena.di.initKoin

class MenaApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {

        }
    }
}