package net.thechance.mena.identity.presentation.api

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import net.thechance.mena.identity.api.IdentityFeatureApi
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.presentation.components.snackBar.IdentitySnackBarController
import net.thechance.mena.identity.presentation.components.snackBar.LocalSnackBarController
import net.thechance.mena.identity.presentation.screen.addresses.myAddresses.MyAddressesScreen
import net.thechance.mena.identity.presentation.screen.login.LoginScreen
import net.thechance.mena.identity.presentation.screen.profile.ProfileScreen
import net.thechance.mena.identity.presentation.screen.register.accountCreated.AccountCreatedScreen
import net.thechance.mena.identity.presentation.screen.register.uploadProfileImage.UploadProfileImageScreen
import org.koin.compose.koinInject

class IdentityFeatureApiImpl : IdentityFeatureApi {

    @Composable
    override fun ProfileTabEntry(updateBottomNavigationVisibility: (Boolean) -> Unit) {
        IdentityFeatureRoot {
            Navigator(ProfileScreen()) { navigator ->
                val current = navigator.lastItem
                LaunchedEffect(current.key) {
                    updateBottomNavigationVisibility(current is ProfileScreen)
                }

                CrossfadeTransition(
                    navigator = navigator,
                    animationSpec = tween(easing = EaseOut)
                )
            }
        }
    }

    @Composable
    override fun LoginFlow() {
        val authenticationRepository: AuthenticationRepository = koinInject()
        val registrationDraftRepository: RegistrationDraftRepository = koinInject()

        val loginScreen = remember { LoginScreen() }

        val initialScreen = determineInitialScreenOnce(
            authenticationRepository = authenticationRepository,
            registrationDraftRepository = registrationDraftRepository
        )

        IdentityFeatureRoot {
            RenderScreenWithNavigator(
                targetScreen = initialScreen,
                defaultScreen = loginScreen
            )
        }
    }

    @Composable
    override fun NavigateToAddressesScreen(onNavigateBack: (() -> Unit)?) {
        IdentityFeatureRoot {
            Navigator(MyAddressesScreen(onNavigateBack = onNavigateBack))
        }
    }

    @Composable
    private fun IdentityFeatureRoot(content: @Composable () -> Unit) {
        val snackBarController = remember { IdentitySnackBarController() }

        CompositionLocalProvider(
            LocalSnackBarController provides snackBarController
        ) {
            content()
        }
    }

    @Composable
    private fun determineInitialScreenOnce(
        authenticationRepository: AuthenticationRepository,
        registrationDraftRepository: RegistrationDraftRepository
    ): Any? {
        var initialScreen by remember { mutableStateOf<Any?>(null) }
        var isDetermined by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            if (!isDetermined) {
                val screen = determineInitialScreen(
                    authenticationRepository = authenticationRepository,
                    registrationDraftRepository = registrationDraftRepository
                )
                initialScreen = if (screen is LoginScreen) null else screen
                isDetermined = true
            }
        }

        return initialScreen
    }

    @Composable
    private fun RenderScreenWithNavigator(
        targetScreen: Any?,
        defaultScreen: LoginScreen
    ) {
        when (targetScreen) {
            is UploadProfileImageScreen -> {
                Navigator(targetScreen)
            }

            is AccountCreatedScreen -> {
                Navigator(targetScreen)
            }

            else -> {
                Navigator(defaultScreen)
            }
        }
    }

    private suspend fun determineInitialScreen(
        authenticationRepository: AuthenticationRepository,
        registrationDraftRepository: RegistrationDraftRepository
    ): Any {
        val authTokens = authenticationRepository.getAuthTokens()
            ?: return LoginScreen()

        val lastPhoneNumber = registrationDraftRepository.getLastPhoneNumber()
            ?: return LoginScreen()

        return getRegistrationFlowScreen(
            authTokens = authTokens,
            lastPhoneNumber = lastPhoneNumber,
            registrationDraftRepository = registrationDraftRepository
        )
    }

    private suspend fun getRegistrationFlowScreen(
        authTokens: AuthenticationTokens,
        lastPhoneNumber: PhoneNumber,
        registrationDraftRepository: RegistrationDraftRepository
    ): Any {
        val imageUploadCompleted = registrationDraftRepository.isImageUploadCompleted()

        return if (imageUploadCompleted) {
            AccountCreatedScreen(authTokens = authTokens, phoneNumber = lastPhoneNumber)
        } else {
            UploadProfileImageScreen(
                authTokens = authTokens,
                phoneNumber = lastPhoneNumber
            )
        }
    }
}