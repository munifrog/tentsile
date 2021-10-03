//
//  TetherView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/15/21.
//

import SwiftUI

struct TetherView: View {
    @Binding var config: Configuration

    var body: some View {
        if let center = config.center {
            let limits = config.getLimits()
            Rectangle()
                .foregroundColor(.clear)
                .overlay(Path() { path in
                    path.move(to: CGPoint(
                        x: CGFloat(limits.x + center.p.x),
                        y: CGFloat(limits.y + center.p.y)
                    ))
                    path.addLine(to: CGPoint(
                        x: CGFloat(limits.x + config.anchors.a.x),
                        y: CGFloat(limits.y + config.anchors.a.y)
                    ))
                    path.move(to: CGPoint(
                        x: CGFloat(limits.x + center.p.x),
                        y: CGFloat(limits.y + center.p.y)
                    ))
                    path.addLine(to: CGPoint(
                        x: CGFloat(limits.x + config.anchors.b.x),
                        y: CGFloat(limits.y + config.anchors.b.y)
                    ))
                    path.move(to: CGPoint(
                        x: CGFloat(limits.x + center.p.x),
                        y: CGFloat(limits.y + center.p.y)
                    ))
                    path.addLine(to: CGPoint(
                        x: CGFloat(limits.x + config.anchors.c.x),
                        y: CGFloat(limits.y + config.anchors.c.y)
                    ))
                }.stroke(style: StrokeStyle(lineWidth: 3))
                .foregroundColor(Color("TetherProvided"))
                )
        }
    }
}

struct TetherView_Previews: PreviewProvider {
    static var previews: some View {
        TetherView(config: .constant(Configuration(anchors: Anchors())))
    }
}
