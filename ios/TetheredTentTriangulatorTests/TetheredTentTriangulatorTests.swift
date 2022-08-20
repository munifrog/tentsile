//
//  TetheredTentTriangulatorTests.swift
//  TetheredTentTriangulatorTests
//
//  Created by Karl Arthur on 9/5/21.
//

import XCTest
@testable import TetheredTentTriangulator

class TetheredTentTriangulatorTests: XCTestCase {

    let MATH_BASE_LENGTH_N: Float = 200
    let MATH_FEET_TO_METERS_CONVERSION: Float = 0.3048
    let MATH_SQUARE_ROOT_OF_THREE: Float = sqrt(3)
    let MATH_SQUARE_ROOT_OF_SEVEN: Float = sqrt(7)
    let MATH_SQUARE_ROOT_OF_THIRTEEN: Float = sqrt(13)
    let MATH_SQUARE_ROOT_OF_NINETEEN: Float = sqrt(19)
    let MATH_TREE_CIRCUMFERENCE: Float = 0.785398163397448 // pi * 25cm or 10inch diameter
    let MATH_ANGLE_ONE_THIRD_CIRCLE: Float = .pi * 2 / 3

    let ALLOWANCE_DELTA_ONE: Float = 0.1;
    let ALLOWANCE_DELTA_TWO: Float = 0.01;
    let ALLOWANCE_DELTA_TWO_FIVE: Float = 0.015;
    let ALLOWANCE_DELTA_THREE: Float = 0.001;

    let CENTER_X: Float = 100
    let CENTER_Y: Float = 300

    func setupEquilateralAnchors(center: Coordinate) -> Anchors {
        let a = Coordinate (
            x: center.x,
            y: center.y + MATH_BASE_LENGTH_N
        )
        let b = Coordinate (
            x: center.x - MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N / 2,
            y: center.y - MATH_BASE_LENGTH_N / 2
        )
        let c = Coordinate (
            x: center.x + MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N / 2,
            y: center.y - MATH_BASE_LENGTH_N / 2
        )
        return Anchors(a: a, b: b, c: c)
    }

    func setupIsoscelesAnchors(center: Coordinate) -> Anchors {
        let a = Coordinate (
            x: center.x - MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_THREE,
            y: center.y + MATH_BASE_LENGTH_N
        )
        let b = Coordinate (
            x: center.x + MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_THREE,
            y: center.y + MATH_BASE_LENGTH_N
        )
        let c = Coordinate (
            x: center.x,
            y: center.y - MATH_BASE_LENGTH_N * 3
        )
        return Anchors(a: a, b: b, c: c)
    }

    func setupScaleneAnchors(center: Coordinate) -> Anchors {
        // Length N
        let a = Coordinate (
            x: center.x - MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N / 2,
            y: center.y + MATH_BASE_LENGTH_N / 2
        )
        // Length 2N
        let b = Coordinate (
            x: center.x + MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N,
            y: center.y + MATH_BASE_LENGTH_N
        )
        // Length 3N
        let c = Coordinate (
            x: center.x,
            y: center.y - 3 * MATH_BASE_LENGTH_N
        )
        return Anchors(a: a, b: b, c: c)
    }

    func setupNoCenterAnchors(center: Coordinate) -> Anchors {
        // If the A anchor were at the center it would barely resolve to a tether center
        let a = Coordinate (
            x: center.x,
            y: center.y - 1
        )
        let b = Coordinate (
            x: center.x - MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N / 2,
            y: center.y - MATH_BASE_LENGTH_N / 2
        )
        let c = Coordinate (
            x: center.x + MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N / 2,
            y: center.y - MATH_BASE_LENGTH_N / 2
        )
        return Anchors(a: a, b: b, c: c)
    }

    func testEquilateralTetherCenter() throws {
        let solution = Coordinate(x: CENTER_X, y: CENTER_Y)
        let anchors = setupEquilateralAnchors(center: solution)
        let tetherCenter: TetherCenter? = Util.getTetherCenter(anchors, smallAngle: MATH_ANGLE_ONE_THIRD_CIRCLE)
        if let center = tetherCenter {
            XCTAssertEqual(solution.x, center.p.x, accuracy: ALLOWANCE_DELTA_THREE)
            XCTAssertEqual(solution.y, center.p.y, accuracy: ALLOWANCE_DELTA_THREE)
        } else {
            XCTAssertTrue(false)
        }
    }

    func testIsoscelesTetherCenter() throws {
        let solution = Coordinate(x: CENTER_X, y: CENTER_Y)
        let anchors = setupIsoscelesAnchors(center: solution)
        let tetherCenter: TetherCenter? = Util.getTetherCenter(anchors, smallAngle: MATH_ANGLE_ONE_THIRD_CIRCLE)
        if let center = tetherCenter {
            XCTAssertEqual(solution.x, center.p.x, accuracy: ALLOWANCE_DELTA_THREE)
            XCTAssertEqual(solution.y, center.p.y, accuracy: ALLOWANCE_DELTA_THREE)
        } else {
            XCTAssertTrue(false)
        }
    }

    func testScaleneTetherCenter() throws {
        let solution = Coordinate(x: CENTER_X, y: CENTER_Y)
        let anchors = setupScaleneAnchors(center: solution)
        let tetherCenter: TetherCenter? = Util.getTetherCenter(anchors, smallAngle: MATH_ANGLE_ONE_THIRD_CIRCLE)
        if let center = tetherCenter {
            XCTAssertEqual(solution.x, center.p.x, accuracy: ALLOWANCE_DELTA_THREE)
            XCTAssertEqual(solution.y, center.p.y, accuracy: ALLOWANCE_DELTA_THREE)
        } else {
            XCTAssertTrue(false)
        }
    }

    func testNoTetherCenter() throws {
        let solution = Coordinate(x: CENTER_X, y: CENTER_Y)
        let anchors = setupNoCenterAnchors(center: solution)
        let tetherCenter: TetherCenter? = Util.getTetherCenter(anchors, smallAngle: MATH_ANGLE_ONE_THIRD_CIRCLE)
        XCTAssertTrue(tetherCenter == nil)
    }

    func testAngleEquivalency() throws {
        let fullCircle: Float = 2 * .pi
        let twoCircles: Float = 4 * .pi

        let iterations: Int = 360
        let angleDiff: Float = fullCircle / Float(iterations)

        var angle: Float
        for number in 0...iterations {
            angle = Float(number) * angleDiff
            XCTAssertTrue(Util.getAngleEquivalency(angle, angle - fullCircle))
            XCTAssertTrue(Util.getAngleEquivalency(angle, angle - twoCircles))
            XCTAssertTrue(Util.getAngleEquivalency(angle, angle + fullCircle))
            XCTAssertTrue(Util.getAngleEquivalency(angle, angle + twoCircles))
        }
    }

    func testGetDirection() throws {
        let fullCircle: Float = 2 * .pi

        let iterations: Int = 360
        let angleDiff: Float = fullCircle / Float(iterations)
        let hypotenuse: Float = 1

        var angle: Float
        var negAngle: Float
        var deltaX: Float
        var deltaY: Float
        var derivedAngle: Float

        for number in 0...iterations {
            // Positive angles
            angle = Float(number) * angleDiff
            deltaX = hypotenuse * cos(angle)
            deltaY = hypotenuse * sin(angle)
            derivedAngle = Util.getDirection(h: hypotenuse, delta_x: deltaX, delta_y: deltaY)
            XCTAssertTrue(Util.getAngleEquivalency(angle, derivedAngle))

            // Negative angles
            negAngle = angle - fullCircle
            deltaX = hypotenuse * cos(negAngle)
            deltaY = hypotenuse * sin(negAngle)
            derivedAngle = Util.getDirection(h: hypotenuse, delta_x: deltaX, delta_y: deltaY)
            XCTAssertTrue(Util.getAngleEquivalency(angle, derivedAngle))
        }
    }

    func testGetSegmentKnots() throws {
        // From Start (S) to end, there can be extremity (E), ratchet (R), Circumference (C), and Knot (K)
        // S ---- ---- ---- ---- E -- R ---- ---- ---- ---- ---- C1 ---- K1 ---- ---- ---- ---- ---- C2 ---- K2 ...
        // These are the transition points, about which we need to check that results match expectations
        let circumferenceMeters: Float = MATH_TREE_CIRCUMFERENCE
        let extremityMeters: Float = 3.66 // "Connect" point
        let offsetMeters: Float = MATH_FEET_TO_METERS_CONVERSION / 3
        let pixelsPerMeter: Float = 35
        let ratchetMeters: Float = MATH_FEET_TO_METERS_CONVERSION
        let strapProvidedMeters: Float = 6.0
        let strapExtensionMeters: Float = 6.0

        let angle: Float = 37 * .pi / 180 // unnecessary angle adding interesting results
        let cosinePixelsPerMeter = pixelsPerMeter * cos(angle)
        let sinePixelsPerMeter = pixelsPerMeter * sin(angle)

        let startCoord = Coordinate(x: CENTER_X, y: CENTER_Y)
        let extremityCoord = Coordinate(
            x: startCoord.x + extremityMeters * cosinePixelsPerMeter,
            y: startCoord.y + extremityMeters * sinePixelsPerMeter
        )

        // Before EXTREMITY : Too short (impossible) : START-END-EXTREMITY
        var pivotMeters = extremityMeters
        var endMeters = pivotMeters - offsetMeters
        var endCoord = Coordinate(
            x: startCoord.x + endMeters * cosinePixelsPerMeter,
            y: startCoord.y + endMeters * sinePixelsPerMeter
        )
        var details: TetherDetails = Util.getSegmentKnots(
            start: startCoord,
            extremity: extremityCoord,
            end: endCoord,
            pixelsPerMeter: pixelsPerMeter,
            strap: strapProvidedMeters,
            circumference: circumferenceMeters
        )
        XCTAssertTrue(details.icon == AnchorIcon.impossible)

        // After EXTREMITY : Rachet area (tricky) : EXTREMITY-END-RATCHET
        endMeters = pivotMeters + offsetMeters
        endCoord = Coordinate(
            x: startCoord.x + endMeters * cosinePixelsPerMeter,
            y: startCoord.y + endMeters * sinePixelsPerMeter
        )
        details = Util.getSegmentKnots(
            start: startCoord,
            extremity: extremityCoord,
            end: endCoord,
            pixelsPerMeter: pixelsPerMeter,
            strap: strapProvidedMeters,
            circumference: circumferenceMeters
        )
        XCTAssertTrue(details.icon == AnchorIcon.tricky)

        // Before RATCHET : Rachet area (tricky) : EXTREMITY-END-RATCHET
        pivotMeters = extremityMeters + ratchetMeters
        endMeters = pivotMeters - offsetMeters
        endCoord = Coordinate(
            x: startCoord.x + endMeters * cosinePixelsPerMeter,
            y: startCoord.y + endMeters * sinePixelsPerMeter
        )
        details = Util.getSegmentKnots(
            start: startCoord,
            extremity: extremityCoord,
            end: endCoord,
            pixelsPerMeter: pixelsPerMeter,
            strap: strapProvidedMeters,
            circumference: circumferenceMeters
        )
        XCTAssertTrue(details.icon == AnchorIcon.tricky)

        // After RATCHET : Provided strap (safe) : RATCHET-END-CIRCUMFERENCE1
        endMeters = pivotMeters + offsetMeters
        endCoord = Coordinate(
            x: startCoord.x + endMeters * cosinePixelsPerMeter,
            y: startCoord.y + endMeters * sinePixelsPerMeter
        )
        details = Util.getSegmentKnots(
            start: startCoord,
            extremity: extremityCoord,
            end: endCoord,
            pixelsPerMeter: pixelsPerMeter,
            strap: strapProvidedMeters,
            circumference: circumferenceMeters
        )
        XCTAssertTrue(details.icon == AnchorIcon.safe)

        // Before CIRCUMFERENCE : Provided strap (safe) : RATCHET-END-CIRCUMFERENCE1
        pivotMeters = extremityMeters + ratchetMeters + strapProvidedMeters - circumferenceMeters
        endMeters = pivotMeters - offsetMeters
        endCoord = Coordinate(
            x: startCoord.x + endMeters * cosinePixelsPerMeter,
            y: startCoord.y + endMeters * sinePixelsPerMeter
        )
        details = Util.getSegmentKnots(
            start: startCoord,
            extremity: extremityCoord,
            end: endCoord,
            pixelsPerMeter: pixelsPerMeter,
            strap: strapProvidedMeters,
            circumference: circumferenceMeters
        )
        XCTAssertTrue(details.icon == AnchorIcon.safe)

        // After CIRCUMFERENCE : During tree wrap (warning) : CIRCUMFERENCE1-END-KNOT1
        endMeters = pivotMeters + offsetMeters
        endCoord = Coordinate(
            x: startCoord.x + endMeters * cosinePixelsPerMeter,
            y: startCoord.y + endMeters * sinePixelsPerMeter
        )
        details = Util.getSegmentKnots(
            start: startCoord,
            extremity: extremityCoord,
            end: endCoord,
            pixelsPerMeter: pixelsPerMeter,
            strap: strapProvidedMeters,
            circumference: circumferenceMeters
        )
        XCTAssertTrue(details.icon == AnchorIcon.warning)

        // Before KNOT : During tree wrap (warning) : CIRCUMFERENCE1-END-KNOT1
        pivotMeters = extremityMeters + ratchetMeters + strapProvidedMeters
        endMeters = pivotMeters - offsetMeters
        endCoord = Coordinate(
            x: startCoord.x + endMeters * cosinePixelsPerMeter,
            y: startCoord.y + endMeters * sinePixelsPerMeter
        )
        details = Util.getSegmentKnots(
            start: startCoord,
            extremity: extremityCoord,
            end: endCoord,
            pixelsPerMeter: pixelsPerMeter,
            strap: strapProvidedMeters,
            circumference: circumferenceMeters
        )
        XCTAssertTrue(details.icon == AnchorIcon.warning)

        // After KNOT : First extension (safe) : KNOT1-END-CIRCUMFERENCE2
        endMeters = pivotMeters + offsetMeters
        endCoord = Coordinate(
            x: startCoord.x + endMeters * cosinePixelsPerMeter,
            y: startCoord.y + endMeters * sinePixelsPerMeter
        )
        details = Util.getSegmentKnots(
            start: startCoord,
            extremity: extremityCoord,
            end: endCoord,
            pixelsPerMeter: pixelsPerMeter,
            strap: strapProvidedMeters,
            circumference: circumferenceMeters
        )
        XCTAssertTrue(details.icon == AnchorIcon.safe)

        // Before CIRCUMFERENCE : First extension (safe) : KNOT1-END-CIRCUMFERENCE2
        pivotMeters = extremityMeters + ratchetMeters + strapProvidedMeters + strapExtensionMeters - circumferenceMeters
        endMeters = pivotMeters - offsetMeters
        endCoord = Coordinate(
            x: startCoord.x + endMeters * cosinePixelsPerMeter,
            y: startCoord.y + endMeters * sinePixelsPerMeter
        )
        details = Util.getSegmentKnots(
            start: startCoord,
            extremity: extremityCoord,
            end: endCoord,
            pixelsPerMeter: pixelsPerMeter,
            strap: strapProvidedMeters,
            circumference: circumferenceMeters
        )
        XCTAssertTrue(details.icon == AnchorIcon.safe)

        // After CIRCUMFERENCE : During tree wrap (warning) : CIRCUMFERENCE2-END-KNOT2
        pivotMeters = extremityMeters + ratchetMeters + strapProvidedMeters + strapExtensionMeters - circumferenceMeters
        endMeters = pivotMeters + offsetMeters
        endCoord = Coordinate(
            x: startCoord.x + endMeters * cosinePixelsPerMeter,
            y: startCoord.y + endMeters * sinePixelsPerMeter
        )
        details = Util.getSegmentKnots(
            start: startCoord,
            extremity: extremityCoord,
            end: endCoord,
            pixelsPerMeter: pixelsPerMeter,
            strap: strapProvidedMeters,
            circumference: circumferenceMeters
        )
        XCTAssertTrue(details.icon == AnchorIcon.warning)
    }

    func testGetMeasurementString() throws {
        let inchDecimal: Float = 1.0 / 12.0
        let eighthInchDecimal: Float = inchDecimal / 8.0
        let offset: Float = inchDecimal / 64.0 // 1/64 of an inch; Before and after offset that should round to the target

        var expectations: [[Float]]
        var expectedString: String
        var actualString: String

        expectations =
        [
            // Use different feet so it is obvious which one fails
            //   [0] target            // [1] -feet // [2] +feet
            [  (1.0 +  1 * inchDecimal),   1,           1 ],
            [  (2.0 +  2 * inchDecimal),   2,           2 ],
            [  (3.0 +  3 * inchDecimal),   3,           3 ],
            [  (4.0 +  4 * inchDecimal),   4,           4 ],
            [  (5.0 +  5 * inchDecimal),   5,           5 ],
            [  (6.0 +  6 * inchDecimal),   6,           7 ],
            [  (7.0 +  7 * inchDecimal),   8,           8 ],
            [  (8.0 +  8 * inchDecimal),   9,           9 ],
            [  (9.0 +  9 * inchDecimal),  10,          10 ],
            [ (10.0 + 10 * inchDecimal),  11,          11 ],
            [ (11.0 + 11 * inchDecimal),  12,          12 ],
            [ (12.0 + 12 * inchDecimal),  13,          13 ],
        ]
        for row in expectations {
            actualString = Util.getMeasurementString(measure: (row[0] - offset), precision: Precision.units, units: Units.imperial)
            expectedString = String(format: "%1.0f'", row[1])
            XCTAssertEqual(actualString, expectedString)
            actualString = Util.getMeasurementString(measure: (row[0] + offset), precision: Precision.units, units: Units.imperial)
            expectedString = String(format: "%1.0f'", row[2])
            XCTAssertEqual(actualString, expectedString)
        }

        expectations =
        [
            // Use different feet so it can be obvious which one fails
            // [0] target             // [1] -feet // [2] -inches // [3] +feet // [4] +inches
            [ ( 1.0 +  1 * inchDecimal),  1,           0,             1,           0 ],
            [ ( 2.0 +  2 * inchDecimal),  2,           0,             2,           4 ],
            [ ( 3.0 +  3 * inchDecimal),  3,           4,             3,           4 ],
            [ ( 4.0 +  4 * inchDecimal),  4,           4,             4,           4 ],
            [ ( 5.0 +  5 * inchDecimal),  5,           4,             5,           4 ],
            [ ( 6.0 +  6 * inchDecimal),  6,           4,             6,           8 ],
            [ ( 7.0 +  7 * inchDecimal),  7,           8,             7,           8 ],
            [ ( 8.0 +  8 * inchDecimal),  8,           8,             8,           8 ],
            [ ( 9.0 +  9 * inchDecimal),  9,           8,             9,           8 ],
            [ (10.0 + 10 * inchDecimal), 10,           8,            11,           0 ],
            [ (11.0 + 11 * inchDecimal), 12,           0,            12,           0 ],
            [ (12.0 + 12 * inchDecimal), 13,           0,            13,           0 ],
        ]
        for row in expectations {
            actualString = Util.getMeasurementString(measure: (row[0] - offset), precision: Precision.tenths, units: Units.imperial)
            expectedString = String(format: "%1.0f' %1.0f\"", row[1], row[2])
            XCTAssertEqual(actualString, expectedString)
            actualString = Util.getMeasurementString(measure: (row[0] + offset), precision: Precision.tenths, units: Units.imperial)
            expectedString = String(format: "%1.0f' %1.0f\"", row[3], row[4])
            XCTAssertEqual(actualString, expectedString)
        }

        expectations =
        [
            // Use different feet so it can be obvious which one fails
            // |..;..;.|;..;..;|.;..;..|
            // [0] target                       // [1] -feet // [2] -inches // [3] -eighths // [4] +feet // [5] +inches // [6] +eighths
            [  (1.0 +    0 * eighthInchDecimal),    1,           0,             0,              1,           0,             0 ],
            [  (1.0 +    1 * eighthInchDecimal),    1,           0,             0,              1,           0,             0 ],
            [  (2.0 +  1.5 * eighthInchDecimal),    2,           0,             0,              2,           0,             3 ],
            [  (3.0 +    2 * eighthInchDecimal),    3,           0,             3,              3,           0,             3 ],
            [  (4.0 +    3 * eighthInchDecimal),    4,           0,             3,              4,           0,             3 ],
            [  (5.0 +    4 * eighthInchDecimal),    5,           0,             3,              5,           0,             3 ],
            [  (6.0 +  4.5 * eighthInchDecimal),    6,           0,             3,              6,           0,             6 ],
            [  (7.0 +    5 * eighthInchDecimal),    7,           0,             6,              7,           0,             6 ],
            [  (8.0 +    6 * eighthInchDecimal),    8,           0,             6,              8,           0,             6 ],
            [  (9.0 +    7 * eighthInchDecimal),    9,           0,             6,              9,           0,             6 ],
            [ (10.0 +  7.5 * eighthInchDecimal),   10,           0,             6,             10,           1,             1 ],
            [ (11.0 +    8 * eighthInchDecimal),   11,           1,             1,             11,           1,             1 ],
            [ (12.0 +    9 * eighthInchDecimal),   12,           1,             1,             12,           1,             1 ],
            [ (13.0 +   10 * eighthInchDecimal),   13,           1,             1,             13,           1,             1 ],
            [ (14.0 + 10.5 * eighthInchDecimal),   14,           1,             1,             14,           1,             4 ],
            [ (15.0 +   11 * eighthInchDecimal),   15,           1,             4,             15,           1,             4 ],
            [ (16.0 +   12 * eighthInchDecimal),   16,           1,             4,             16,           1,             4 ],
            [ (17.0 +   13 * eighthInchDecimal),   17,           1,             4,             17,           1,             4 ],
            [ (18.0 + 13.5 * eighthInchDecimal),   18,           1,             4,             18,           1,             7 ],
            [ (19.0 +   14 * eighthInchDecimal),   19,           1,             7,             19,           1,             7 ],
            [ (20.0 +   15 * eighthInchDecimal),   20,           1,             7,             20,           1,             7 ],
            [ (21.0 +   16 * eighthInchDecimal),   21,           1,             7,             21,           1,             7 ],
            [ (22.0 + 16.5 * eighthInchDecimal),   22,           1,             7,             22,           2,             2 ],
            [ (23.0 +   17 * eighthInchDecimal),   23,           2,             2,             23,           2,             2 ],
            [ (24.0 +   18 * eighthInchDecimal),   24,           2,             2,             24,           2,             2 ],
            [ (25.0 +   19 * eighthInchDecimal),   25,           2,             2,             25,           2,             2 ],
            [ (26.0 + 19.5 * eighthInchDecimal),   26,           2,             2,             26,           2,             5 ],
            [ (27.0 +   20 * eighthInchDecimal),   27,           2,             5,             27,           2,             5 ],
            [ (28.0 +   21 * eighthInchDecimal),   28,           2,             5,             28,           2,             5 ],
            [ (29.0 +   22 * eighthInchDecimal),   29,           2,             5,             29,           2,             5 ],
            [ (30.0 + 22.5 * eighthInchDecimal),   30,           2,             5,             30,           3,             0 ],
            [ (31.0 +   23 * eighthInchDecimal),   31,           3,             0,             31,           3,             0 ],
            [ (32.0 +   24 * eighthInchDecimal),   32,           3,             0,             32,           3,             0 ],
        ]
        for row in expectations {
            actualString = Util.getMeasurementString(measure: (row[0] - offset), precision: Precision.hundredths, units: Units.imperial)
            expectedString = String(format: "%1.0f' %1.0f\" %1.0f/8", row[1], row[2], row[3])
            XCTAssertEqual(actualString, expectedString)
            actualString = Util.getMeasurementString(measure: (row[0] + offset), precision: Precision.hundredths, units: Units.imperial)
            expectedString = String(format: "%1.0f' %1.0f\" %1.0f/8", row[4], row[5], row[6])
            XCTAssertEqual(actualString, expectedString)
        }
    }
}
