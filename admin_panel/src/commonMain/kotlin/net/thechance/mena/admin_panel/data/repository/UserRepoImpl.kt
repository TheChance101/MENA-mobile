package net.thechance.mena.admin_panel.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.thechance.mena.admin_panel.domain.entity.User
import net.thechance.mena.admin_panel.domain.entity.User.UserState
import net.thechance.mena.admin_panel.domain.repository.UserRepo
import org.koin.core.annotation.Single
import java.time.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Single
@OptIn(ExperimentalUuidApi::class)
class UserRepoImpl : UserRepo {
    private val _users = MutableStateFlow<List<User>>(emptyList())

    init {
        _users.value = createMockUsers()
    }

    override fun getAllUsers(): Flow<List<User>> {
        return _users.asStateFlow()
    }

    suspend fun updateUserState(userId: Uuid, newState: UserState) {
        _users.value = _users.value.map { user ->
            if (user.id == userId) {
                user.copy(userState = newState)
            } else {
                user
            }
        }
    }

    private fun createMockUsers(): List<User> = listOf(
        User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440001"),
            userName = "John Michael Smith",
            phoneNumber = "+1234567890",
            lastLoginDate = LocalDate.now().minusDays(1),
            lastVisitDate = LocalDate.now(),
            userState = UserState.ACTIVE
        ),
        User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440002"),
            userName = "Sarah Elizabeth Johnson",
            phoneNumber = "+1234567891",
            lastLoginDate = LocalDate.now().minusDays(3),
            lastVisitDate = LocalDate.now().minusDays(2),
            userState = UserState.ACTIVE
        ),
        User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440003"),
            userName = "Michael James Brown",
            phoneNumber = "+1234567892",
            lastLoginDate = LocalDate.now().minusDays(5),
            lastVisitDate = LocalDate.now().minusDays(4),
            userState = UserState.BLOCKED
        ),
        User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440004"),
            userName = "Emily Grace Davis",
            phoneNumber = "+1234567893",
            lastLoginDate = LocalDate.now().minusDays(2),
            lastVisitDate = LocalDate.now().minusDays(1),
            userState = UserState.ACTIVE
        ),
        User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440005"),
            userName = "David Robert Wilson",
            phoneNumber = "+1234567894",
            lastLoginDate = LocalDate.now().minusWeeks(1),
            lastVisitDate = LocalDate.now().minusDays(6),
            userState = UserState.BLOCKED
        ),
        User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440006"),
            userName = "Jessica Marie Martinez",
            phoneNumber = "+1234567895",
            lastLoginDate = LocalDate.now(),
            lastVisitDate = LocalDate.now(),
            userState = UserState.ACTIVE
        ),
        User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440007"),
            userName = "James Alexander Anderson",
            phoneNumber = "+1234567896",
            lastLoginDate = LocalDate.now().minusDays(4),
            lastVisitDate = LocalDate.now().minusDays(3),
            userState = UserState.ACTIVE
        ),
        User(
            id = Uuid.parse("550e8400-e29b-41d4-a716-446655440008"),
            userName = "Lisa Anne Taylor",
            phoneNumber = "+1234567897",
            lastLoginDate = LocalDate.now().minusMonths(1),
            lastVisitDate = LocalDate.now().minusWeeks(2),
            userState = UserState.BLOCKED
        )
    )
}
