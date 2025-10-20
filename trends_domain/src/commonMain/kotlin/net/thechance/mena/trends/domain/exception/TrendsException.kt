package net.thechance.mena.trends.domain.exception

sealed class TrendsException(message: String = ""): Exception(message)
class NoInternetException(message: String = ""): TrendsException(message)
class FileNotFoundException(message: String = ""): TrendsException(message)

open class VideoValidationException(message: String = ""): TrendsException(message)
class MaxFileSizeExceededException(message: String = ""): VideoValidationException(message)
class MaxFileDurationExceededException(message: String = ""): VideoValidationException(message)