//
//  PerimeterLabels.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/15/21.
//

import SwiftUI

struct PerimeterLabels: View {
    var anchors: Anchors

    var body: some View {
        let screenSize = UIScreen.main.bounds
        let halfWidth: Float = Float(screenSize.width) / 2.0
        let halfHeight: Float = Float(screenSize.height) / 2.0
        Rectangle()
            .foregroundColor(.clear)
            .aspectRatio(0.66667, contentMode: .fit)
            .overlay(
                Text(String(format: "%3.1f", self.anchors.ab))
                    .font(.title)
                    .colorInvert()
                    .position(
                        x: CGFloat(halfWidth + (self.anchors.a.x + self.anchors.b.x) / 2),
                        y: CGFloat(halfHeight + (self.anchors.a.y + self.anchors.b.y) / 2)
                    )
            )
            .overlay(
                Text(String(format: "%3.1f", self.anchors.bc))
                    .font(.title)
                    .colorInvert()
                    .position(
                        x: CGFloat(halfWidth + (self.anchors.b.x + self.anchors.c.x) / 2),
                        y: CGFloat(halfHeight + (self.anchors.b.y + self.anchors.c.y) / 2)
                    )
            )
            .overlay(
                Text(String(format: "%3.1f", self.anchors.ca))
                    .font(.title)
                    .colorInvert()
                    .position(
                        x: CGFloat(halfWidth + (self.anchors.c.x + self.anchors.a.x) / 2),
                        y: CGFloat(halfHeight + (self.anchors.c.y + self.anchors.a.y) / 2)
                    )
            )
    }
}

struct PerimeterLabels_Previews: PreviewProvider {
    static var previews: some View {
        PerimeterLabels(anchors: Anchors())
    }
}
