package net.thechance.mena.identity.data.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.russhwolf.settings.Settings
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.CoroutineScope
import net.thechance.mena.identity.data.dataSource.local.database.IdentityDatabase
import net.thechance.mena.identity.data.dataSource.local.database.dao.AddressDao
import net.thechance.mena.identity.data.dataSource.local.database.dao.UserDao
import net.thechance.mena.identity.data.repository.AuthenticationRepositoryImpl
import net.thechance.mena.identity.data.repository.ImagesRepositoryImpl
import net.thechance.mena.identity.data.repository.ApplicationInfoRepositoryImpl
import net.thechance.mena.identity.data.repository.RegisterRepositoryImpl
import net.thechance.mena.identity.data.repository.RegistrationDraftRepositoryImpl
import net.thechance.mena.identity.data.repository.ResetPasswordRepositoryImpl
import net.thechance.mena.identity.data.repository.SettingsRepositoryImpl
import net.thechance.mena.identity.data.repository.UserRepositoryImpl
import net.thechance.mena.identity.data.repository.location.AddressesRepositoryImpl
import net.thechance.mena.identity.data.repository.location.GeocoderWrapper
import net.thechance.mena.identity.data.repository.location.MobileGeocoderWrapper
import net.thechance.mena.identity.domain.repository.AddressesRepository
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.domain.repository.ApplicationInfoRepository
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.service.AuthorizationService
import net.thechance.mena.identity.domain.service.LocalizationService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

private const val IDENTITY_CLIENT = "IdentityClient"
private const val COIL_CLIENT = "CoilClient"
private const val BASE_URL = "baseUrl"
private const val IDENTITY_SCOPE = "IdentityScope"

expect val IdentityPlatformModule: Module
val identityDataModule = module {
    single { CIO.create() }
    singleOf(::Settings)

    single<SettingsRepository>(createdAtStart = true) { SettingsRepositoryImpl(settings = get()) }
    single<UserRepository> {
        UserRepositoryImpl(
            client = get(named(IDENTITY_CLIENT)), userDao = get())
    }

    single<AuthenticationRepository> {
        AuthenticationRepositoryImpl(client = get(named(IDENTITY_CLIENT)), settings = get())
    }

    single<ResetPasswordRepository> {
        ResetPasswordRepositoryImpl(client = get(named(IDENTITY_CLIENT)))
    }

    single<RegisterRepository> {
        RegisterRepositoryImpl(client = get(named(IDENTITY_CLIENT)))
    }
    single<ApplicationInfoRepository> {
        ApplicationInfoRepositoryImpl(
            client = get(named(IDENTITY_CLIENT)),
            get ())
    }

    single<RegistrationDraftRepository> {
        RegistrationDraftRepositoryImpl(settings = get())
    }

    singleOf(::MobileGeocoderWrapper) bind GeocoderWrapper::class
    single<AddressesRepository> { 
        AddressesRepositoryImpl(
            client = get(named(IDENTITY_CLIENT)), 
            geocoder = get(), 
            addressDao = get(),
            scope = get(named(IDENTITY_SCOPE))
        ) 
    }

    singleOf(::ImagesRepositoryImpl) bind ImagesRepository::class
    singleOf(::AuthorizationService)
    singleOf(::LocalizationService)
    single(named(IDENTITY_CLIENT)) {
        provideHttpClient(
            engine = get(),
            baseUrl = get<String>(named(BASE_URL)),
            authorizationService = { get<AuthorizationService>() },
        )
    }

    single(named(COIL_CLIENT)) {
        provideCoilClient(engine = get())
    }

    single { provideDatabaseBuilder() }
    single<IdentityDatabase> { getRoomDatabase(builder = get()) }
    single<UserDao> { get<IdentityDatabase>().getUserDao() }
    single<AddressDao> { get<IdentityDatabase>().getAddressDao() }

    single(named(IDENTITY_SCOPE)) { CoroutineScope(Dispatchers.IO) }
}

private fun getRoomDatabase(builder: RoomDatabase.Builder<IdentityDatabase>): IdentityDatabase {
    return builder
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}