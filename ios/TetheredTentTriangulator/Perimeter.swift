//
//  Perimeter.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/14/21.
//

import SwiftUI

struct Perimeter: View {
    var anchors: Anchors

    var body: some View {
        let screenSize = UIScreen.main.bounds
        let halfWidth: Float = Float(screenSize.width) / 2.0
        let halfHeight: Float = Float(screenSize.height) / 2.0
        Rectangle()
            .foregroundColor(.clear)
            .aspectRatio(0.66667, contentMode: .fit)
            .overlay(Path() { path in
                path.move(to: CGPoint(
                    x: CGFloat(halfWidth + anchors.a.x),
                    y: CGFloat(halfHeight + anchors.a.y)
                ))
                path.addLine(to: CGPoint(
                    x: CGFloat(halfWidth + anchors.b.x),
                    y: CGFloat(halfHeight + anchors.b.y)
                ))
                path.addLine(to: CGPoint(
                    x: CGFloat(halfWidth + anchors.c.x),
                    y: CGFloat(halfHeight + anchors.c.y)
                ))
                path.addLine(to: CGPoint(
                    x: CGFloat(halfWidth + anchors.a.x),
                    y: CGFloat(halfHeight + anchors.a.y)
                ))
            }.stroke(style: StrokeStyle(lineWidth: 3, dash: [18,6,3,6]))
            .foregroundColor(Color("Perimeter"))
            )
    }
}

struct Perimeter_Previews: PreviewProvider {
    static var previews: some View {
        Perimeter(anchors: Anchors())
    }
}
