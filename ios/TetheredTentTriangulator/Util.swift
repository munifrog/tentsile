//
//  Util.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/15/21.
//

import Foundation

private let STRAP_EXTENSION_LENGTH: Float = 6.0
private let MATH_METERS_TO_FEET_CONVERSION: Float = 3.2808399;
// The tether holds an 8 kilogram tent and is under 300 kilograms of force:
private let MATH_INCLINATION_ANGLE: Float = asin(8.0 / 300.0);
private let MATH_INCLINATION_MULTIPLIER: Float = 1.0 / cos(MATH_INCLINATION_ANGLE);
private let MATH_ANGLE_QUARTER_CIRCLE: Float = .pi / 2

enum AnchorIcon {
    case impossible
    case safe
    case tricky
    case warning
}

enum Precision {
    case units
    case tenths
    case hundredths
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
    private static let ANGLE_ONE_HALF_CIRCLE = Float(ANGLE_ONE_FULL_CIRCLE / 2.0)
    private static let ANGLE_ONE_SIXTH_CIRCLE = Float(ANGLE_ONE_FULL_CIRCLE / 6.0)
    private static let ANGLE_ONE_THIRD_CIRCLE = Float(ANGLE_ONE_FULL_CIRCLE / 3.0)
    private static let MATH_FEET_TO_METERS_CONVERSION = Float(0.3048)
    private static let SINE_TWO_PI_DIV_THREE = Float(sqrt(3.0) / 2.0)

    static func getTetherCenter(_ anchors: Anchors, smallAngle: Float) -> TetherCenter? {
        let largeAngle = (ANGLE_ONE_FULL_CIRCLE - smallAngle) / 2.0
        let sineLargeAngle = sin(largeAngle)

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

        if (angleBAC < smallAngle) && (angleCBA < largeAngle) && (angleACB < largeAngle) {
            // The tether center exists, so we can compute lengths from it to the anchors
            let angleTheta = smallAngle - angleBAC
            let anglePBA = atan(lengthCA * sin(angleTheta) / (lengthAB + lengthCA * cos(angleTheta))) // beta1
            let anglePAB = ANGLE_ONE_HALF_CIRCLE - largeAngle - anglePBA // alpha2
            let anglePAC = angleBAC - anglePAB // alpha1

            let lengthPA = lengthAB * sin(anglePBA) / sineLargeAngle // d
            let lengthPB = lengthAB * sin(anglePAB) / sineLargeAngle // e
            let lengthPC = lengthCA * sin(anglePAC) / sineLargeAngle // f

            // Determine the location of the platform center (Q is for quadrant or Y=0 line) relative to the screen
            // These vectors need to be pointing towards A for the next step to work
            let angleQAB = getDirection(h: lengthAB, delta_x:  -diffAB_x, delta_y: -diffAB_y)
            let angleQAC = getDirection(h: lengthCA, delta_x: diffCA_x, delta_y: diffCA_y)

            // This tells us whether the anchors switched from ABC to ACB orientation
            // Angle PCA needs to match angle PCB, which occurs when adding on one side and subtracting on other.
            let anglePAC_a = angleQAC + anglePAC
            let anglePAB_m = angleQAB - anglePAB
            //let anglePAC_m = angleQAC - anglePAC
            let anglePAB_a = angleQAB + anglePAB

            let flipped = getAngleEquivalency(anglePAC_a, anglePAB_m)
            let angleToP = flipped ? anglePAC_a : anglePAB_a

            let p_x = anchors.a.x + lengthPA * cos(angleToP)
            let p_y = anchors.a.y + lengthPA * sin(angleToP)

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

    static func getMeasurementString(
        measure: Float,
        precision: Precision,
        units: Units
    ) -> String {
        // This function is meant for labels only
        if units == .metric {
            if precision == .hundredths {
                return String(format: "%3.2f m", measure)
            } else if precision == .tenths {
                return String(format: "%3.1f m", measure)
            } else { //if precision == Precision.units {
                return String(format: "%3.0f m", measure)
            }
        } else {
            var feet: Float
            var inches: Float
            var eighths: Float
            let fraction: Float

            if precision == .hundredths {
                feet = floor(measure)
                fraction = measure - feet
                // Multiply fraction into 32 equal units (3/8 inch apart). Round to snap to closest.
                // Then multiply by 3 to stretch to a full foot.
                eighths = round(fraction * 32) * 3
                inches = floor(eighths / 8.0)
                eighths -= inches * 8
                if inches == 12 {
                    feet += 1
                    inches = 0
                }
                return String(format: "%1.0f' %1.0f\" %1.0f/8", feet, inches, eighths)
            } else if precision == .tenths {
                feet = floor(measure)
                fraction = measure - feet
                // Multiply fraction into 3 equal units. Round to snap to closest.
                // Then multiply by 4 to stretch to a full foot.
                inches = round(fraction * 3) * 4
                if inches == 12 {
                    feet += 1
                    inches = 0
                }
                return String(format: "%1.0f' %1.0f\"", feet, inches)
            } else { //if precision == Precision.units {
                return String(format: "%1.0f'", round(measure))
            }
        }
    }

    static func getMeasureFromPixels(
        pixels: Float,
        meterScale: Float,
        units: Units
    ) -> Float {
        return pixels * meterScale * (units == .metric ? 1.0 : MATH_METERS_TO_FEET_CONVERSION)
    }

    static func getInclinedMeasureFromPixels(
        pixels: Float,
        meterScale: Float,
        units: Units
    ) -> Float {
        // Convert the level-length to hypotenuse-length
        return MATH_INCLINATION_MULTIPLIER * getMeasureFromPixels(pixels: pixels, meterScale: meterScale, units: units)
    }

    static func getSmallAngleGivenIndent(
        hypotenuse: Float,
        base: Float,
        indent: Float
    ) -> Float {
        let alpha = atan(2 * indent / base)
        let gamma = asin( base / 2 / hypotenuse)
        return Float(MATH_ANGLE_QUARTER_CIRCLE + gamma - alpha)
    }
}
