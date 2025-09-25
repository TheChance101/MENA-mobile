package android.util

import kotlin.jvm.JvmStatic

@Suppress("unused")
object Log {
    @JvmStatic
    fun isLoggable(tag: String?, level: Int): Boolean = false
}
