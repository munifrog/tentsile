//
//  Configuration.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/11/21.
//

import Foundation

enum Select {
    case anchor_a
    case anchor_b
    case anchor_c
    case missed
    case none
    case point
}

struct Configuration {
    var anchors: Anchors
    private var selection: Select = .none
    private var radiusSquared: Float = 225

    init() {
        self.anchors = Anchors()
    }

    init(anchors: Anchors) {
        self.anchors = anchors
    }

    mutating func endSelection() {
        self.selection = .none
    }

    mutating func updateSelection(coordinate: Coordinate) {
        // When already selected, allow the selected point to be updated with the new location
        switch selection {
        case .anchor_a:
            self.anchors.a.x = coordinate.x
            self.anchors.a.y = coordinate.y
        case .anchor_b:
            self.anchors.b.x = coordinate.x
            self.anchors.b.y = coordinate.y
        case .anchor_c:
            self.anchors.c.x = coordinate.x
            self.anchors.c.y = coordinate.y
        case .missed:
            // Do nothing
            break
        case .none:
            // Assume the user did not touch close enough to any anchor point
            // Set selection as missed to ignore any other motion events
            selection = .missed

            // Determine if any selection points are close enough to the touch point
            var closestDist = radiusSquared
            var newSelection: Select = selection

            var diff_x = coordinate.x - self.anchors.a.x
            var diff_y = coordinate.y - self.anchors.a.y
            var diffSquared = diff_x * diff_x + diff_y * diff_y
            if diffSquared < closestDist {
                newSelection = .anchor_a
                closestDist = diffSquared
            }

            diff_x = coordinate.x - self.anchors.b.x
            diff_y = coordinate.y - self.anchors.b.y
            diffSquared = diff_x * diff_x + diff_y * diff_y
            if diffSquared < closestDist {
                newSelection = .anchor_b
                closestDist = diffSquared
            }

            diff_x = coordinate.x - self.anchors.c.x
            diff_y = coordinate.y - self.anchors.c.y
            diffSquared = diff_x * diff_x + diff_y * diff_y
            if diffSquared < closestDist {
                newSelection = .anchor_c
                closestDist = diffSquared
            }

            // TODO: Add another comparison here when there is a tether center

            selection = newSelection
        case .point:
            // Update all of the anchor points by the same vector
            break
        }
    }
}
