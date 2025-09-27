package net.thechance.mena.faith.presentation.extensions.timeFormatingHelper

data class TimeAgo(
    val amount: Int = noValue,
    val unit: TimeUnit = TimeUnit.SECONDS
)

const val noValue = 0