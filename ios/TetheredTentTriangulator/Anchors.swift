//
//  Anchors.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/11/21.
//

import Foundation

private let USER_DEFAULTS_STORED_ANCHOR_A = "com.munifrog.tethered.tent.triangulator.config.anchor.first"
private let USER_DEFAULTS_STORED_ANCHOR_B = "com.munifrog.tethered.tent.triangulator.config.anchor.second"
private let USER_DEFAULTS_STORED_ANCHOR_C = "com.munifrog.tethered.tent.triangulator.config.anchor.third"

struct Anchors {
    var a: Coordinate {
        didSet {
            storeA()
        }
    }
    var b: Coordinate {
        didSet {
            storeB()
        }
    }
    var c: Coordinate {
        didSet {
            storeC()
        }
    }
    var ab: Float {
        let ab_x = a.x - b.x
        let ab_y = a.y - b.y
        return sqrt(ab_x * ab_x + ab_y * ab_y)
    }
    var bc: Float {
        let bc_x = b.x - c.x
        let bc_y = b.y - c.y
        return sqrt(bc_x * bc_x + bc_y * bc_y)
    }
    var ca: Float {
        let ca_x = c.x - a.x
        let ca_y = c.y - a.y
        return sqrt(ca_x * ca_x + ca_y * ca_y)
    }

    var ab_label: Coordinate {
        return Coordinate(
            x: (a.x + b.x) / 2,
            y: (a.y + b.y) / 2
        )
    }
    var bc_label: Coordinate {
        return Coordinate(
            x: (b.x + c.x) / 2,
            y: (b.y + c.y) / 2
        )
    }
    var ca_label: Coordinate {
        return Coordinate(
            x: (c.x + a.x) / 2,
            y: (c.y + a.y) / 2
        )
    }

    init(anchors: Anchors) {
        self.a = anchors.a
        self.b = anchors.b
        self.c = anchors.c
    }

    init(a: Coordinate, b: Coordinate, c: Coordinate) {
        self.a = a
        self.b = b
        self.c = c
    }

    init() {
        let refLength: Double = 125.0
        let offset = 15 * Double.pi / 180
        var angle: Double

        if let storedAnchorA = UserDefaults.standard.object(forKey: USER_DEFAULTS_STORED_ANCHOR_A) as? [Float] {
            self.a = Coordinate(
                x: storedAnchorA[0],
                y: storedAnchorA[1]
            )
        } else {
            angle = offset
            self.a = Coordinate(
                x: (refLength * cos(angle)),
                y: (refLength * sin(angle))
            )
        }

        if let storedAnchorB = UserDefaults.standard.object(forKey: USER_DEFAULTS_STORED_ANCHOR_B) as? [Float] {
            self.b = Coordinate(
                x: storedAnchorB[0],
                y: storedAnchorB[1]
            )
        } else {
            angle = offset + (2.0 * Double.pi / 3.0)
            self.b = Coordinate(
                x: (refLength * cos(angle)),
                y: (refLength * sin(angle))
            )
        }

        if let storedAnchorC = UserDefaults.standard.object(forKey: USER_DEFAULTS_STORED_ANCHOR_C) as? [Float] {
            self.c = Coordinate(
                x: storedAnchorC[0],
                y: storedAnchorC[1]
            )
        } else {
            angle = offset + (4.0 * Double.pi / 3.0)
            self.c = Coordinate(
                x: (refLength * cos(angle)),
                y: (refLength * sin(angle))
            )
        }
    }

    mutating func rotate() {
        let placeholder: Coordinate = self.a
        self.a = self.b
        self.b = self.c
        self.c = placeholder
        // Force the storing of each anchor now that they are stable
        storeA()
        storeB()
        storeC()
    }

    func reset() {
        UserDefaults.standard.removeObject(forKey: USER_DEFAULTS_STORED_ANCHOR_A)
        UserDefaults.standard.removeObject(forKey: USER_DEFAULTS_STORED_ANCHOR_B)
        UserDefaults.standard.removeObject(forKey: USER_DEFAULTS_STORED_ANCHOR_C)
    }

    func storeA() {
        UserDefaults.standard.set([ a.x, a.y ], forKey: USER_DEFAULTS_STORED_ANCHOR_A)
    }

    func storeB() {
        UserDefaults.standard.set([ b.x, b.y ], forKey: USER_DEFAULTS_STORED_ANCHOR_B)
    }

    func storeC() {
        UserDefaults.standard.set([ c.x, c.y ], forKey: USER_DEFAULTS_STORED_ANCHOR_C)
    }
}
