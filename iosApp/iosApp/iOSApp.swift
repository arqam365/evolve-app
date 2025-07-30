import SwiftUI

@main
struct iOSApp: App {

    init() {
        KoinInitializer().startKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}