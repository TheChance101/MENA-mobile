package net.thechance.mena.identity.data.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.russhwolf.settings.Settings
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.data.dataSource.local.database.IdentityDatabase
import net.thechance.mena.identity.data.dataSource.local.database.dao.UserDao
import net.thechance.mena.identity.data.repository.AuthenticationRepositoryImpl
import net.thechance.mena.identity.data.repository.ResetPasswordRepositoryImpl
import net.thechance.mena.identity.data.repository.UserRepositoryImpl
import net.thechance.mena.identity.data.repository.location.AddressesRepositoryImpl
import net.thechance.mena.identity.data.repository.location.GeocoderWrapper
import net.thechance.mena.identity.data.repository.location.MobileGeocoderWrapper
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.service.AuthorizationService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

private const val IDENTITY_CLIENT = "IdentityClient"
private const val BASE_URL = "baseUrl"

expect val IdentityPlatformModule: Module
val identityDataModule = module {
    single { CIO.create() }
    singleOf(::Settings)

    single<UserRepository> {
        UserRepositoryImpl(client = get(named(IDENTITY_CLIENT)), userDao = get())
    }

    single<AuthenticationRepository> {
        AuthenticationRepositoryImpl(client = get(named(IDENTITY_CLIENT)), settings = get())
    }

    single<ResetPasswordRepository> {
        ResetPasswordRepositoryImpl(client = get(named("IdentityClient")))
    }

    singleOf(::MobileGeocoderWrapper) bind GeocoderWrapper::class
    single<AddressesRepository> { AddressesRepositoryImpl(client = get(named(IDENTITY_CLIENT)), get()) }

    singleOf(::AuthorizationService)
    single(named(IDENTITY_CLIENT)) {
        provideHttpClient(
            engine = get(),
            baseUrl = get<String>(named(BASE_URL)),
            settings = get(),
            refreshToken = { get<AuthorizationService>().refreshToken() }
        )
    }

    single { provideDatabaseBuilder() }
    single<IdentityDatabase> { getRoomDatabase(builder = get()) }
    single<UserDao> { get<IdentityDatabase>().getUserDao() }

}

fun getRoomDatabase(builder: RoomDatabase.Builder<IdentityDatabase>): IdentityDatabase {
    return builder
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}