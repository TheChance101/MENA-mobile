package net.thechance.mena.trends.presentation.shared.util

import net.thechance.mena.trends.presentation.screen.upload_reel.UploadReelScreenState.UploadingReelState

val UploadingReelState.isIdle: Boolean
    get() = this == UploadingReelState.IDLE

val UploadingReelState.isUploading: Boolean
    get() = this == UploadingReelState.UPLOADING

val UploadingReelState.isFailed: Boolean
    get() = this == UploadingReelState.FAILED

val UploadingReelState.isSuccess: Boolean
    get() = this == UploadingReelState.SUCCESS