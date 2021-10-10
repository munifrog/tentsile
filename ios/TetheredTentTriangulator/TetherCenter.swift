//
//  TetherCenter.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/15/21.
//

import Foundation

struct TetherCenter {
    var p: Coordinate
    var pa: Float
    var pb: Float
    var pc: Float
    var flips: Bool

    init(p: Coordinate, pa: Float, pb: Float, pc: Float, flips: Bool) {
        self.p = p
        self.pa = pa
        self.pb = pb
        self.pc = pc
        self.flips = flips
    }

    mutating func rotate() {
        let placeholder: Float = self.pa
        self.pa = self.pb
        self.pb = self.pc
        self.pc = placeholder
    }
}
