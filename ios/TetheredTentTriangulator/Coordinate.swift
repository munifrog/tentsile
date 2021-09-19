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

    // Unary operator: -coord
    static prefix func - (coord: Coordinate) -> Coordinate {
        return Coordinate(x: -coord.x, y: -coord.y)
    }

    static func - (left: Coordinate, right: Coordinate) -> Coordinate {
        return Coordinate(x: left.x - right.x, y: left.y - right.y)
    }

    static func + (left: Coordinate, right: Coordinate) -> Coordinate {
        return Coordinate(x: left.x + right.x, y: left.y + right.y)
    }

    static func += (left: inout Coordinate, right: Coordinate) {
        left.x = left.x + right.x
        left.y = left.y + right.y
    }
}
