//
//  Coordinate.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/11/21.
//

import Foundation

struct Coordinate {
    var x: Float
    var y: Float

    init(x: Float, y: Float) {
        self.x = x
        self.y = y
    }

    init(x: Double, y: Double) {
        self.x = Float(x)
        self.y = Float(y)
    }
}
