//
//  MenuView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/5/21.
//

import SwiftUI

struct MenuView: View {
    @Binding var config: Configuration
    @Binding var showFaq: Bool

    private let URL_TENTSILE_WEBSITE: String = "https://www.tentsile.com/"
    private let URL_SOURCE_CODE_IOS: String = "https://github.com/munifrog/tentsile/tree/master/ios/TetheredTentTriangulator"

    var body: some View {
        // https://stackoverflow.com/a/64845327
        Menu {
            Button ("Reset Tether Points", action: resetAnchors)
            Button ("FAQ", action: { showFaq.toggle() })
            Link("Tentsile Website", destination: URL(string: URL_TENTSILE_WEBSITE)!)
            if config.units == .metric {
                Button ("Use feet (ft)", action: setImperial)
            } else {
                Button ("Use meters (m)", action: setMetric)
            }
            SymbolMenuView(symbols: $config.symbols)
            Link("View the code!", destination: URL(string: URL_SOURCE_CODE_IOS)!)
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
        MenuView(
            config: .constant(Configuration()),
            showFaq: .constant(false)
        )
    }
}
