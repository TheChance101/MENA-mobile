package net.thechance.mena.wallet.presentation.utils

import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString as getMessage

interface StringProvider {
    suspend fun getString(resource: StringResource, vararg args: Any): String
}

class DefaultStringProvider : StringProvider {
    override suspend fun getString(resource: StringResource, vararg args: Any): String {
        return getMessage(resource, *args)
    }
}

