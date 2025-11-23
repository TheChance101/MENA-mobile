package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.thechance.mena.identity.data.dataSource.local.database.dao.UserDao
import net.thechance.mena.identity.data.dataSource.local.database.model.UserEntity
import net.thechance.mena.identity.data.dto.profile.request.ChangePasswordRequestDto
import net.thechance.mena.identity.data.dto.profile.request.UpdateProfileRequestDto
import net.thechance.mena.identity.data.dto.profile.response.ChangePasswordResponseDto
import net.thechance.mena.identity.data.dto.profile.response.ProfileResponseDto
import net.thechance.mena.identity.data.mapper.toDomain
import net.thechance.mena.identity.data.mapper.toEntity
import net.thechance.mena.identity.data.utils.deleteJson
import net.thechance.mena.identity.data.utils.formatAsString
import net.thechance.mena.identity.data.utils.getJson
import net.thechance.mena.identity.data.utils.postFileWithData
import net.thechance.mena.identity.data.utils.postFileWithDataAndTokens
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.postEmpty
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.data.utils.invalidateAuthTokens
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.UserRepository


class UserRepositoryImpl(
    private val client: HttpClient,
    private val userDao: UserDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {
    override suspend fun getUser(): Flow<User?> {
        return withContext(dispatcher) {
            launch {
                try {
                    val user: ProfileResponseDto = client.getJson(path = PROFILE)
                    userDao.upsert(user.toEntity())
                } catch (_: Exception) {
                }
            }

            userDao.getUser()
                .map { userEntity ->
                    userEntity?.toDomain()
                }
                .flowOn(dispatcher)
        }
    }

    override suspend fun updateUser(user: User) {
        return safeWrapper {
            val user: ProfileResponseDto = client.postJson(
                path = PROFILE,
                requestDto = user.toRequest(),
            )
            userDao.upsert(user.toEntity())
        }
    }

    override suspend fun uploadUserProfileImage(imageByteArray: ByteArray?) {
        return safeWrapper {
            client.postFileWithData(
                path = PROFILE_IMAGE,
                fileKey = "file",
                imageByteArray = imageByteArray
            )
        }
    }

    override suspend fun uploadUserProfileImageWithTokens(
        imageByteArray: ByteArray?,
        authTokens: AuthenticationTokens
    ) {
        return safeWrapper {
            client.postFileWithDataAndTokens(
                path = PROFILE_IMAGE,
                fileKey = "file",
                imageByteArray = imageByteArray,
                accessToken = authTokens.accessToken
            )
        }
    }

    override suspend fun deleteUserProfileImage() {
        return safeWrapper {
            client.deleteJson(path = PROFILE_IMAGE)
        }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        return safeWrapper {
            client.postJson<ChangePasswordRequestDto, ChangePasswordResponseDto>(
                requestDto = ChangePasswordRequestDto(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    confirmPassword = confirmPassword
                ),
                path = CHANGE_PASSWORD_PATH
            )
        }

    }

    override suspend fun deleteAccount() {
        safeWrapper {
            client.postEmpty(DELETE_ACCOUNT_PATH)
        }
        try {
            client.invalidateAuthTokens()
        } catch (_: Exception) {
        }
        withContext(dispatcher) {
            try {
                userDao.deleteUser()
            } catch (_: Exception) {
            }
        }
    }

    fun User.toRequest(): UpdateProfileRequestDto {
        return UpdateProfileRequestDto(
            firstName = this.firstName,
            lastName = this.lastName,
            imageUrl = this.profileImageUrl,
            username = this.username,
            birthDate = this.birthDate.formatAsString(),
            gender = when (this.gender) {
                Gender.MALE -> UserEntity.MALE
                else -> UserEntity.FEMALE
            }
        )
    }

    companion object {
        const val PROFILE = "identity/profile"
        const val PROFILE_IMAGE = "identity/profile/image"
        const val CHANGE_PASSWORD_PATH = "identity/profile/change-password"
        const val DELETE_ACCOUNT_PATH = "identity/profile/delete-account"

    }
}