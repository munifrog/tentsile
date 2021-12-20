//
//  TetherKnots.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/14/21.
//

import Foundation

struct TetherKnots {
    var a: [Coordinate]
    var b: [Coordinate]
    var c: [Coordinate]
    var pixels_a: Float
    var pixels_b: Float
    var pixels_c: Float
    var icon_a: AnchorIcon
    var icon_b: AnchorIcon
    var icon_c: AnchorIcon

    init(a: TetherDetails, b: TetherDetails, c: TetherDetails) {
        self.a = a.knots
        self.b = b.knots
        self.c = c.knots
        self.pixels_a = a.pixels
        self.pixels_b = b.pixels
        self.pixels_c = c.pixels
        self.icon_a = a.icon
        self.icon_b = b.icon
        self.icon_c = c.icon
    }
}
