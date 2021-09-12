//
//  AnchorView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/12/21.
//

import SwiftUI

struct AnchorView: View {
    var anchors: Anchors

    private let diamFree: CGFloat = 15
    private let diamSelected: CGFloat = 30

    var body: some View {
        let screenSize = UIScreen.main.bounds
        let halfWidth: Float = Float(screenSize.width) / 2.0
        let halfHeight: Float = Float(screenSize.height) / 2.0

        Rectangle()
            .foregroundColor(.clear)
            .aspectRatio(0.66667, contentMode: .fit)
            .overlay(
                Circle()
                    .fill(Color.green)
                    .frame(width: diamFree, height: diamFree, alignment: .center)
                    .position(
                        x: CGFloat(halfWidth + anchors.a.x),
                        y: CGFloat(halfHeight + anchors.a.y)
                    )
                )
            .overlay(
                Circle()
                    .fill(Color.green)
                    .frame(width: diamFree, height: diamFree, alignment: .center)
                    .position(
                        x: CGFloat(halfWidth + anchors.b.x),
                        y: CGFloat(halfHeight + anchors.b.y)
                    )
                )
            .overlay(
                Circle()
                    .fill(Color.green)
                    .frame(width: diamFree, height: diamFree, alignment: .center)
                    .position(
                        x: CGFloat(halfWidth + anchors.c.x),
                        y: CGFloat(halfHeight + anchors.c.y)
                    )
                )
    }
}

struct AnchorView_Previews: PreviewProvider {
    static var previews: some View {
        AnchorView(anchors: Anchors())
    }
}
