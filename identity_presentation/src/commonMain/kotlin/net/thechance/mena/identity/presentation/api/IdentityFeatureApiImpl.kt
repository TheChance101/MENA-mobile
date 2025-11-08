package net.thechance.mena.identity.presentation.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import net.thechance.mena.identity.api.IdentityFeatureApi
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.AddressesScreen
import net.thechance.mena.identity.presentation.screen.login.LoginScreen
import net.thechance.mena.identity.presentation.screen.profile.ProfileScreen
import net.thechance.mena.identity.presentation.screen.register.accountCreated.AccountCreatedScreen
import net.thechance.mena.identity.presentation.screen.register.uploadProfileImage.UploadProfileImageScreen
import org.koin.compose.koinInject

class IdentityFeatureApiImpl : IdentityFeatureApi {
    @Composable
    override fun ProfileTabEntry() {
        Navigator(ProfileScreen())
    }

    @Composable
    override fun LoginFlow() {
        val authenticationRepository: AuthenticationRepository = koinInject()
        val registrationDraftRepository: RegistrationDraftRepository = koinInject()

        var initialScreen by remember { mutableStateOf<Any?>(null) }

        LaunchedEffect(Unit) {
            initialScreen =
                determineInitialScreen(authenticationRepository, registrationDraftRepository)
        }

        RenderScreen(initialScreen)
    }

    private suspend fun determineInitialScreen(
        authenticationRepository: AuthenticationRepository,
        registrationDraftRepository: RegistrationDraftRepository
    ): Any? {
        val authTokens = authenticationRepository.getAuthTokens() ?: return LoginScreen()

        val lastPhoneNumber = registrationDraftRepository.getLastPhoneNumber()
            ?: return LoginScreen()

        return determineScreenForRegistrationFlow(
            authTokens = authTokens,
            lastPhoneNumber = lastPhoneNumber,
            registrationDraftRepository = registrationDraftRepository
        )
    }

    private suspend fun determineScreenForRegistrationFlow(
        authTokens: AuthenticationTokens,
        lastPhoneNumber: PhoneNumber,
        registrationDraftRepository: RegistrationDraftRepository
    ): Any {
        val imageUploadCompleted = registrationDraftRepository.isImageUploadCompleted()
        return createScreenForRegistrationFlow(authTokens, lastPhoneNumber, imageUploadCompleted)
    }

    private fun createScreenForRegistrationFlow(
        authTokens: AuthenticationTokens,
        lastPhoneNumber: PhoneNumber,
        imageUploadCompleted: Boolean
    ): Any = if (imageUploadCompleted) {
        AccountCreatedScreen(authTokens = authTokens)
    } else {
        UploadProfileImageScreen(
            authTokens = authTokens,
            phoneNumber = lastPhoneNumber
        )
    }

    @Composable
    private fun RenderScreen(screen: Any?) {
        when (screen) {
            is UploadProfileImageScreen -> Navigator(screen)
            is AccountCreatedScreen -> Navigator(screen)
            is LoginScreen -> Navigator(screen)
            null -> Navigator(LoginScreen())
        }
    }

    @Composable
    override fun NavigateToAddressesScreen(onNavigateBack: (() -> Unit)?) {
        Navigator(AddressesScreen(onNavigateBack = onNavigateBack))
    }
}