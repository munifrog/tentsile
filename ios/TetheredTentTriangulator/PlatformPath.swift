//
//  Platform.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 10/2/21.
//

import SwiftUI

private let MATH_DIVIDE_BY_SQRT_THREE: Float = sqrt(3) / 3
private let MATH_FULL_CIRCLE: Float = 2 * .pi
private let MATH_QUARTER_CIRCLE: Float = MATH_FULL_CIRCLE / 4
private let MATH_TWO_THIRDS_PI: Float = MATH_FULL_CIRCLE / 3
private let MATH_COS_TWO_THIRDS_PI: Float = cos(MATH_TWO_THIRDS_PI)
private let MATH_SIN_TWO_THIRDS_PI: Float = sin(MATH_TWO_THIRDS_PI)

private let TENTSILE_BASE_CONNECT: Float = 2.7
private let TENTSILE_BASE_DUO: Float = 2.7
private let TENTSILE_BASE_FLITE: Float = 2.7
private let TENTSILE_BASE_T_MINI: Float = 2.7
private let TENTSILE_BASE_UNA: Float = 1.6
private let TENTSILE_BASE_TRILOGY: Float = TENTSILE_BASE_CONNECT
private let TENTSILE_CENTER_HOLE_HYPOTENUSE: Float = 0.6
private let TENTSILE_CIRCUMFERENCE_DEFAULT: Float = 0.785398163397448 // pi * 25cm or 10inch diameter
private let TENTSILE_CIRCUMFERENCE_UNA: Float = 0.628318530717959 // pi * 20cm or 8inch diameter
private let TENTSILE_NOTCH_SCALE: Float = 0.5
private let TENTSILE_HYPOTENUSE_CONNECT: Float = 4.0
private let TENTSILE_HYPOTENUSE_DUO: Float = 4.0
private let TENTSILE_HYPOTENUSE_FLITE: Float = 3.25
private let TENTSILE_HYPOTENUSE_STINGRAY: Float = 4.1
private let TENTSILE_HYPOTENUSE_T_MINI: Float = 3.25
private let TENTSILE_HYPOTENUSE_TRILLIUM: Float = 4.1
private let TENTSILE_HYPOTENUSE_TRILLIUM_XL: Float = 6.0
private let TENTSILE_HYPOTENUSE_VISTA: Float = 4.1
private let TENTSILE_HYPOTENUSE_UNA: Float = 2.9
private let TENTSILE_HYPOTENUSE_UNIVERSE: Float = 4.4
private let TENTSILE_HYPOTENUSE_TRILOGY: Float = TENTSILE_HYPOTENUSE_CONNECT
private let TENTSILE_STRAPS_DEFAULT: Float = 6.0
private let TENTSILE_STRAPS_UNA: Float = 4.0

private let TENTSILE_TETHER_ANGLE_BALANCED: Float = MATH_TWO_THIRDS_PI
private let TENTSILE_TETHER_ANGLE_CONNECT: Float = 1.69638705 // indented 30cm
private let TENTSILE_TETHER_ANGLE_DUO: Float = 1.69638705 // indented 30cm
private let TENTSILE_TETHER_ANGLE_FLITE: Float = 1.7804929 // indented 30cm
private let TENTSILE_TETHER_ANGLE_T_MINI: Float = 1.7804929 // indented 30cm
private let TENTSILE_TETHER_ANGLE_UNA: Float = 1.6053040 // indented 20cm
private let TENTSILE_TETHER_ANGLE_TRILOGY: Float = TENTSILE_TETHER_ANGLE_CONNECT

struct PlatformDetails {
    var path: [[Coordinate]]
    var circumference: Float
    var extremites: [Coordinate]
    var rotates: Bool
    var strap: Float
    var tetherangle: Float
}

struct PlatformPath {
    private var connect: PlatformDetails = getTenstsileIsosceles(
        hypotenuse: TENTSILE_HYPOTENUSE_CONNECT,
        base: TENTSILE_BASE_CONNECT,
        tetherangle: TENTSILE_TETHER_ANGLE_CONNECT)
    private var duo: PlatformDetails = getTenstsileIsosceles(
        hypotenuse: TENTSILE_HYPOTENUSE_DUO,
        base: TENTSILE_BASE_DUO,
        tetherangle: TENTSILE_TETHER_ANGLE_DUO)
    private var flite: PlatformDetails = getTenstsileIsosceles(
        hypotenuse: TENTSILE_HYPOTENUSE_FLITE,
        base: TENTSILE_BASE_FLITE,
        tetherangle: TENTSILE_TETHER_ANGLE_FLITE)
    private var stingray: PlatformDetails = getTentsileEquilateral(longest: TENTSILE_HYPOTENUSE_STINGRAY)
    private var t_mini: PlatformDetails = getTenstsileIsosceles(
        hypotenuse: TENTSILE_HYPOTENUSE_T_MINI,
        base: TENTSILE_BASE_T_MINI,
        tetherangle: TENTSILE_TETHER_ANGLE_T_MINI)
    private var trillium: PlatformDetails = getTentsileEquilateral(longest: TENTSILE_HYPOTENUSE_TRILLIUM)
    private var trillium_xl: PlatformDetails = getTentsileEquilateral(longest: TENTSILE_HYPOTENUSE_TRILLIUM_XL)
    private var trilogy: PlatformDetails = getTentsileTrilogy(
        hypotenuse: TENTSILE_HYPOTENUSE_TRILOGY,
        base: TENTSILE_BASE_TRILOGY,
        tetherangle: TENTSILE_TETHER_ANGLE_TRILOGY)
    private var una: PlatformDetails = getTenstsileIsosceles(
        hypotenuse: TENTSILE_HYPOTENUSE_UNA,
        base: TENTSILE_BASE_UNA,
        tetherangle: TENTSILE_TETHER_ANGLE_UNA,
        strap: TENTSILE_STRAPS_UNA,
        circumference: TENTSILE_CIRCUMFERENCE_UNA)
    private var universe: PlatformDetails = getTentsileEquilateral(longest: TENTSILE_HYPOTENUSE_UNIVERSE)
    private var vista: PlatformDetails = getTentsileEquilateral(longest: TENTSILE_HYPOTENUSE_VISTA)

    func getDetails(_ type: Platform) -> PlatformDetails {
        switch type {
        case .connect:
            return self.connect
        case .duo:
            return self.duo
        case .flite:
            return self.flite
        case .stingray:
            return self.stingray
        case .t_mini:
            return self.t_mini
        case .trillium:
            return self.trillium
        case .trillium_xl:
            return self.trillium_xl
        case .trilogy:
            return self.trilogy
        case .una:
            return self.una
        case .universe:
            return self.universe
        case .vista:
            return self.vista
        }
    }
}

// The Tentsile equilateral triangle tents and hammocks have a hole in the middle
private func getTentsileEquilateral(longest: Float) -> PlatformDetails {
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
    let extremes: [Coordinate] = [shapes[0][0], shapes[1][0], shapes[2][0]]

    return PlatformDetails(
        path: shapes,
        circumference: TENTSILE_CIRCUMFERENCE_DEFAULT,
        extremites: extremes,
        rotates: false,
        strap: TENTSILE_STRAPS_DEFAULT,
        tetherangle: TENTSILE_TETHER_ANGLE_BALANCED)
}

private func getTenstsileIsosceles(
    hypotenuse: Float,
    base: Float,
    tetherangle: Float
) -> PlatformDetails {
    return getTenstsileIsosceles(
        hypotenuse: hypotenuse,
        base: base,
        tetherangle: tetherangle,
        strap: TENTSILE_STRAPS_DEFAULT,
        circumference: TENTSILE_CIRCUMFERENCE_DEFAULT)
}

private func getTenstsileIsosceles(
    hypotenuse: Float,
    base: Float,
    tetherangle: Float,
    strap: Float,
    circumference: Float
) -> PlatformDetails {
    let measurements = getIsoscelesMeasurements(hypotenuse: hypotenuse, base: base, tetherangle: tetherangle)
    var shapes = [[Coordinate]]()

    let point_x: Float = measurements[0]
    let point_y: Float = 0

    let barb_x: Float = -measurements[1]
    let barb_y: Float = measurements[2]

    let notch_x: Float = -measurements[3]
    let notch_y: Float = 0

    var path = [Coordinate]()
    path.append(Coordinate(x: point_x, y: point_y))
    path.append(Coordinate(x: barb_x, y: barb_y))
    path.append(Coordinate(x: notch_x, y: notch_y))
    path.append(Coordinate(x: barb_x, y: -barb_y))
    shapes.append(path)
    let extremes: [Coordinate] = [path[0], path[1], path[3]]

    return PlatformDetails(
        path: shapes,
        circumference: circumference,
        extremites: extremes,
        rotates: true,
        strap: strap,
        tetherangle: tetherangle)
}

private func getIsoscelesMeasurements(
    hypotenuse: Float,
    base: Float,
    tetherangle: Float
) -> [Float] {
    let largeAngle = (MATH_FULL_CIRCLE - tetherangle) / 2.0
    let sineLargeAngle = sin(largeAngle)
    let centerHeight = sqrt(hypotenuse * hypotenuse - base * base / 4.0)

    // barbSide : distance away from center line to short tip
    let barbSide = base / 2.0
    // barbTether : distance between tether center and short tip
    let barbTether = barbSide / sineLargeAngle
    // barbCenter : distance from tether center to short tip
    let barbCenter = sqrt(barbTether * barbTether - barbSide * barbSide)
    // pointTether : distance between tether center and long tip
    let pointTether = centerHeight - barbCenter

    let betaAngle = asin(pointTether * sineLargeAngle / hypotenuse)
    let gammaAngle = asin(barbSide / hypotenuse)
    let alphaAngle = MATH_QUARTER_CIRCLE - gammaAngle - betaAngle - betaAngle

    let indent = barbSide * tan(alphaAngle)
    // gap : distance between tether center and indentation
    let gap = centerHeight - pointTether - indent

    return [pointTether, barbCenter, barbSide, gap]
}

private func getTentsileTrilogy(
    hypotenuse: Float,
    base: Float,
    tetherangle: Float
) -> PlatformDetails {
    let basePlatform: PlatformDetails = getTenstsileIsosceles(hypotenuse: hypotenuse, base: base, tetherangle: tetherangle)

    let largeAngle = (MATH_FULL_CIRCLE - tetherangle) / 2.0
    let sineLargeAngle = sin(largeAngle)
    let barbSide = base / 2.0
    let barbTether = barbSide / sineLargeAngle
    let barbCenter = sqrt(barbTether * barbTether - barbSide * barbSide)

    let shiftedPlatform = basePlatform.path.translated(by: Coordinate(x: base * MATH_DIVIDE_BY_SQRT_THREE / 2 + barbCenter, y: 0))
    let rotatedPlatformCCW = shiftedPlatform.rotated(by: MATH_TWO_THIRDS_PI)
    let rotatedPlatformCW = shiftedPlatform.rotated(by: -MATH_TWO_THIRDS_PI)

    var allShapes = [[Coordinate]]()
    for shape in shiftedPlatform {
        allShapes.append(shape)
    }
    for shape in rotatedPlatformCCW {
        allShapes.append(shape)
    }
    for shape in rotatedPlatformCW {
        allShapes.append(shape)
    }

    let extremes: [Coordinate] = [shiftedPlatform[0][0], rotatedPlatformCCW[0][0], rotatedPlatformCW[0][0]]

    return PlatformDetails(
        path: allShapes,
        circumference: TENTSILE_CIRCUMFERENCE_DEFAULT,
        extremites: extremes,
        rotates: false,
        strap: TENTSILE_STRAPS_DEFAULT,
        tetherangle: TENTSILE_TETHER_ANGLE_BALANCED)
}
