//
//  TetherIconView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/14/21.
//

import SwiftUI

struct TetherIconView: View {
    var setup: DrawableSetup
    var symbols: Symbols

    var body: some View {
        if let knots = setup.knots {
            let dimen = CGFloat(20)
            let limits = setup.offset
            AnchorIconView(icon: knots.icon_a, level: symbols)
                .frame(width: dimen, height: dimen, alignment: .center)
                .position(CGPoint(limits + setup.anchors.a))
            AnchorIconView(icon: knots.icon_b, level: symbols)
                .frame(width: dimen, height: dimen, alignment: .center)
                .position(CGPoint(limits + setup.anchors.b))
            AnchorIconView(icon: knots.icon_c, level: symbols)
                .frame(width: dimen, height: dimen, alignment: .center)
                .position(CGPoint(limits + setup.anchors.c))
        } else {
            EmptyView()
        }
    }
}

struct TetherIconView_Previews: PreviewProvider {
    private static var config = Configuration()

    static var previews: some View {
        TetherIconView(
            setup: config.getDrawableSetup(),
            symbols: config.symbols
        )
    }
}
