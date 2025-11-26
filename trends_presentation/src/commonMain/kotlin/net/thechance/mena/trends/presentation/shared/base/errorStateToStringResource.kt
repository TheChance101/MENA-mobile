package net.thechance.mena.trends.presentation.shared.base

import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.error_generic
import mena.trends_presentation.generated.resources.error_no_internet
import mena.trends_presentation.generated.resources.error_request_timeout
import mena.trends_presentation.generated.resources.max_file_duration_exceeded
import mena.trends_presentation.generated.resources.max_file_size_exceeded
import org.jetbrains.compose.resources.StringResource

internal fun ErrorState.toStringResource(): StringResource {
    return when (this) {
        ErrorState.NoInternet -> Res.string.error_no_internet
        is ErrorState.RequestFailed -> Res.string.error_generic
        ErrorState.RequestTimeout -> Res.string.error_request_timeout
        UploadTrendErrorState.FileTooLarge -> Res.string.max_file_size_exceeded
        UploadTrendErrorState.DurationTooLarge -> Res.string.max_file_duration_exceeded
    }
}