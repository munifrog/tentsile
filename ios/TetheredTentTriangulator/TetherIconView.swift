//
//  TetherIconView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/14/21.
//

import SwiftUI

struct TetherIconView: View {
    @Binding var config: Configuration

    var body: some View {
        if let knots = config.knots {
            let dimen = CGFloat(20)
            let limits = config.getLimits()
            AnchorIconView(icon: knots.icon_a)
                .frame(width: dimen, height: dimen, alignment: .center)
                .position(CGPoint(limits + config.anchors.a))
            AnchorIconView(icon: knots.icon_b)
                .frame(width: dimen, height: dimen, alignment: .center)
                .position(CGPoint(limits + config.anchors.b))
            AnchorIconView(icon: knots.icon_c)
                .frame(width: dimen, height: dimen, alignment: .center)
                .position(CGPoint(limits + config.anchors.c))
        } else {
            EmptyView()
        }
    }
}

struct TetherIconView_Previews: PreviewProvider {
    static var previews: some View {
        TetherIconView(config: .constant(Configuration()))
    }
}
