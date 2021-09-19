//
//  Extensions.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/19/21.
//

import SwiftUI

extension CGPoint {
    static func - (left: CGPoint, right: CGPoint) -> Coordinate {
        return Coordinate(x: left.x - right.x, y: left.y - right.y)
    }

    static func + (left: CGPoint, right: CGPoint) -> Coordinate {
        return Coordinate(x: left.x + right.x, y: left.y + right.y)
    }
}

extension CGSize {
    static func / (left: CGSize, divisor: CGFloat) -> CGPoint {
        return CGPoint(x: left.width / divisor, y: left.height / divisor)
    }
}
