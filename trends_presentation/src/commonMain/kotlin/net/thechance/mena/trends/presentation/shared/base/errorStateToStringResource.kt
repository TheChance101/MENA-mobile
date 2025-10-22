package net.thechance.mena.trends.presentation.shared.base

import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.error_generic
import mena.trends_presentation.generated.resources.error_no_internet
import mena.trends_presentation.generated.resources.error_request_timeout
import mena.trends_presentation.generated.resources.max_file_duration_exceeded
import mena.trends_presentation.generated.resources.max_file_size_exceeded
import net.thechance.mena.trends.presentation.screen.update_categories.UpdateCategoryErrorState
import net.thechance.mena.trends.presentation.screen.upload_reel.UploadReelErrorState
import org.jetbrains.compose.resources.StringResource

internal fun UploadReelErrorState.toStringResource(): StringResource {
    return when (this) {
        UploadReelErrorState.NoInternet -> Res.string.error_no_internet
        is UploadReelErrorState.RequestFailed -> Res.string.error_generic
        UploadReelErrorState.RequestTimeout -> Res.string.error_request_timeout
        UploadReelErrorState.FileTooLarge -> Res.string.max_file_size_exceeded
        UploadReelErrorState.DurationTooLarge -> Res.string.max_file_duration_exceeded
    }
}

internal fun UpdateCategoryErrorState.toStringResource(): StringResource {
    return when (this) {
        UpdateCategoryErrorState.NoInternet -> Res.string.error_no_internet
        is UpdateCategoryErrorState.RequestFailed -> Res.string.error_generic
        UpdateCategoryErrorState.RequestTimeout -> Res.string.error_request_timeout
    }
}