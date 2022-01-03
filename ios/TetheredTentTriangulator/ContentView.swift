//
//  ContentView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/5/21.
//

import SwiftUI

struct ContentView: View {
    @State private var config = Configuration()
    @State private var orientation: UIDeviceOrientation = UIDevice.current.orientation

    var body: some View {
        Group {
            if orientation.isPortrait {
                VContentView(config: $config)
                    .background(Color("ThemeLight"))
            } else {
                HContentView(config: $config)
                    .background(Color("ThemeLight"))
            }
        }
        .background(Color("ThemeLight"))
        .onReceive(NotificationCenter.Publisher(center: .default, name: UIDevice.orientationDidChangeNotification)) { _ in
            // See https://stackoverflow.com/a/58738150
            self.orientation = UIDevice.current.orientation
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
