package net.thechance.mena.trends.presentation.video_player

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerLayer
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
class PlayerLayerView(player: AVPlayer) :
    UIView(frame = platform.CoreGraphics.CGRectZero.readValue()) {
    private val playerLayer = AVPlayerLayer()

    init {
        layer.addSublayer(playerLayer)
        playerLayer.player = player
        playerLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        playerLayer.frame = layer.bounds
    }
}