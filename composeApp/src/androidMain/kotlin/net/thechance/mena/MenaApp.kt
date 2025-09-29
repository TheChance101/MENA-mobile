package net.thechance.mena

import android.app.Application
import org.koin.android.ext.koin.androidContext

class MenaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin{
            androidContext(this@MenaApp)
        }
    }
}