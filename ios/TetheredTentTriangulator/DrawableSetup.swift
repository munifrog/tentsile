//
//  DrawableTethers.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 2/21/22.
//

import Foundation

private let MATH_METERS_CENTER_TO_ANCHOR_MIN: Float = 0.7

struct DrawableSetup {
    let anchors: Anchors
    let center: TetherCenter?
    let knots: TetherKnots?
    let offset: Coordinate
    let scalePixels: Float
    let scaleMeters: Float

    private var extremes: [Coordinate]
    private let platform: PlatformDetails
    private var rotation: Float
    private var start: Coordinate

    init(
        anchors: Anchors,
        center: TetherCenter?,
        platform: PlatformDetails,
        scale: Float,
        offset: Coordinate
    ) {
        self.anchors = anchors
        self.center = center
        self.offset = offset
        self.platform = platform
        self.scalePixels = scale
        self.scaleMeters = 1 / scale
        self.extremes = []
        self.rotation = 0.0
        self.start = offset

        if let c = center {
            self.start = offset + c.p
            let aAnchor = offset + anchors.a
            let bAnchor = offset + anchors.b
            let cAnchor = offset + anchors.c

            let allowance = scale * MATH_METERS_CENTER_TO_ANCHOR_MIN
            if c.pa > allowance && c.pb > allowance && c.pc > allowance {
                let b_index = c.flips ? 2 : 1
                let c_index = c.flips ? 1 : 2
                let circumference = platform.circumference
                let strap = platform.strap
                let delta = anchors.a - c.p
                let hypotenuse = sqrt(delta.x * delta.x + delta.y * delta.y)
                self.rotation = Util.getDirection(h: hypotenuse, delta_x: delta.x, delta_y: delta.y)
                self.extremes = platform.extremites
                    .rotated(by: rotation)
                    .scaled(by: scale)
                    .translated(by: start)

                let aTether: TetherDetails = Util.getSegmentKnots(
                    start: start,
                    extremity: extremes[0],
                    end: aAnchor,
                    pixelsPerMeter: scale,
                    strap: strap,
                    circumference: circumference
                )
                let bTether: TetherDetails = Util.getSegmentKnots(
                    start: start,
                    extremity: extremes[b_index],
                    end: bAnchor,
                    pixelsPerMeter: scale,
                    strap: strap,
                    circumference: circumference
                )
                let cTether: TetherDetails = Util.getSegmentKnots(
                    start: start,
                    extremity: extremes[c_index],
                    end: cAnchor,
                    pixelsPerMeter: scale,
                    strap: strap,
                    circumference: circumference
                )
                self.knots = TetherKnots(a: aTether, b: bTether, c: cTether)
            } else {
                self.knots = TetherKnots(
                    a: TetherDetails(knots: [start, aAnchor], pixels: -1, icon: AnchorIcon.safe),
                    b: TetherDetails(knots: [start, bAnchor], pixels: -1, icon: AnchorIcon.safe),
                    c: TetherDetails(knots: [start, cAnchor], pixels: -1, icon: AnchorIcon.safe))
            }
        } else {
            self.knots = nil
        }
    }

    func getCanDrawPlatform() -> Bool {
        if let c = self.center {
            let allowance = self.scalePixels * MATH_METERS_CENTER_TO_ANCHOR_MIN
            return c.pa > allowance && c.pb > allowance && c.pc > allowance
        } else {
            return false
        }
    }

    func getPlatformPath() -> [[Coordinate]] {
        return platform.path
            .rotated(by: rotation)
            .scaled(by: scalePixels)
            .translated(by: start)
    }
}
