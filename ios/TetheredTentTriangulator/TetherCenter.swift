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

    init(p: Coordinate, pa: Float, pb: Float, pc: Float) {
        self.p = p
        self.pa = pa
        self.pb = pb
        self.pc = pc
    }
}
