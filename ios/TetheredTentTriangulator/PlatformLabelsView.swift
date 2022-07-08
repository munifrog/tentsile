//
//  PlatformLabelsView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/20/21.
//

import SwiftUI

struct PlatformLabelsView: View {
    var setup: DrawableSetup
    var units: Units

    var body: some View {
        if let k = setup.knots {
            let measureA = Util.getInclinedMeasureFromPixels(
                pixels: k.pixels_a,
                meterScale: setup.scaleMeters,
                units: units
            )
            let precisionA = Util.getLimitedPrecision(measureA, units: self.units)
            let measureB = Util.getInclinedMeasureFromPixels(
                pixels: k.pixels_b,
                meterScale: setup.scaleMeters,
                units: units
            )
            let precisionB = Util.getLimitedPrecision(measureB, units: self.units)
            let measureC = Util.getInclinedMeasureFromPixels(
                pixels: k.pixels_c,
                meterScale: setup.scaleMeters,
                units: units
            )
            let precisionC = Util.getLimitedPrecision(measureC, units: self.units)

            let lastA = k.a.count - 1
            let lastB = k.b.count - 1
            let lastC = k.c.count - 1
            ZStack {
                if measureA > 0 {
                    Rectangle()
                        .foregroundColor(.clear)
                        .overlay(
                            LabelView(
                                value: precisionA,
                                offset_x: 0,
                                offset_y: 0,
                                a_x: k.a[1].x,
                                a_y: k.a[1].y,
                                b_x: k.a[lastA].x,
                                b_y: k.a[lastA].y,
                                units: units,
                                color: Color("FontTethers")
                            )
                        )
                }
                if measureB > 0 {
                    Rectangle()
                        .foregroundColor(.clear)
                        .overlay(
                            LabelView(
                                value: precisionB,
                                offset_x: 0,
                                offset_y: 0,
                                a_x: k.b[1].x,
                                a_y: k.b[1].y,
                                b_x: k.b[lastB].x,
                                b_y: k.b[lastB].y,
                                units: units,
                                color: Color("FontTethers")
                            )
                        )
                }
                if measureC > 0 {
                    Rectangle()
                        .foregroundColor(.clear)
                        .overlay(
                            LabelView(
                                value: precisionC,
                                offset_x: 0,
                                offset_y: 0,
                                a_x: k.c[1].x,
                                a_y: k.c[1].y,
                                b_x: k.c[lastC].x,
                                b_y: k.c[lastC].y,
                                units: units,
                                color: Color("FontTethers")
                            )
                        )
                }
            }
        } else {
            EmptyView()
        }
    }
}

struct PlatformLabelsView_Previews: PreviewProvider {
    @State private static var config = Configuration(
        Coordinate(x: 160.0, y: 217.0)
    )

    static var previews: some View {
        PlatformLabelsView(
            setup: config.getDrawableSetup(),
            units: config.units
        ).colorInvert()
    }
}
