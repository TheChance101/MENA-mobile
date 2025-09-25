package net.thechance.mena.trends.presentation.shared.util.logger

import net.thechance.mena.trends.domain.util.Logger
import org.koin.core.annotation.Single

@Single(binds = [Logger::class])
internal class LoggerDefault : Logger {
    override fun logError(tag: String, message: String) {
        logErrorPlatform(tag, message)
    }
}

expect fun logErrorPlatform(tag: String, message: String)