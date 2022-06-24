//
//  ContentView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/5/21.
//

import SwiftUI

struct ContentView: View {
    @State private var config = Configuration()
    @State private var isPortrait: Bool = true
    @State private var showFaq: Bool = false

    init() {
        self.isPortrait = getIsPortrait()
    }

    var body: some View {
        ZStack {
            Group {
                if isPortrait {
                    VContentView(
                        config: $config,
                        showFaq: $showFaq
                    )
                } else {
                    HContentView(
                        config: $config,
                        showFaq: $showFaq
                    )
                }
            }
            .background(Color("ThemeLight"))
            .onReceive(NotificationCenter.Publisher(center: .default, name: UIDevice.orientationDidChangeNotification)) { _ in
                self.isPortrait = getIsPortrait()
            }
            if showFaq {
                DraggableView(isPresented: $showFaq)
            }
        }
    }

    func getIsPortrait() -> Bool {
        // https://stackoverflow.com/a/65586833
        guard let scene = UIApplication.shared.windows.first?.windowScene
        else { return true }
        return scene.interfaceOrientation.isPortrait
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
