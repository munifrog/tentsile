//
//  ContentView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/5/21.
//

import SwiftUI

struct ContentView: View {
    @State private var config = Configuration(anchors: Anchors())

    var body: some View {
        VStack {
            HStack {
                Text("Tethered Tent Triangulator")
                    .font(.headline)
                    .colorInvert()
                Spacer()
                MenuView(config: $config)
            }
            .padding(.horizontal)
            .background(Color("ThemePrimary"))
            HStack {
                PlatformPicker(platform: $config.platform)
                PlatformRotator(config: $config)
            }
            HSlider(position: $config.scale)
            Spacer()
            Clearing(config: $config)
        }
        .background(Color("ThemeLight"))
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
