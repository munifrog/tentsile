//
//  MenuView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/5/21.
//

import SwiftUI

struct MenuView: View {
    @Binding var config: Configuration

    private let URL_TENTSILE_WEBSITE: String = "https://www.tentsile.com/"

    var body: some View {
        Menu {
            Button ("Reset Tether Points", action: resetAnchors)
            Link("Tentsile Website", destination: URL(string: URL_TENTSILE_WEBSITE)!)
            VStack {
                if config.units == .metric {
                    Button ("Use feet (ft)", action: setImperial)
                } else {
                    Button ("Use meters (m)", action: setMetric)
                }
                SymbolMenuView(symbols: $config.symbols)
            }
        } label: {
            Label("more", systemImage: "ellipsis")
                .labelStyle(IconOnlyLabelStyle())
                .frame(width: 30, height: 30, alignment: .center)
                .background(Color("ThemeDark"))
                .mask(Circle())
        }
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

struct MenuView_Previews: PreviewProvider {
    static var previews: some View {
        MenuView(config: .constant(Configuration()))
    }
}
