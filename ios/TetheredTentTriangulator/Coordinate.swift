//
//  Coordinate.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/11/21.
//

import SwiftUI

struct Coordinate {
    var x: Float
    var y: Float

    init() {
        self.x = 0
        self.y = 0
    }

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

    static func * (left: Float, right: Coordinate) -> Coordinate {
        return Coordinate(x: left * right.x, y: left * right.y)
    }

    static func * (left: Coordinate, right: Float) -> Coordinate {
        return Coordinate(x: right * left.x, y: right * left.y)
    }

    static func += (left: inout Coordinate, right: Coordinate) {
        left.x = left.x + right.x
        left.y = left.y + right.y
    }
}

extension Array where Element == Coordinate {
    func rotated(by angle: Float) -> [Coordinate] {
        var rotated = [Coordinate]()

        let sineAngle = sin(angle)
        let cosineAngle = cos(angle)

        for coord in self {
            rotated.append(Coordinate(
                x: coord.x * cosineAngle - coord.y * sineAngle,
                y: coord.y * cosineAngle + coord.x * sineAngle
            ))
        }
        return rotated
    }

    func scaled(by scale: Float) -> [Coordinate] {
        var scaled = [Coordinate]()
        for coord in self {
            scaled.append(Coordinate(
                x: scale * coord.x,
                y: scale * coord.y
            ))
        }
        return scaled
    }

    func translated(by amount: Coordinate) -> [Coordinate] {
        var moved = [Coordinate]()
        for coord in self {
            moved.append(Coordinate(
                x: amount.x + coord.x,
                y: amount.y + coord.y
            ))
        }
        return moved
    }

    func asPathView() -> Path {
        var path = Path()
        if !isEmpty {
            path.move(to: CGPoint(x: CGFloat(self[0].x), y: CGFloat(self[0].y)))
            for i in 1..<count  {
                path.addLine(to: CGPoint(
                    x: CGFloat(self[i].x),
                    y: CGFloat(self[i].y)
                ))
            }
        }
        return path
    }
}

extension Array where Element == [Coordinate] {
    func rotated(by angle: Float) -> [[Coordinate]] {
        var groupingArray = [[Coordinate]]()
        for index in self {
            groupingArray.append(index.rotated(by: angle))
        }
        return groupingArray
    }

    func scaled(by scale: Float) -> [[Coordinate]] {
        var groupingArray = [[Coordinate]]()
        for index in self {
            groupingArray.append(index.scaled(by: scale))
        }
        return groupingArray
    }

    func translated(by amount: Coordinate) -> [[Coordinate]] {
        var groupingArray = [[Coordinate]]()
        for index in self {
            groupingArray.append(index.translated(by: amount))
        }
        return groupingArray
    }

    func asPathView() -> Path {
        var path = Path()
        if !self.isEmpty {
            for index in self {
                if !index.isEmpty {
                    path.move(to: CGPoint(x: CGFloat(index[0].x), y: CGFloat(index[0].y)))
                    for i in 1..<index.count {
                        path.addLine(to: CGPoint(
                            x: CGFloat(index[i].x),
                            y: CGFloat(index[i].y)
                        ))
                    }
                }
            }
        }
        return path
    }
}
