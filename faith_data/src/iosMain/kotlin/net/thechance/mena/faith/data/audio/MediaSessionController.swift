import Foundation
import MediaPlayer
import AVFoundation
import UIKit

@objc class MediaSessionController: NSObject {

    private var player: AVPlayer?

    @objc func bindPlayer(_ player: AVPlayer) {
        self.player = player
        setupRemoteCommands()
    }

    // MARK: Update Metadata

    @objc func updateNowPlaying(title: String, artist: String, artworkName: String?) {
        var info: [String: Any] = [
            MPMediaItemPropertyTitle: title,
            MPMediaItemPropertyArtist: artist
        ]

        if let name = artworkName, let image = UIImage(named: name) {
            let artwork = MPMediaItemArtwork(boundsSize: image.size) {
                _ in image
            }
            info[MPMediaItemPropertyArtwork] = artwork
        }

        MPNowPlayingInfoCenter.default().nowPlayingInfo = info
    }

    @objc func updatePlaybackState(isPlaying: Bool) {
        var info = MPNowPlayingInfoCenter.default().nowPlayingInfo ?? [:]
        info[MPNowPlayingInfoPropertyPlaybackRate] = isPlaying ? 1.0: 0.0
        MPNowPlayingInfoCenter.default().nowPlayingInfo = info
    }

    // MARK: Remote Command Center

    private func setupRemoteCommands() {
        let center = MPRemoteCommandCenter.shared()

        center.playCommand.addTarget {
            [weak self] _ in
            self ?.player ?.play()
            self ?.updatePlaybackState(isPlaying: true)
            return .success
        }

        center.pauseCommand.addTarget {
            [weak self] _ in
            self ?.player ?.pause()
            self ?.updatePlaybackState(isPlaying: false)
            return .success
        }

        center.nextTrackCommand.addTarget {
            _ in
            NotificationCenter.default.post(name: .quranNextAyah, object: nil)
            return .success
        }

        center.previousTrackCommand.addTarget {
            _ in
            NotificationCenter.default.post(name: .quranPreviousAyah, object: nil)
            return .success
        }
    }
}

extension Notification.Name {
    static let quranNextAyah = Notification.Name("quranNextAyah")
    static let quranPreviousAyah = Notification.Name("quranPreviousAyah")
}
