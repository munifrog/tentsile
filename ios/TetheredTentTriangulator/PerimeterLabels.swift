//
//  PerimeterLabels.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/15/21.
//

import SwiftUI

struct PerimeterLabels: View {
    var setup: DrawableSetup
    var units: Units

    var body: some View {
        let limits = setup.offset
        let measureA = Util.getMeasureFromPixels(
            pixels: setup.anchors.ab,
            meterScale: setup.scaleMeters,
            units: units
        )
        let measureB = Util.getMeasureFromPixels(
            pixels: setup.anchors.bc,
            meterScale: setup.scaleMeters,
            units: units
        )
        let measureC = Util.getMeasureFromPixels(
            pixels: setup.anchors.ca,
            meterScale: setup.scaleMeters,
            units: units
        )

        Rectangle()
            .foregroundColor(.clear)
            .overlay(
                LabelView(
                    value: measureA,
                    offset_x: limits.x,
                    offset_y: limits.y,
                    a_x: setup.anchors.a.x,
                    a_y: setup.anchors.a.y,
                    b_x: setup.anchors.b.x,
                    b_y: setup.anchors.b.y,
                    units: units,
                    color: Color("FontPerimeter")
                )
            )
            .overlay(
                LabelView(
                    value: measureB,
                    offset_x: limits.x,
                    offset_y: limits.y,
                    a_x: setup.anchors.b.x,
                    a_y: setup.anchors.b.y,
                    b_x: setup.anchors.c.x,
                    b_y: setup.anchors.c.y,
                    units: units,
                    color: Color("FontPerimeter")
                )
            )
            .overlay(
                LabelView(
                    value: measureC,
                    offset_x: limits.x,
                    offset_y: limits.y,
                    a_x: setup.anchors.c.x,
                    a_y: setup.anchors.c.y,
                    b_x: setup.anchors.a.x,
                    b_y: setup.anchors.a.y,
                    units: units,
                    color: Color("FontPerimeter")
                )
            )
    }
}

struct PerimeterLabels_Previews: PreviewProvider {
    @State private static var config = Configuration(
        Coordinate(x: 160.0, y: 217.0)
    )

    static var previews: some View {
        PerimeterLabels(
            setup: config.getDrawableSetup(),
            units: config.units
        ).colorInvert()
    }
}
