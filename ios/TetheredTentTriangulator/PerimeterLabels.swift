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
                LabelView(
                    value: config.anchors.ab,
                    offset_x: limits.x,
                    offset_y: limits.y,
                    a_x: config.anchors.a.x,
                    a_y: config.anchors.a.y,
                    b_x: config.anchors.b.x,
                    b_y: config.anchors.b.y
                )
            )
            .overlay(
                LabelView(
                    value: config.anchors.bc,
                    offset_x: limits.x,
                    offset_y: limits.y,
                    a_x: config.anchors.b.x,
                    a_y: config.anchors.b.y,
                    b_x: config.anchors.c.x,
                    b_y: config.anchors.c.y
                )
            )
            .overlay(
                LabelView(
                    value: config.anchors.ca,
                    offset_x: limits.x,
                    offset_y: limits.y,
                    a_x: config.anchors.c.x,
                    a_y: config.anchors.c.y,
                    b_x: config.anchors.a.x,
                    b_y: config.anchors.a.y
                )
            )
    }
}

struct PerimeterLabels_Previews: PreviewProvider {
    static var previews: some View {
        PerimeterLabels(config: .constant(Configuration(anchors: Anchors())))
    }
}
