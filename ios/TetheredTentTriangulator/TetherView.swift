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
            let b_index = center.flips ? 2 : 1
            let c_index = center.flips ? 1 : 2
            let extremes = config.getExtremities()
            let limits = config.getLimits()
            let point = CGPoint(
                x: CGFloat(limits.x + center.p.x),
                y: CGFloat(limits.y + center.p.y)
            )
            Rectangle()
                .foregroundColor(.clear)
                .overlay(Path() { path in
                    path.move(to: point)
                    path.addLine(to: CGPoint(
                        x: CGFloat(extremes[0].x),
                        y: CGFloat(extremes[0].y)
                    ))
                    path.move(to: point)
                    path.addLine(to: CGPoint(
                        x: CGFloat(extremes[b_index].x),
                        y: CGFloat(extremes[b_index].y)
                    ))
                    path.move(to: point)
                    path.addLine(to: CGPoint(
                        x: CGFloat(extremes[c_index].x),
                        y: CGFloat(extremes[c_index].y)
                    ))
                }.stroke(style: StrokeStyle(lineWidth: 3))
                .foregroundColor(Color("TetherUnder"))
                )
                .overlay(Path() { path in
                    path.move(to: CGPoint(
                        x: CGFloat(extremes[0].x),
                        y: CGFloat(extremes[0].y)
                    ))
                    path.addLine(to: CGPoint(
                        x: CGFloat(limits.x + config.anchors.a.x),
                        y: CGFloat(limits.y + config.anchors.a.y)
                    ))
                    path.move(to: CGPoint(
                        x: CGFloat(extremes[b_index].x),
                        y: CGFloat(extremes[b_index].y)
                    ))
                    path.addLine(to: CGPoint(
                        x: CGFloat(limits.x + config.anchors.b.x),
                        y: CGFloat(limits.y + config.anchors.b.y)
                    ))
                    path.move(to: CGPoint(
                        x: CGFloat(extremes[c_index].x),
                        y: CGFloat(extremes[c_index].y)
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
