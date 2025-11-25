package net.thechance.mena.trends.presentation.shared.util

import net.thechance.mena.trends.presentation.screen.upload_trend.UploadTrendScreenState.UploadingTrendState

val UploadingTrendState.isIdle: Boolean
    get() = this == UploadingTrendState.IDLE

val UploadingTrendState.isUploading: Boolean
    get() = this == UploadingTrendState.UPLOADING

val UploadingTrendState.isFailed: Boolean
    get() = this == UploadingTrendState.FAILED

val UploadingTrendState.isSuccess: Boolean
    get() = this == UploadingTrendState.SUCCESS