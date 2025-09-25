package net.thechance.mena.identity.data.di

import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

actual val IdentityPlatformModule = module {
    single { Darwin.create() }
}
