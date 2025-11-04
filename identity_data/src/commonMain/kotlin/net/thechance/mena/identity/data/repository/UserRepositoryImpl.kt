package net.thechance.mena.identity.data.repository

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.thechance.mena.identity.data.dataSource.local.database.dao.UserDao
import net.thechance.mena.identity.data.dataSource.local.database.model.UserEntity
import net.thechance.mena.identity.data.dataSource.local.setting.appLanguage
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
import net.thechance.mena.identity.domain.util.AppLanguage
import kotlin.uuid.ExperimentalUuidApi


class UserRepositoryImpl(
    private val client: HttpClient,
    private val userDao: UserDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val settings: Settings
) : UserRepository {
    private val observableLanguage: MutableStateFlow<String> =
        MutableStateFlow(settings.appLanguage)

    override suspend fun getUser(): Flow<User?> {
        CoroutineScope(dispatcher).launch {
            try {
                val user: ProfileResponseDto = client.getJson(path = PROFILE)
                userDao.upsert(user.toEntity())
            } catch (_: Exception) {

            }
        }

        return userDao.getUser()
            .map { userEntity ->
                userEntity?.toDomain()
            }
            .flowOn(Dispatchers.IO)
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

    override fun applyLanguage(languageIso: String) {
        settings.appLanguage = languageIso.also { observableLanguage.value = it }
    }

    override fun observeAppLanguage(): Flow<AppLanguage> =
        observableLanguage.map { it.toAppLanguage() }

    override fun getCurrentAppLanguage(): AppLanguage = settings.appLanguage.toAppLanguage()

    companion object {
        const val PROFILE = "identity/profile"
    }

    private fun String.toAppLanguage(): AppLanguage {
        return when (this) {
            AppLanguage.ENGLISH.iso -> AppLanguage.ENGLISH
            AppLanguage.ARABIC.iso -> AppLanguage.ARABIC
            else -> AppLanguage.ENGLISH
        }
    }
}