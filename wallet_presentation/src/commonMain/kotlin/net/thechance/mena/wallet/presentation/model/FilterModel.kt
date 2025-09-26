package net.thechance.mena.wallet.presentation.model

import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.all
import mena.wallet_presentation.generated.resources.failed
import mena.wallet_presentation.generated.resources.online_purchase
import mena.wallet_presentation.generated.resources.received
import mena.wallet_presentation.generated.resources.sent
import mena.wallet_presentation.generated.resources.success
import org.jetbrains.compose.resources.StringResource

enum class FilterType(val labelRes: StringResource) {
    SENT(Res.string.sent),
    RECEIVED(Res.string.received),
    ONLINE_PURCHASE(Res.string.online_purchase)
}

enum class FilterStatus(val labelRes: StringResource) {
    ALL(Res.string.all),
    SUCCESS(Res.string.success),
    FAILED(Res.string.failed)
}