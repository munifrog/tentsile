//
//  AnchorView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/12/21.
//

import SwiftUI

struct AnchorView: View {
    var config: Configuration

    private let diamFree: CGFloat = 15
    private let diamSelected: CGFloat = 30

    var body: some View {
        let limits = config.getLimits()

        Rectangle()
            .foregroundColor(.clear)
            .aspectRatio(0.66667, contentMode: .fit)
            .overlay(
                Circle()
                    .fill(Color.green)
                    .frame(width: diamFree, height: diamFree, alignment: .center)
                    .position(
                        x: CGFloat(limits.x + config.anchors.a.x),
                        y: CGFloat(limits.y + config.anchors.a.y)
                    )
                )
            .overlay(
                Circle()
                    .fill(Color.green)
                    .frame(width: diamFree, height: diamFree, alignment: .center)
                    .position(
                        x: CGFloat(limits.x + config.anchors.b.x),
                        y: CGFloat(limits.y + config.anchors.b.y)
                    )
                )
            .overlay(
                Circle()
                    .fill(Color.green)
                    .frame(width: diamFree, height: diamFree, alignment: .center)
                    .position(
                        x: CGFloat(limits.x + config.anchors.c.x),
                        y: CGFloat(limits.y + config.anchors.c.y)
                    )
                )
    }
}

struct AnchorView_Previews: PreviewProvider {
    static var previews: some View {
        AnchorView(config: Configuration(anchors: Anchors()))
    }
}
