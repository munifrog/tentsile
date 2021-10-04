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
                Menu {
                    Button ("Reset Tether Points", action: resetAnchors)
                    Button ("Tentsile Website", action: resetAnchors)
                    VStack {
                        if config.units == .metric {
                            Button ("Use feet (ft)", action: setImperial)
                        } else {
                            Button ("Use meters (m)", action: setMetric)
                        }
                    }
                } label: {
                    Label("more", systemImage: "ellipsis")
                        .labelStyle(IconOnlyLabelStyle())
                        .frame(width: 30, height: 30, alignment: .center)
                        .background(Color("ThemeDark"))
                        .mask(Circle())
                }
            }
            .padding(.horizontal)
            .background(Color("ThemePrimary"))
            PlatformPicker(platform: $config.platform)
            HSlider()
            Spacer()
            Clearing(config: $config)
        }
        .background(Color("ThemeLight"))
    }

    func resetAnchors() {
        config.resetAnchors()
    }

    func setImperial() {
        config.units = .imperial
    }

    func setMetric() {
        config.units = .metric
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
