//
//  Configuration.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/11/21.
//

import Foundation

enum Select {
    case anchor_a
    case ab
    case ab_waiting
    case anchor_b
    case bc
    case bc_waiting
    case anchor_c
    case ca
    case ca_waiting
    case rotate
    case ignore
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
private let MATH_THREE_EIGHTHS_INCH_TO_FEET_CONVERSION: Float = 0.03125;
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
    var fineTuneOffset: Float = 0
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

    private var radiusSquared: Float = 3600
    private var distanceScale: Float = 25.0
    private var drawable: DrawableSetup?
    private var imageScale: Float = 0.04
    private var initial_p: Coordinate?
    private var initial_a: Coordinate?
    private var initial_b: Coordinate?
    private var initial_c: Coordinate?
    private var initial_angle_t: Float?
    private var initial_angle_a: Float?
    private var initial_angle_b: Float?
    private var initial_angle_c: Float?
    private var initial_pa: Float?
    private var initial_pb: Float?
    private var initial_pc: Float?
    private var limits: Coordinate = Coordinate()
    private var path: PlatformPath = PlatformPath()
    private var latestFlip: Bool = false

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
        updateTetherCenter()
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

    mutating private func resetInitialAngles() {
        self.initial_angle_t = nil
        self.initial_angle_a = nil
        self.initial_angle_b = nil
        self.initial_angle_c = nil
        self.initial_pa = nil
        self.initial_pb = nil
        self.initial_pc = nil
    }

    mutating private func saveInitialAngles(touch: Coordinate) {
        if self.center != nil {
            let p = self.center!.p
            let delta_t = touch - p
            let hyp_t = sqrt(delta_t.x * delta_t.x + delta_t.y * delta_t.y)
            self.initial_angle_t = Util.getDirection(h: hyp_t, delta_x: delta_t.x, delta_y: delta_t.y)
            let delta_a = anchors.a - p
            let hyp_a = sqrt(delta_a.x * delta_a.x + delta_a.y * delta_a.y)
            self.initial_pa = hyp_a
            self.initial_angle_a = Util.getDirection(h: hyp_a, delta_x: delta_a.x, delta_y: delta_a.y)
            let delta_b = anchors.b - p
            let hyp_b = sqrt(delta_b.x * delta_b.x + delta_b.y * delta_b.y)
            self.initial_pb = hyp_b
            self.initial_angle_b = Util.getDirection(h: hyp_b, delta_x: delta_b.x, delta_y: delta_b.y)
            let delta_c = anchors.c - p
            let hyp_c = sqrt(delta_c.x * delta_c.x + delta_c.y * delta_c.y)
            self.initial_pc = hyp_c
            self.initial_angle_c = Util.getDirection(h: hyp_c, delta_x: delta_c.x, delta_y: delta_c.y)
        }
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

    mutating func endSelection(touch: Coordinate) {
        switch selection {
        case .anchor_a, .anchor_b, .anchor_c :
            resetInitialPositions()
            selection = .none
        case .ab_waiting, .bc_waiting, .ca_waiting:
            let end_selection = getSelection(touch: touch)
            if selection == end_selection {
                if end_selection == .ab_waiting {
                    selection = .ab
                } else if end_selection == .bc_waiting {
                    selection = .bc
                } else {
                    selection = .ca
                }
                fineTuneOffset = 0
            } else {
                selection = .none
            }
        case .rotate:
            resetInitialAngles()
            selection = .none
        case .point, .ignore:
            selection = .none
        default:
            // Do nothing
            break
        }
    }

    mutating func isFineTuning() -> Bool {
        return selection == .ab || selection == .bc || selection == .ca
    }

    private mutating func getSelectedPerimeterDistance() -> Float {
        let segment: Float = self.units == .metric ? 0.01 : MATH_THREE_EIGHTHS_INCH_TO_FEET_CONVERSION
        var pixels: Float = 0.0
        if selection == .ab {
            pixels = anchors.ab
        } else if selection == .bc {
            pixels = anchors.bc
        } else if selection == .ca {
            pixels = anchors.ca
        } else {
            pixels = 0.0
        }
        return Util.getMeasureFromPixels(
            pixels: pixels,
            meterScale: distanceScale,
            units: self.units
        ) + fineTuneOffset * segment
    }

    mutating func getSelectedPerimeterString() -> String {
        return Util.getMeasurementString(
            measure: getSelectedPerimeterDistance(),
            precision: Precision.hundredths,
            units: self.units
        )
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
        case .ab, .bc, .ca:
            makeAnchorsMatchMeasurements()
            selection = .none
            updateTetherCenter()
        case .ab_waiting, .bc_waiting, .ca_waiting:
            // Wait for the touch event to end before doing anything
            break
        case .rotate:
            updateAnchorRotations(touch: touch)
            updateTetherCenter()
        case .ignore:
            // Do nothing
            break
        case .none:
            // Assume the user did not touch close enough to any anchor point
            // Set selection to ignore any other motion events
            self.selection = .ignore
            self.selection = self.getSelection(touch: touch)
            if self.selection == .point {
                saveInitialPositions()
            } else if self.selection == .rotate {
                saveInitialAngles(touch: touch)
            }
        case .point:
            updateAnchorTranslations(touch: touch)
            updateTetherCenter()
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
        var newSelection: Select = .rotate

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

        diff = touch - self.anchors.ab_label
        diffSquared = diff.x * diff.x + diff.y * diff.y
        if diffSquared < closestDist {
            newSelection = .ab_waiting
            closestDist = diffSquared
        }

        diff = touch - self.anchors.bc_label
        diffSquared = diff.x * diff.x + diff.y * diff.y
        if diffSquared < closestDist {
            newSelection = .bc_waiting
            closestDist = diffSquared
        }

        diff = touch - self.anchors.ca_label
        diffSquared = diff.x * diff.x + diff.y * diff.y
        if diffSquared < closestDist {
            newSelection = .ca_waiting
            closestDist = diffSquared
        }

        if getCanDrawPlatform() {
            diff = touch - center!.p
            diffSquared = diff.x * diff.x + diff.y * diff.y
            if diffSquared < closestDist {
                newSelection = .point
                //closestDist = diffSquared
            }
        }

        return newSelection
    }

    mutating func makeAnchorsMatchMeasurements() {
        // We need two fixed points to determine where to place the third.
        // One will be opposite the line segment that is changing.
        // The other will be at the corner next to the shorter side.
        var shortSide: Float
        var longSide: Float
        var pivot: Coordinate
        var opposite: Coordinate
        var multiplier: Float = getFlip() ? -1.0 : 1.0
        var anchorToUpdate: Select
        if selection == .ab {
            opposite = anchors.c
            if anchors.bc < anchors.ca {
                shortSide = anchors.bc
                longSide = anchors.ca
                pivot = anchors.b
                anchorToUpdate = Select.anchor_a
            } else {
                multiplier *= -1.0
                shortSide = anchors.ca
                longSide = anchors.bc
                pivot = anchors.a
                anchorToUpdate = Select.anchor_b
            }
        } else if selection == .bc {
            opposite = anchors.a
            if anchors.ab < anchors.ca {
                multiplier *= -1.0
                shortSide = anchors.ab
                longSide = anchors.ca
                pivot = anchors.b
                anchorToUpdate = Select.anchor_c
            } else {
                shortSide = anchors.ca
                longSide = anchors.ab
                pivot = anchors.c
                anchorToUpdate = Select.anchor_b
            }
        } else { //if selection == .ca {
            opposite = anchors.b
            if anchors.ab < anchors.bc {
                shortSide = anchors.ab
                longSide = anchors.bc
                pivot = anchors.a
                anchorToUpdate = Select.anchor_c
            } else {
                multiplier *= -1.0
                shortSide = anchors.bc
                longSide = anchors.ab
                pivot = anchors.c
                anchorToUpdate = Select.anchor_a
            }
        }
        // Determine the angle from pivot to opposite points
        let delta = opposite - pivot
        let hypotenuse = sqrt(delta.x * delta.x + delta.y * delta.y)
        let startingAngle: Float = Util.getDirection(h: hypotenuse, delta_x: delta.x, delta_y: delta.y)
        // Determine the new angle inside the triangle at the pivot corner
        let changingSegment: Float = Util.getPixelsFromMeasure(
            measure: getSelectedPerimeterDistance(),
            pixelScale: imageScale,
            units: self.units
        )
        let numerator: Float = changingSegment * changingSegment + shortSide * shortSide - longSide * longSide
        let denominator: Float = 2 * changingSegment * shortSide
        let insideAngle: Float = acos(numerator / denominator)
        // The multiplier helps determine whether to add or subtract the inside angle
        let totalAngle = startingAngle + multiplier * insideAngle
        // Update moving anchor relative to pivot point
        let updatedCoordinate = Coordinate(
            x: pivot.x + changingSegment * cos(totalAngle),
            y: pivot.y + changingSegment * sin(totalAngle)
        )
        // Only change the anchor that moves
        if anchorToUpdate == Select.anchor_a {
            anchors.a = updatedCoordinate
        } else if anchorToUpdate == Select.anchor_b {
            anchors.b = updatedCoordinate
        } else { //if anchorToUpdate == Select.anchor_c {
            anchors.c = updatedCoordinate
        }
    }

    mutating func updateAnchorRotations(touch: Coordinate) {
        // Update all of the anchor points by the same rotation
        guard let p = self.center?.p else { return }
        guard let initAngleT = self.initial_angle_t else { return }
        guard let initAngleA = self.initial_angle_a else { return }
        guard let initAngleB = self.initial_angle_b else { return }
        guard let initAngleC = self.initial_angle_c else { return }
        guard let initPA = self.initial_pa else { return }
        guard let initPB = self.initial_pb else { return }
        guard let initPC = self.initial_pc else { return }
        let delta = touch - p
        let hypotenuse = sqrt(delta.x * delta.x + delta.y * delta.y)
        let angleNow = Util.getDirection(h: hypotenuse, delta_x: delta.x, delta_y: delta.y)
        let angleDiff = angleNow - initAngleT
        let angleA = initAngleA + angleDiff
        let angleB = initAngleB + angleDiff
        let angleC = initAngleC + angleDiff
        self.anchors.a = Coordinate(
            x: p.x + initPA * cos(angleA),
            y: p.y + initPA * sin(angleA)
        )
        self.anchors.b = Coordinate(
            x: p.x + initPB * cos(angleB),
            y: p.y + initPB * sin(angleB)
        )
        self.anchors.c = Coordinate(
            x: p.x + initPC * cos(angleC),
            y: p.y + initPC * sin(angleC)
        )
    }

    mutating func updateAnchorTranslations(touch: Coordinate) {
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
        self.center = Util.getTetherCenter(self.anchors,  smallAngle: getPlatform().tetherangle)
        updateFlip()
        self.drawable = computeDrawableSetup()
    }

    func getFlip() -> Bool {
        return latestFlip
    }

    mutating func updateFlip() {
        if let center = self.center {
            self.latestFlip = center.flips
        }
    }
}
