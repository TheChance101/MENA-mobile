package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.thechance.mena.identity.data.dataSource.local.database.dao.UserDao
import net.thechance.mena.identity.data.dataSource.local.database.model.UserEntity
import net.thechance.mena.identity.data.dto.profile.ProfileResponseDto
import net.thechance.mena.identity.data.dto.profile.UpdateProfileRequestDto
import net.thechance.mena.identity.data.mapper.toDomain
import net.thechance.mena.identity.data.mapper.toEntity
import net.thechance.mena.identity.data.utils.formatAsString
import net.thechance.mena.identity.data.utils.getJson
import net.thechance.mena.identity.data.utils.postFileWithData
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.repository.UserRepository
import kotlin.uuid.ExperimentalUuidApi


class UserRepositoryImpl(
    private val client: HttpClient,
    private val userDao: UserDao,
    private val scope: CoroutineScope
) : UserRepository {
    override suspend fun observeUser(): Flow<User?> {
        scope.launch {
            runCatching {
                val user: ProfileResponseDto = client.getJson(path = PROFILE)
                userDao.upsert(user.toEntity())
            }
        }

        return userDao.observeUser().map { userEntity -> userEntity?.toDomain() }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updateUser(
        user: User,
        shouldUpdateImage: Boolean,
        imageByteArray: ByteArray?,
    ) {
        return safeWrapper {
            val user: ProfileResponseDto = client.postFileWithData(
                path = PROFILE,
                dataKey = "user",
                requestDto = user.toRequest(shouldUpdateImage),
                fileKey = "file",
                imageByteArray = imageByteArray
            )
            userDao.upsert(user.toEntity())
        }
    }

    fun User.toRequest(shouldUpdateImage: Boolean): UpdateProfileRequestDto {
        return UpdateProfileRequestDto(
            firstName = this.firstName,
            lastName = this.lastName,
            imageUrl = this.profileImageUrl,
            username = this.username,
            birthDate = this.birthDate.formatAsString(),
            gender = when (this.gender) {
                Gender.MALE -> UserEntity.MALE
                else -> UserEntity.FEMALE
            },
            updateImage = shouldUpdateImage
        )
    }

    companion object {
        const val PROFILE = "identity/profile/me"
    }
}