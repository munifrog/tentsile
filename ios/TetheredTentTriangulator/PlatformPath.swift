//
//  Platform.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 10/2/21.
//

import SwiftUI

private let MATH_DIVIDE_BY_SQRT_THREE: Float = sqrt(3) / 3
private let MATH_TWO_THIRDS_PI: Float = 2 * .pi / 3
private let MATH_COS_TWO_THIRDS_PI: Float = cos(MATH_TWO_THIRDS_PI)
private let MATH_SIN_TWO_THIRDS_PI: Float = sin(MATH_TWO_THIRDS_PI)

private let TENTSILE_CENTER_HOLE_HYPOTENUSE: Float = 0.6
private let TENTSILE_HYPOTENUSE_STINGRAY: Float = 4.1
private let TENTSILE_HYPOTENUSE_TRILLIUM: Float = 4.1
private let TENTSILE_HYPOTENUSE_TRILLIUM_XL: Float = 6.0
private let TENTSILE_HYPOTENUSE_VISTA: Float = 4.1
private let TENTSILE_HYPOTENUSE_UNIVERSE: Float = 4.4

struct PlatformPath {
    lazy var connect: [[Coordinate]] = [[Coordinate]]()
    lazy var duo: [[Coordinate]] = [[Coordinate]]()
    lazy var flite: [[Coordinate]] = [[Coordinate]]()
    lazy var stingray: [[Coordinate]] = getTentsileEquilateral(longest: TENTSILE_HYPOTENUSE_STINGRAY)
    lazy var t_mini: [[Coordinate]] = [[Coordinate]]()
    lazy var trillium: [[Coordinate]] = getTentsileEquilateral(longest: TENTSILE_HYPOTENUSE_TRILLIUM)
    lazy var trillium_xl: [[Coordinate]] = getTentsileEquilateral(longest: TENTSILE_HYPOTENUSE_TRILLIUM_XL)
    lazy var trilogy: [[Coordinate]] = [[Coordinate]]()
    lazy var una: [[Coordinate]] = [[Coordinate]]()
    lazy var universe: [[Coordinate]] = getTentsileEquilateral(longest: TENTSILE_HYPOTENUSE_UNIVERSE)
    lazy var vista: [[Coordinate]] = getTentsileEquilateral(longest: TENTSILE_HYPOTENUSE_VISTA)
}

// The Tentsile equilateral triangle tents and hammocks have a hole in the middle
private func getTentsileEquilateral(longest: Float) -> [[Coordinate]] {
    let distal = longest * MATH_DIVIDE_BY_SQRT_THREE
    let proximal = TENTSILE_CENTER_HOLE_HYPOTENUSE * MATH_DIVIDE_BY_SQRT_THREE

    var shapes = [[Coordinate]]()
    var coordArray = [Coordinate]()
    coordArray.append(
        Coordinate(
            x: distal,
            y: 0
        ))
    coordArray.append(
        Coordinate(
            x: proximal,
            y: 0
        ))
    coordArray.append(
        Coordinate(
            x: proximal * MATH_COS_TWO_THIRDS_PI,
            y: proximal * MATH_SIN_TWO_THIRDS_PI
        ))
    coordArray.append(
        Coordinate(
            x: distal * MATH_COS_TWO_THIRDS_PI,
            y: distal * MATH_SIN_TWO_THIRDS_PI
        ))

    let grouping: [Coordinate] = [Coordinate](coordArray)
    shapes.append(grouping)
    shapes.append(grouping.rotated(by: MATH_TWO_THIRDS_PI))
    shapes.append(grouping.rotated(by: -MATH_TWO_THIRDS_PI))
    return shapes
}
