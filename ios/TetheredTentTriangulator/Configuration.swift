//
//  Configuration.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/11/21.
//

import Foundation

struct Configuration {
    var anchors: Anchors

    init() {
        self.anchors = Anchors()
    }

    init(anchors: Anchors) {
        self.anchors = anchors
    }
}
