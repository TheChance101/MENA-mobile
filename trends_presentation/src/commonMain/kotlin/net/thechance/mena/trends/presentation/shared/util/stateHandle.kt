package net.thechance.mena.trends.presentation.shared.util

import androidx.lifecycle.SavedStateHandle
import net.thechance.mena.trends.presentation.navigation.Route

interface StateHandle {
    fun <T>get(key: String): T?
    fun <T> set(key: String, value: T)
}

internal fun StateHandle.toRoute(): Route.ReelDetails {
    val reelId: String = this.get("reelId") ?: throw IllegalStateException("reelId not found")
    return Route.ReelDetails(reelId)
}



class AndroidStateHandle(private val handle: SavedStateHandle) : StateHandle {
    override fun <T> get(key: String): T? = handle[key]
    override fun <T> set(key: String, value: T) {
        handle[key] = value
    }
}



