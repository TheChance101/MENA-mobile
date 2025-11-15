@file:OptIn(ExperimentalTime::class)

package net.thechance.mena.wallet.data.mapper

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import kotlin.time.ExperimentalTime

fun TransactionFilterParams.toRequest(page: Int, pageSize: Int): HttpRequestBuilder.() -> Unit = {
    val timezone = TimeZone.currentSystemDefault()
    types?.forEach { parameter("type", it.name) }
    status?.let { parameter("status", it.name) }

    startDate?.let {
        val startDateTime = it.atStartOfDayIn(timezone)
        parameter("from", startDateTime.toString().removeSuffix("Z"))
    }

    endDate?.let {
        val endDateTime = it.plus(DatePeriod(days = 1)).atStartOfDayIn(timezone)
        parameter("to", endDateTime.toString().removeSuffix("Z"))
    }
    parameter("page", page)
    parameter("size", pageSize)
}