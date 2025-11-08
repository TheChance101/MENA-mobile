import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
            .onOpenURL { url in
                let deepLink = parseDeepLink(from: url)
                DIHelper().getMainEntryViewModel().onDeepLinkChange(deepLink: deepLink)
            }
    }
}

private func parseDeepLink(from url: URL) -> DeepLink {
    let components = URLComponents(url: url, resolvingAgainstBaseURL: true)

    let userId = components?.queryItems?.first(where: { $0.name == "userId" })?.value

    return DeepLink(userId: userId)
}