//
//  Perimeter.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/14/21.
//

import SwiftUI

struct Perimeter: View {
    @Binding var config: Configuration

    var body: some View {
        let limits = config.getLimits()
        Rectangle()
            .foregroundColor(.clear)
            .overlay(Path() { path in
                path.move(to: CGPoint(
                    x: CGFloat(limits.x + config.anchors.a.x),
                    y: CGFloat(limits.y + config.anchors.a.y)
                ))
                path.addLine(to: CGPoint(
                    x: CGFloat(limits.x + config.anchors.b.x),
                    y: CGFloat(limits.y + config.anchors.b.y)
                ))
                path.addLine(to: CGPoint(
                    x: CGFloat(limits.x + config.anchors.c.x),
                    y: CGFloat(limits.y + config.anchors.c.y)
                ))
                path.addLine(to: CGPoint(
                    x: CGFloat(limits.x + config.anchors.a.x),
                    y: CGFloat(limits.y + config.anchors.a.y)
                ))
            }.stroke(style: StrokeStyle(lineWidth: 3, dash: [18,6,3,6]))
            .foregroundColor(Color("Perimeter"))
            )
    }
}

struct Perimeter_Previews: PreviewProvider {
    static var previews: some View {
        Perimeter(config: .constant(Configuration(anchors: Anchors())))
    }
}
