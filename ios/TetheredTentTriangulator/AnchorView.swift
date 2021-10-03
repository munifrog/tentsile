//
//  AnchorView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/12/21.
//

import SwiftUI

struct AnchorView: View {
    @Binding var config: Configuration

    private let diamFree: CGFloat = 15
    private let diamSelected: CGFloat = 30

    var body: some View {
        let limits = config.getLimits()
        let selection: Select = config.selection
        let diamA = selection == .anchor_a ? diamSelected : diamFree
        let diamB = selection == .anchor_b ? diamSelected : diamFree
        let diamC = selection == .anchor_c ? diamSelected : diamFree
        let diamTether = selection == .point ? diamSelected : diamFree

        Rectangle()
            .foregroundColor(.clear)
            .overlay(
                Circle()
                    .fill(Color.green)
                    .frame(width: diamA, height: diamA, alignment: .center)
                    .position(
                        x: CGFloat(limits.x + config.anchors.a.x),
                        y: CGFloat(limits.y + config.anchors.a.y)
                    )
                )
            .overlay(
                Circle()
                    .fill(Color.green)
                    .frame(width: diamB, height: diamB, alignment: .center)
                    .position(
                        x: CGFloat(limits.x + config.anchors.b.x),
                        y: CGFloat(limits.y + config.anchors.b.y)
                    )
                )
            .overlay(
                Circle()
                    .fill(Color.green)
                    .frame(width: diamC, height: diamC, alignment: .center)
                    .position(
                        x: CGFloat(limits.x + config.anchors.c.x),
                        y: CGFloat(limits.y + config.anchors.c.y)
                    )
                )
            .overlay(
                HStack {
                    if let center = config.center {
                        Circle()
                            .fill(Color("TetherProvided"))
                            .frame(width: diamTether, height: diamTether, alignment: .center)
                            .position(
                                x: CGFloat(limits.x + center.p.x),
                                y: CGFloat(limits.y + center.p.y)
                            )
                    } else {
                        EmptyView()
                    }
                }
            )
    }
}

struct AnchorView_Previews: PreviewProvider {
    static var previews: some View {
        AnchorView(config: .constant(Configuration(anchors: Anchors())))
    }
}
