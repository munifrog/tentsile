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

enum Units: String {
    case imperial = "imperial"
    case metric = "metric"
}

enum Symbols: Int, Comparable {
    case none = 0
    case cannot
    case warn
    case tricky

    static func < (left: Symbols, right: Symbols) -> Bool {
        return left.rawValue < right.rawValue
    }

    static postfix func ++ (symbol: inout Symbols) {
        switch symbol {
        case none:
            symbol = Symbols.cannot
        case cannot:
            symbol = Symbols.warn
        case warn:
            symbol = Symbols.tricky
        case tricky:
            // Do nothing
            return
        }
    }

    static postfix func -- (symbol: inout Symbols) {
        switch symbol {
        case tricky:
            symbol = Symbols.warn
        case warn:
            symbol = Symbols.cannot
        case cannot:
            symbol = Symbols.none
        case none:
            // Do nothing
            return
        }
    }
}

private let MATH_BASE_PIXELS_PER_METER: Float = 75;
private let MATH_METERS_CENTER_TO_ANCHOR_MIN: Float = 0.7
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
private let USER_DEFAULTS_STORED_SCALE = "com.munifrog.tethered.tent.triangulator.config.slider"
private let USER_DEFAULTS_STORED_SYMBOLS = "com.munifrog.tethered.tent.triangulator.config.symbols"
private let USER_DEFAULTS_STORED_UNITS = "com.munifrog.tethered.tent.triangulator.config.units"

struct Configuration {
    var anchors: Anchors
    var center: TetherCenter?
    var platform: Platform {
        didSet {
            UserDefaults.standard.set(platform.rawValue, forKey: USER_DEFAULTS_STORED_PLATFORM)
            updateTetherCenter()
        }
    }
    var scale: Float {
        didSet {
            UserDefaults.standard.set(scale, forKey: USER_DEFAULTS_STORED_SCALE)
            updateConvertedScale()
            updateTetherCenter()
        }
    }
    var selection: Select = .none
    var symbols: Symbols {
        didSet {
            UserDefaults.standard.set(symbols.rawValue, forKey: USER_DEFAULTS_STORED_SYMBOLS)
        }
    }
    var units: Units {
        didSet {
            UserDefaults.standard.set(units.rawValue, forKey: USER_DEFAULTS_STORED_UNITS)
        }
    }

    private var radiusSquared: Float = 225
    private var distanceScale: Float = 25.0
    private var drawable: DrawableSetup?
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
        if let storedScale = UserDefaults.standard.object(forKey: USER_DEFAULTS_STORED_SCALE) as? Float {
            self.scale = storedScale
        } else {
            self.scale = 25.0
        }
        if let storedSymbols = UserDefaults.standard.object(forKey: USER_DEFAULTS_STORED_SYMBOLS) as? Int {
            self.symbols = Symbols(rawValue: storedSymbols)!
        } else {
            self.symbols = .warn
        }
        if let storedUnits = UserDefaults.standard.string(forKey: USER_DEFAULTS_STORED_UNITS) {
            self.units = Units(rawValue: storedUnits)!
        } else {
            self.units = .metric
        }
        self.anchors = Anchors()
        self.updateTetherCenter()
        self.resetInitialPositions()
        self.updateConvertedScale()
    }

    init(_ screen: Coordinate) {
        self.init()
        self.setLimits(screen: screen)
    }

    mutating func rotate() {
        self.anchors.rotate()
        if var c = center {
            c.rotate()
            updateTetherCenter()
        }
    }

    mutating func setLimits(screen: Coordinate) {
        self.limits = screen
        updateTetherCenter()
    }

    func getLimits() -> Coordinate {
        return self.limits
    }

    func getDrawableSetup() -> DrawableSetup {
        return drawable!
    }

    func computeDrawableSetup() -> DrawableSetup {
        DrawableSetup(
            anchors: self.anchors,
            center: self.center,
            platform: getPlatform(),
            scale: imageScale,
            offset: getLimits()
        )
    }

    func getCanDrawPlatform() -> Bool {
        if let c = center {
            let allowance = getImageScale() * MATH_METERS_CENTER_TO_ANCHOR_MIN
            return c.pa > allowance && c.pb > allowance && c.pc > allowance
        } else {
            return false
        }
    }

    func getPlatform() -> PlatformDetails {
        return path.getDetails(platform)
    }

    mutating func resetAnchors() {
        self.anchors.reset()
        self.anchors = Anchors()
        self.updateTetherCenter()
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
            updateTetherCenter()
        case .anchor_b:
            anchors.b = getBoundedTouch(touch: touch)
            updateTetherCenter()
        case .anchor_c:
            anchors.c = getBoundedTouch(touch: touch)
            updateTetherCenter()
        case .missed:
            // Do nothing
            break
        case .none:
            // Assume the user did not touch close enough to any anchor point
            // Set selection as missed to ignore any other motion events
            self.selection = .missed
            self.selection = self.getSelection(touch: touch)
        case .point:
            updateAnchors(touch: touch)
            updateTetherCenter()
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

        if getCanDrawPlatform() {
            diff = touch - center!.p
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

    func getImageScale() -> Float {
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

    mutating func updateTetherCenter() {
        self.center = Util.getTetherCenter(self.anchors)
        self.drawable = computeDrawableSetup()
    }
}
