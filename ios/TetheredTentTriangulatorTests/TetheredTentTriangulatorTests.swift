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
    let MATH_SQUARE_ROOT_OF_THREE: Float = sqrt(3)
    let MATH_SQUARE_ROOT_OF_SEVEN: Float = sqrt(7)
    let MATH_SQUARE_ROOT_OF_THIRTEEN: Float = sqrt(13)
    let MATH_SQUARE_ROOT_OF_NINETEEN: Float = sqrt(19)

    let ALLOWANCE_DELTA_ONE: Float = 0.1;
    let ALLOWANCE_DELTA_TWO: Float = 0.01;
    let ALLOWANCE_DELTA_TWO_FIVE: Float = 0.015;
    let ALLOWANCE_DELTA_THREE: Float = 0.001;

    let CENTER_X: Float = 100
    let CENTER_Y: Float = 300

    let util = Util()

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
        let tetherCenter: TetherCenter? = util.getTetherCenter(anchors)
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
        let tetherCenter: TetherCenter? = util.getTetherCenter(anchors)
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
        let tetherCenter: TetherCenter? = util.getTetherCenter(anchors)
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
        let tetherCenter: TetherCenter? = util.getTetherCenter(anchors)
        XCTAssertTrue(tetherCenter == nil)
    }
}
