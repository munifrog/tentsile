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
