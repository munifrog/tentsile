//
//  Coordinate.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/11/21.
//

import Foundation
import SwiftUI

struct Coordinate {
    var x: Float
    var y: Float

    init(x: Float, y: Float) {
        self.x = x
        self.y = y
    }

    init(x: CGFloat, y: CGFloat) {
        self.x = Float(x)
        self.y = Float(y)
    }

    init(x: Double, y: Double) {
        self.x = Float(x)
        self.y = Float(y)
    }

    init(coordinate: Coordinate) {
        self.x = coordinate.x
        self.y = coordinate.y
    }

    init(coordinate: CGPoint) {
        self.x = Float(coordinate.x)
        self.y = Float(coordinate.y)
    }
}
