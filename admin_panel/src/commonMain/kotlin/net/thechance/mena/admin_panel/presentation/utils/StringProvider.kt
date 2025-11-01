package net.thechance.mena.admin_panel.presentation.utils

import org.jetbrains.compose.resources.StringResource
import org.koin.core.annotation.Single
import org.jetbrains.compose.resources.getString as getMessage

interface StringProvider {
    suspend fun getString(resource: StringResource, vararg args: Any): String
}

@Single
class DefaultStringProvider : StringProvider {
    override suspend fun getString(resource: StringResource, vararg args: Any): String {
        return getMessage(resource, *args)
    }
}