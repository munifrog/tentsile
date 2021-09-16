//
//  TetherView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/15/21.
//

import SwiftUI

struct TetherView: View {
    let config: Configuration

    var body: some View {
        if let center = config.center {
            let screenSize = UIScreen.main.bounds
            let halfWidth: Float = Float(screenSize.width) / 2.0
            let halfHeight: Float = Float(screenSize.height) / 2.0
            Rectangle()
                .foregroundColor(.clear)
                .aspectRatio(0.66667, contentMode: .fit)
                .overlay(Path() { path in
                    path.move(to: CGPoint(
                        x: CGFloat(halfWidth + center.p.x),
                        y: CGFloat(halfHeight + center.p.y)
                    ))
                    path.addLine(to: CGPoint(
                        x: CGFloat(halfWidth + config.anchors.a.x),
                        y: CGFloat(halfHeight + config.anchors.a.y)
                    ))
                    path.move(to: CGPoint(
                        x: CGFloat(halfWidth + center.p.x),
                        y: CGFloat(halfHeight + center.p.y)
                    ))
                    path.addLine(to: CGPoint(
                        x: CGFloat(halfWidth + config.anchors.b.x),
                        y: CGFloat(halfHeight + config.anchors.b.y)
                    ))
                    path.move(to: CGPoint(
                        x: CGFloat(halfWidth + center.p.x),
                        y: CGFloat(halfHeight + center.p.y)
                    ))
                    path.addLine(to: CGPoint(
                        x: CGFloat(halfWidth + config.anchors.c.x),
                        y: CGFloat(halfHeight + config.anchors.c.y)
                    ))
                }.stroke(style: StrokeStyle(lineWidth: 3))
                .foregroundColor(Color("TetherProvided"))
                )
        }
    }
}

struct TetherView_Previews: PreviewProvider {
    static var previews: some View {
        TetherView(config: Configuration(anchors: Anchors()))
    }
}
