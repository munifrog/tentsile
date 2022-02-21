//
//  Util.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/15/21.
//

import Foundation

private let STRAP_EXTENSION_LENGTH: Float = 6.0

enum AnchorIcon {
    case impossible
    case safe
    case tricky
    case warning
}

struct TetherDetails {
    let icon: AnchorIcon
    let knots: [Coordinate]
    let pixels: Float

    init(knots: [Coordinate], pixels: Float, icon: AnchorIcon) {
        self.icon = icon
        self.knots = knots
        self.pixels = pixels
    }
}

class Util {
    private static let ANGLE_ALLOWANCE = Float(0.001)
    private static let ANGLE_ONE_FULL_CIRCLE = Float(2.0 * .pi)
    private static let ANGLE_ONE_SIXTH_CIRCLE = Float(ANGLE_ONE_FULL_CIRCLE / 6.0)
    private static let ANGLE_ONE_THIRD_CIRCLE = Float(ANGLE_ONE_FULL_CIRCLE / 3.0)
    private static let MATH_FEET_TO_METERS_CONVERSION = Float(0.3048)
    private static let SINE_TWO_PI_DIV_THREE = Float(sqrt(3.0) / 2.0)

    static func getTetherCenter(_ anchors: Anchors) -> TetherCenter? {
        // Compute where the tether center will exist
        let diffAB_x = anchors.a.x - anchors.b.x
        let diffAB_y = anchors.a.y - anchors.b.y
        let diffBC_x = anchors.b.x - anchors.c.x
        let diffBC_y = anchors.b.y - anchors.c.y
        let diffCA_x = anchors.c.x - anchors.a.x
        let diffCA_y = anchors.c.y - anchors.a.y

        let sqLengthAB = diffAB_x * diffAB_x + diffAB_y * diffAB_y
        let sqLengthBC = diffBC_x * diffBC_x + diffBC_y * diffBC_y
        let sqLengthCA = diffCA_x * diffCA_x + diffCA_y * diffCA_y

        let lengthAB = sqrt(sqLengthAB) // c
        let lengthBC = sqrt(sqLengthBC) // a
        let lengthCA = sqrt(sqLengthCA) // b

        // Use Law of Cosines to determine the angles
        let angleBAC = acos((sqLengthCA + sqLengthAB - sqLengthBC) / 2.0 / lengthCA / lengthAB) // A
        let angleCBA = acos((sqLengthAB + sqLengthBC - sqLengthCA) / 2.0 / lengthAB / lengthBC) // B
        let angleACB = acos((sqLengthBC + sqLengthCA - sqLengthAB) / 2.0 / lengthBC / lengthCA) // C

        if angleBAC < ANGLE_ONE_THIRD_CIRCLE && angleCBA < ANGLE_ONE_THIRD_CIRCLE && angleACB < ANGLE_ONE_THIRD_CIRCLE {
            // The tether center exists, so we can compute lengths from it to the anchors
            let angleTheta = ANGLE_ONE_THIRD_CIRCLE - angleACB
            let anglePBC = atan(lengthCA * sin(angleTheta) / (lengthBC + lengthCA * cos(angleTheta)))
            let anglePCB = ANGLE_ONE_SIXTH_CIRCLE - anglePBC
            let anglePCA = angleACB - anglePCB

            //let lengthPA = lengthCA * sin(anglePCA) / SINE_TWO_PI_DIV_THREE
            //let lengthPB = lengthAB * sin(anglePCB) / SINE_TWO_PI_DIV_THREE
            let lengthPC = lengthBC * sin(anglePBC) / SINE_TWO_PI_DIV_THREE

            // Determine the location of the platform center (Q is for quadrant or Y=0 line)
            let angleQCB = getDirection(h: lengthBC, delta_x:  diffBC_x, delta_y:  diffBC_y)
            let angleQCA = getDirection(h: lengthCA, delta_x: -diffCA_x, delta_y: -diffCA_y)

            // This tells us whether the anchors switched from ABC to ACB orientation
            // Angle PCA needs to match angle PCB, which occurs when adding on one side and subtracting on other.
            let anglePCA_option1 = angleQCA + anglePCA
            let anglePCA_option2 = angleQCA - anglePCA
            let anglePCB_option1 = angleQCB + anglePCB
            //let anglePCB_option2 = angleQCB - anglePCB

            let flipped = getAngleEquivalency(anglePCA_option2, anglePCB_option1)
            let angleToP = flipped ? anglePCB_option1 : anglePCA_option1

            let p_x = anchors.c.x + lengthPC * cos(angleToP)
            let p_y = anchors.c.y + lengthPC * sin(angleToP)

            let diffPA_x = p_x - anchors.a.x
            let diffPA_y = p_y - anchors.a.y
            let lengthPA = sqrt(diffPA_x * diffPA_x + diffPA_y * diffPA_y)
            let diffPB_x = p_x - anchors.b.x
            let diffPB_y = p_y - anchors.b.y
            let lengthPB = sqrt(diffPB_x * diffPB_x + diffPB_y * diffPB_y)
            //let diffPC_x = p_x - anchors.c.x
            //let diffPC_y = p_y - anchors.c.y
            //let lengthPC = sqrt(diffPC_x * diffPC_x + diffPC_y * diffPC_y)

            return TetherCenter(
                p: Coordinate(x: p_x, y: p_y),
                pa: lengthPA,
                pb: lengthPB,
                pc: lengthPC,
                flips: flipped
            )
        } else {
            // If angles are too great, then the center cannot exist
            return nil
        }
    }

    // Arcsine ranges from -pi/2 (Quadrant 4) to +pi/2 (Quadrant 1)
    // When delta_x is positive then we are in Quadrant 1 or 4, corresponding to the arcsine results;
    // When delta_x is negative then we are in Quadrant 2 or 3.
    // The same delta_y at a negative delta_x, occurs exactly pi away from the arcsine results;
    static func getDirection(h: Float, delta_x: Float, delta_y: Float) -> Float {
        var angle = asin(delta_y / h)
        if (delta_x < 0) {
            angle = .pi - angle
        }
        return angle
    }

    static func getAngleEquivalency(_ a: Float, _ b: Float) -> Bool {
        let full_circle: Float = 2 * .pi
        let rawDelta = abs(b - a)
        if rawDelta < ANGLE_ALLOWANCE {
            return true
        } else {
            let numCircles: Float = round(rawDelta / full_circle)
            let modDelta = abs(rawDelta - numCircles * full_circle)
            return modDelta < ANGLE_ALLOWANCE
        }
    }

    static func getSegmentKnots(
        start: Coordinate,
        extremity: Coordinate,
        end: Coordinate,
        pixelsPerMeter: Float,
        strap: Float,
        circumference: Float
    ) -> TetherDetails {
        var anchorIcon = AnchorIcon.safe
        var knots: [Coordinate] = [Coordinate]()
        knots.append(start)
        knots.append(extremity)

        // Between the platform extremity and anchor point, determine where the colors transition:
        //   A. Segment connects platform center and platform extremity.
        //   B. One imperial foot length connects ratchet to platform.
        //   C. The provided strap (either 6m or 4m) defines what strap length the user will be guaranteed to own.
        //   D. Any further distance will be split into lengths equal to extension straps (which are all 6m)
        //   E. Last segment consists of the length remaining, (less than 6m) after the last extension
        let pixelAnchorVector: Coordinate = end - start
        let pixelAnchorAmount: Float = sqrt(pixelAnchorVector.x * pixelAnchorVector.x + pixelAnchorVector.y * pixelAnchorVector.y)

        let pixelTetherVector = end - extremity
        var pixelTetherAmount: Float = sqrt(pixelTetherVector.x * pixelTetherVector.x + pixelTetherVector.y * pixelTetherVector.y)

        let angle: Float = getDirection(h: pixelAnchorAmount, delta_x: pixelAnchorVector.x, delta_y: pixelAnchorVector.y)
        let sine = sin(angle)
        let cosine = cos(angle)

        let pixelExtremityVector: Coordinate = extremity - start
        let pixelExtremityAmount: Float = sqrt(pixelExtremityVector.x * pixelExtremityVector.x + pixelExtremityVector.y * pixelExtremityVector.y)

        let ratchetDistance = pixelExtremityAmount + (pixelsPerMeter * MATH_FEET_TO_METERS_CONVERSION)
        let pixelStrapAllowance = pixelsPerMeter * (circumference)

        if pixelAnchorAmount < pixelExtremityAmount {
            anchorIcon = AnchorIcon.impossible
            pixelTetherAmount *= -1
        } else if pixelAnchorAmount <= ratchetDistance {
            anchorIcon = AnchorIcon.tricky
        } else { // pixelAnchorAmount > ratchetDistance
            var pixelNextKnot = ratchetDistance
            knots.append(Coordinate(x: start.x + pixelNextKnot * cosine, y: start.y + pixelNextKnot * sine))
            // Provided straps
            pixelNextKnot += pixelsPerMeter * strap
            if pixelAnchorAmount + pixelStrapAllowance > pixelNextKnot {
                anchorIcon = AnchorIcon.warning
            }
            // Extension straps
            while pixelNextKnot < pixelAnchorAmount {
                anchorIcon = AnchorIcon.safe
                knots.append(Coordinate(x: start.x + pixelNextKnot * cosine, y: start.y + pixelNextKnot * sine))
                pixelNextKnot += pixelsPerMeter * STRAP_EXTENSION_LENGTH
                if pixelAnchorAmount + pixelStrapAllowance > pixelNextKnot {
                    anchorIcon = AnchorIcon.warning
                }
            }
        }
        knots.append(end)
        return TetherDetails(knots: knots, pixels: pixelTetherAmount, icon: anchorIcon)
    }
}
