package net.thechance.mena.trends.domain.exception

sealed class TrendsException(message: String = ""): Exception(message)

class NoInternetException(message: String = ""): TrendsException(message)

class MaxFileSizeExceededException(message: String = ""): TrendsException(message)
class MaxFileDurationExceededException(message: String = ""): TrendsException(message)