//
//  MotionHandler.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 3/19/23.
//

import Foundation
import CoreMotion

class MotionHandler: ObservableObject {
    @Published private var position: [Double]

    private var active: Bool = false
    private let manager = CMMotionManager()
    private let queue = OperationQueue()
    private let INTERVAL = Double(1.0 / 60.0)

    init() {
        position = [Double.zero, Double.zero, Double.zero]
    }

    func start() {
        if !active {
            active = true
            self.manager.deviceMotionUpdateInterval = INTERVAL
            self.manager.startDeviceMotionUpdates(to: OperationQueue.main) { motion, error in
                if let data = motion {
                    // In a sphere the bubble would appear opposite the direction of gravity.
                    // The y-axis is reversed on screens, so its value is already negated.
                    // Gravity is returned as a unit vector, so there is no need to compute further
                    self.position = [-data.gravity.x, data.gravity.y, -data.gravity.z]
                }
            }
        }
    }

    func stop() -> Void {
        if active {
            active = false
            self.manager.stopDeviceMotionUpdates()
        }
    }

    func getPosition() -> [Double] {
        return self.position
    }
}
