package net.thechance.mena.identity.presentation.core.mapper

import net.thechance.mena.identity.domain.entity.AddressType
import net.thechance.mena.identity.presentation.feature.authenticationFlow.login.LoginScreenUIEffect
import net.thechance.mena.identity.presentation.feature.locationFlow.addEditLocation.AddEditLocationScreenUIEffect
import net.thechance.mena.identity.presentation.feature.locationFlow.shared.AddressUIState
import net.thechance.mena.identity.presentation.feature.locationFlow.shared.CoordinatesUiState
import net.thechance.mena.identity.presentation.feature.profileFlow.profile.ProfileScreenUIEffect
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class EffectMapperTest {

    @Test
    fun `createNavigateToMapEffect should create NavigateToMap effect with addressModel`() {
        val addressUIState = AddressUIState(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440000"),
            addressType = AddressType.Home,
            isMainAddress = false,
            addressDetails = "Test Street",
            coordinates = CoordinatesUiState(33.3152, 44.3661)
        )
        val onSuccess: (AddressUIState) -> Unit = { }

        val result = createNavigateToMapEffect(addressUIState, onSuccess)

        assertEquals(AddEditLocationScreenUIEffect.NavigateToMap(addressUIState, onSuccess), result)
    }

    @Test
    fun `createNavigateToMapEffect should create NavigateToMap effect with null addressModel`() {
        val onSuccess: (AddressUIState) -> Unit = { }

        val result = createNavigateToMapEffect(null, onSuccess)

        assertEquals(AddEditLocationScreenUIEffect.NavigateToMap(null, onSuccess), result)
    }

    @Test
    fun `createNavigateToHomeEffect should return NavigateToHome effect`() {
        val result = createNavigateToHomeEffect()

        assertEquals(LoginScreenUIEffect.NavigateToHome, result)
    }

    @Test
    fun `createNavigateToEditProfileEffect should return NavigateToEditProfileScreen effect`() {
        val result = createNavigateToEditProfileEffect()

        assertEquals(ProfileScreenUIEffect.NavigateToEditProfileScreen, result)
    }
}