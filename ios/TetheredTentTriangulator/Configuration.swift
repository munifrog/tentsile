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

enum Units {
    case imperial
    case metric
}

private let MATH_BASE_PIXELS_PER_METER: Float = 75;
private let MATH_METERS_TO_FEET_CONVERSION: Float = 3.2808399;
private let MATH_SLIDER_POINT_00: Float = 0.0;
private let MATH_SLIDER_POINT_01: Float = 50.0;
private let MATH_SLIDER_POINT_02: Float = 75.0;
private let MATH_SLIDER_POINT_03: Float = 100.0;
private let MATH_SCALE_POINT_00: Float = 1.0;
private let MATH_SCALE_POINT_01: Float = 3.0;
private let MATH_SCALE_POINT_02: Float = 8.0;
private let MATH_SCALE_POINT_03: Float = 10.0;
private let MATH_SCALE_SLOPE_00_01: Float =
    (MATH_SCALE_POINT_01 - MATH_SCALE_POINT_00) /
    (MATH_SLIDER_POINT_01 - MATH_SLIDER_POINT_00);
private let MATH_SCALE_SLOPE_01_02: Float =
    (MATH_SCALE_POINT_02 - MATH_SCALE_POINT_01) /
    (MATH_SLIDER_POINT_02 - MATH_SLIDER_POINT_01);
private let MATH_SCALE_SLOPE_02_03: Float =
    (MATH_SCALE_POINT_03 - MATH_SCALE_POINT_02) /
    (MATH_SLIDER_POINT_03 - MATH_SLIDER_POINT_02);
private let USER_DEFAULTS_STORED_PLATFORM = "com.munifrog.tethered.tent.triangulator.config.platform"

struct Configuration {
    var anchors: Anchors
    var center: TetherCenter?
    var platform: Platform {
        didSet {
            UserDefaults.standard.set(platform.rawValue, forKey: USER_DEFAULTS_STORED_PLATFORM)
        }
    }
    var scale: Float = 25.0 {
        didSet {
            updateConvertedScale()
        }
    }
    var selection: Select = .none
    var units: Units = .metric
    var util: Util = Util()

    private var radiusSquared: Float = 225
    private var distanceScale: Float = 25.0
    private var imageScale: Float = 0.04
    private var initial_p: Coordinate?
    private var initial_a: Coordinate?
    private var initial_b: Coordinate?
    private var initial_c: Coordinate?
    private var limits: Coordinate = Coordinate()
    private var path: PlatformPath = PlatformPath()

    init() {
        if let storedPlatform = UserDefaults.standard.string(forKey: USER_DEFAULTS_STORED_PLATFORM) {
            self.platform = Platform(rawValue: storedPlatform)!
        } else {
            self.platform = .stingray
        }
        self.anchors = Anchors()
        self.center = util.getTetherCenter(self.anchors)
        self.resetInitialPositions()
        self.updateConvertedScale()
    }

    init(anchors: Anchors) {
        if let storedPlatform = UserDefaults.standard.string(forKey: USER_DEFAULTS_STORED_PLATFORM) {
            self.platform = Platform(rawValue: storedPlatform)!
        } else {
            self.platform = .stingray
        }
        self.anchors = anchors
        self.center = util.getTetherCenter(self.anchors)
        self.resetInitialPositions()
        self.updateConvertedScale()
    }

    mutating func rotate() {
        self.anchors.rotate()
        if var c = center {
            c.rotate()
        }
    }

    mutating func setLimits(screen: Coordinate) {
        self.limits = screen
    }

    func getLimits() -> Coordinate {
        return self.limits
    }

    mutating func getPlatform() -> PlatformDetails {
        return path.getDetails(platform)
    }

    mutating func resetAnchors() {
        self.anchors = Anchors()
        self.center = util.getTetherCenter(self.anchors)
    }

    mutating private func resetInitialPositions() {
        self.initial_p = nil
        self.initial_a = nil
        self.initial_b = nil
        self.initial_c = nil
    }

    mutating private func saveInitialPositions() {
        self.initial_p = self.center?.p
        self.initial_a = self.anchors.a
        self.initial_b = self.anchors.b
        self.initial_c = self.anchors.c
    }

    mutating func endSelection() {
        self.selection = .none
        self.resetInitialPositions()
    }

    mutating func updateSelection(touch: Coordinate) {
        // When already selected, allow the selected point to be updated with the new location
        switch selection {
        case .anchor_a:
            anchors.a = getBoundedTouch(touch: touch)
            center = util.getTetherCenter(self.anchors)
        case .anchor_b:
            anchors.b = getBoundedTouch(touch: touch)
            center = util.getTetherCenter(self.anchors)
        case .anchor_c:
            anchors.c = getBoundedTouch(touch: touch)
            center = util.getTetherCenter(self.anchors)
        case .missed:
            // Do nothing
            break
        case .none:
            // Assume the user did not touch close enough to any anchor point
            // Set selection as missed to ignore any other motion events
            self.selection = .missed
            self.selection = self.getSelection(touch: touch)
        case .point:
            self.updateAnchors(touch: touch)
            break
        }
    }

    func getBoundedTouch(touch: Coordinate) -> Coordinate {
        var limitedV = Coordinate(x: touch.x, y: touch.y)
        let max = self.limits
        let min = -max

        if touch.x < min.x {
            limitedV.x = min.x
        } else if touch.x > max.x {
            limitedV.x = max.x
        }
        if touch.y < min.y {
            limitedV.y = min.y
        } else if touch.y > max.y {
            limitedV.y = max.y
        }
        return limitedV
    }

    mutating func getSelection(touch: Coordinate) -> Select {
        // Determine if any selection points are close enough to the touch point
        var closestDist = self.radiusSquared
        var newSelection: Select = self.selection

        var diff = touch - self.anchors.a
        var diffSquared = diff.x * diff.x + diff.y * diff.y
        if diffSquared < closestDist {
            newSelection = .anchor_a
            closestDist = diffSquared
        }

        diff = touch - self.anchors.b
        diffSquared = diff.x * diff.x + diff.y * diff.y
        if diffSquared < closestDist {
            newSelection = .anchor_b
            closestDist = diffSquared
        }

        diff = touch - self.anchors.c
        diffSquared = diff.x * diff.x + diff.y * diff.y
        if diffSquared < closestDist {
            newSelection = .anchor_c
            closestDist = diffSquared
        }

        if let center = self.center {
            diff = touch - center.p
            diffSquared = diff.x * diff.x + diff.y * diff.y
            if diffSquared < closestDist {
                newSelection = .point
                //closestDist = diffSquared
                saveInitialPositions()
            }
        }

        return newSelection
    }

    mutating func updateAnchors(touch: Coordinate) {
        // Update all of the anchor points by the same vector
        guard let initP = self.initial_p else { return }
        guard let initA = self.initial_a else { return }
        guard let initB = self.initial_b else { return }
        guard let initC = self.initial_c else { return }

        // touch = initP + v
        let v = touch - initP

        // Limit the anchors to within the Clearing view
        let tempA = initA + v
        let tempB = initB + v
        let tempC = initC + v

        // Need to determine the greatest value in each direction
        var smallestX = tempA.x
        if (tempB.x < smallestX) {
            smallestX = tempB.x
        }
        if (tempC.x < smallestX) {
            smallestX = tempC.x
        }
        var largestX = tempA.x
        if (tempB.x > largestX) {
            largestX = tempB.x
        }
        if (tempC.x > largestX) {
            largestX = tempC.x
        }

        var smallestY = tempA.y
        if (tempB.y < smallestY) {
            smallestY = tempB.y
        }
        if (tempC.y < smallestY) {
            smallestY = tempC.y
        }
        var largestY = tempA.y
        if (tempB.y > largestY) {
            largestY = tempB.y
        }
        if (tempC.y > largestY) {
            largestY = tempC.y
        }

        var limitedV = Coordinate(x: 0.0, y: 0.0)
        let max = self.limits
        let min = -max

        if smallestX < min.x {
            limitedV.x =  (min.x - smallestX)
        } else if largestX > max.x {
            limitedV.x =  (max.x - largestX)
        }
        if smallestY < min.y {
            limitedV.y =  (min.y - smallestY)
        } else if largestY > max.y {
            limitedV.y =  (max.y - largestY)
        }

        self.center?.p = initP + v + limitedV
        self.anchors.a = initA + v + limitedV
        self.anchors.b = initB + v + limitedV
        self.anchors.c = initC + v + limitedV
    }

    mutating func getPath() -> [[Coordinate]] {
        return path.getDetails(platform).path
            .rotated(by: getRotation())
            .scaled(by: getImageScale())
            .translated(by: getTranslation())
    }

    mutating func getExtremities() -> [Coordinate] {
        return path.getDetails(platform).extremites
            .rotated(by: getRotation())
            .scaled(by: getImageScale())
            .translated(by: getTranslation())
    }

    mutating func getRotation() -> Float {
        if let focus = center {
            let delta = anchors.a - focus.p
            let hypotenuse = sqrt(delta.x * delta.x + delta.y * delta.y)
            return util.getDirection(h: hypotenuse, delta_x: delta.x, delta_y: delta.y)
        } else {
            return 0
        }
    }

    mutating func getTranslation() -> Coordinate {
        if let focus = center {
            return getLimits() + focus.p
        } else {
            return Coordinate()
        }
    }

    mutating func getImageScale() -> Float {
        if let _ = center {
            return imageScale
        } else {
            return 0
        }
    }

    mutating func updateConvertedScale() {
        var diff: Float
        var offset: Float
        var slope: Float
        if (scale < MATH_SLIDER_POINT_01) {
            diff = scale - MATH_SLIDER_POINT_00;
            offset = MATH_SCALE_POINT_00;
            slope = MATH_SCALE_SLOPE_00_01;
        } else if (scale < MATH_SLIDER_POINT_02) {
            diff = scale - MATH_SLIDER_POINT_01;
            offset = MATH_SCALE_POINT_01;
            slope = MATH_SCALE_SLOPE_01_02;
        } else { // if (position <= MATH_SLIDER_POINT_03) {
            diff = scale - MATH_SLIDER_POINT_02;
            offset = MATH_SCALE_POINT_02;
            slope = MATH_SCALE_SLOPE_02_03;
        }
        let scale = offset + slope * diff
        distanceScale = scale / MATH_BASE_PIXELS_PER_METER
        imageScale = MATH_BASE_PIXELS_PER_METER / scale
    }

    func getDistance(_ points: Float) -> Float {
        return points * distanceScale * (units == .metric ? 1.0 : MATH_METERS_TO_FEET_CONVERSION)
    }
}
