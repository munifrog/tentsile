//
//  PlatformLabelsView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/20/21.
//

import SwiftUI

struct PlatformLabelsView: View {
    @Binding var config: Configuration

    var body: some View {
        if let k = config.knots {
            let metersA = config.getDistance(k.pixels_a)
            let metersB = config.getDistance(k.pixels_b)
            let metersC = config.getDistance(k.pixels_c)
            let lastA = k.a.count - 1
            let lastB = k.b.count - 1
            let lastC = k.c.count - 1
            ZStack {
                if metersA > 0 {
                    Rectangle()
                        .foregroundColor(.clear)
                        .overlay(
                            LabelView(
                                value: metersA,
                                offset_x: 0,
                                offset_y: 0,
                                a_x: k.a[1].x,
                                a_y: k.a[1].y,
                                b_x: k.a[lastA].x,
                                b_y: k.a[lastA].y,
                                units: config.units,
                                color: Color("FontTethers")
                            )
                        )
                }
                if metersB > 0 {
                    Rectangle()
                        .foregroundColor(.clear)
                        .overlay(
                            LabelView(
                                value: metersB,
                                offset_x: 0,
                                offset_y: 0,
                                a_x: k.b[1].x,
                                a_y: k.b[1].y,
                                b_x: k.b[lastB].x,
                                b_y: k.b[lastB].y,
                                units: config.units,
                                color: Color("FontTethers")
                            )
                        )
                }
                if metersC > 0 {
                    Rectangle()
                        .foregroundColor(.clear)
                        .overlay(
                            LabelView(
                                value: metersC,
                                offset_x: 0,
                                offset_y: 0,
                                a_x: k.c[1].x,
                                a_y: k.c[1].y,
                                b_x: k.c[lastC].x,
                                b_y: k.c[lastC].y,
                                units: config.units,
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
    static var previews: some View {
        PlatformLabelsView(config: .constant(Configuration()))
    }
}
