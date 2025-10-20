package net.thechance.mena.faith.presentation.base

import net.thechance.mena.faith.domain.exception.FaithException
import org.jetbrains.compose.resources.StringResource

data class ErrorState(
    val message: StringResource,
    val exception: FaithException? = null
)