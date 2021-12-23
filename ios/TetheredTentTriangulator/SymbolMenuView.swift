//
//  SymbolMenuView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/23/21.
//

import SwiftUI

struct SymbolMenuView: View {
    @Binding var symbols: Symbols

    var body: some View {
        if symbols > Symbols.none {
            Button ("Decrease symbols", action: decrease)
        }
        if symbols < Symbols.warn {
            Button ("Increase symbols", action: increase)
        }
    }

    func increase() {
        symbols++
    }

    func decrease() {
        symbols--
    }
}

struct SymbolMenuView_Previews: PreviewProvider {
    static var previews: some View {
        SymbolMenuView(symbols: .constant(Symbols.warn))
    }
}
