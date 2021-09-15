//
//  Anchors.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/11/21.
//

import Foundation

struct Anchors {
    var a: Coordinate
    var b: Coordinate
    var c: Coordinate
    var ab: Float {
        let ab_x = a.x - b.x
        let ab_y = a.y - b.y
        return sqrt(ab_x * ab_x + ab_y * ab_y)
    }
    var bc: Float {
        let bc_x = b.x - c.x
        let bc_y = b.y - c.y
        return sqrt(bc_x * bc_x + bc_y * bc_y)
    }
    var ca: Float {
        let ca_x = c.x - a.x
        let ca_y = c.y - a.y
        return sqrt(ca_x * ca_x + ca_y * ca_y)
    }

    init(anchors: Anchors) {
        self.a = anchors.a
        self.b = anchors.b
        self.c = anchors.c
    }

    init() {
        let refLength: Double = 125.0
        let offset = 15 * Double.pi / 180
        var angle: Double

        angle = offset
        self.a = Coordinate(
            x: (refLength * cos(angle)),
            y: (refLength * sin(angle))
        )
        angle = offset + (2.0 * Double.pi / 3.0)
        self.b = Coordinate(
            x: (refLength * cos(angle)),
            y: (refLength * sin(angle))
        )
        angle = offset + (4.0 * Double.pi / 3.0)
        self.c = Coordinate(
            x: (refLength * cos(angle)),
            y: (refLength * sin(angle))
        )
    }
}
