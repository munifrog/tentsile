//
//  PerimeterLabels.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/15/21.
//

import SwiftUI

struct PerimeterLabels: View {
    @Binding var config: Configuration

    var body: some View {
        let limits = config.getLimits()
        Rectangle()
            .foregroundColor(.clear)
            .overlay(
                Text(String(format: "%3.1f", config.anchors.ab))
                    .font(.title)
                    .colorInvert()
                    .position(
                        x: CGFloat(limits.x + (config.anchors.a.x + config.anchors.b.x) / 2),
                        y: CGFloat(limits.y + (config.anchors.a.y + config.anchors.b.y) / 2)
                    )
            )
            .overlay(
                Text(String(format: "%3.1f", config.anchors.bc))
                    .font(.title)
                    .colorInvert()
                    .position(
                        x: CGFloat(limits.x + (config.anchors.b.x + config.anchors.c.x) / 2),
                        y: CGFloat(limits.y + (config.anchors.b.y + config.anchors.c.y) / 2)
                    )
            )
            .overlay(
                Text(String(format: "%3.1f", config.anchors.ca))
                    .font(.title)
                    .colorInvert()
                    .position(
                        x: CGFloat(limits.x + (config.anchors.c.x + config.anchors.a.x) / 2),
                        y: CGFloat(limits.y + (config.anchors.c.y + config.anchors.a.y) / 2)
                    )
            )
    }
}

struct PerimeterLabels_Previews: PreviewProvider {
    static var previews: some View {
        PerimeterLabels(config: .constant(Configuration(anchors: Anchors())))
    }
}
