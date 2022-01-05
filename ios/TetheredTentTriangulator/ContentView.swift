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

    init() {
        self.orientation = getCurrentOrientation()
    }

    var body: some View {
        Group {
            if orientation.isPortrait {
                VContentView(config: $config)
            } else {
                HContentView(config: $config)
            }
        }
        .background(Color("ThemeLight"))
        .onReceive(NotificationCenter.Publisher(center: .default, name: UIDevice.orientationDidChangeNotification)) { _ in
            // See https://stackoverflow.com/a/58738150
            let orientation = UIDevice.current.orientation
            if orientation != UIDeviceOrientation.portraitUpsideDown {
                self.orientation = orientation
            }
        }
    }

    func getCurrentOrientation() -> UIDeviceOrientation {
        let orientation = UIDevice.current.orientation
        if orientation == UIDeviceOrientation.portraitUpsideDown {
            return UIDeviceOrientation.portrait
        } else {
            return orientation
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
