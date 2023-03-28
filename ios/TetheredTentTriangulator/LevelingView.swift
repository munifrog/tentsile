//
//  LevelingView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 3/12/23.
//

import SwiftUI

// https://stackoverflow.com/a/62020515
// https://stackoverflow.com/a/65586833
// https://swdevnotes.com/swift/2021/for-loop-in-swiftui/
// https://thinkdiff.net/how-to-get-screen-size-in-swiftui-cfeb0f54fe0e
struct LevelingView: View {
    @StateObject private var motionHandler = MotionHandler()
    @State private var screenCenter: [Double]

    private let COLOR_BACKGROUND = Color("LevelBackground")
    private let COLOR_BUBBLE = Color("LevelBubble")
    private let COLOR_LINE = Color("LevelLines")
    private let COLOR_MARGIN = Color("LevelRim")
    private let COLOR_TARGET = Color("LevelMain")
    private let COUNT_CIRCLES = Int(5)
    private let DIMEN_CROSS_HAIR_MARGIN = CGFloat(50)
    private let DIMEN_LINE_ROUNDING = CGFloat(3)
    private let DIMEN_LINE_WIDTH = CGFloat(3)
    private let DIMEN_RIM_MARGIN = CGFloat(20)
    private let DIMEN_TARGET_MARGIN = CGFloat(30)
    private let DIMEN_TARGET_PADDING = CGFloat(10)

    private var diamBubble = CGFloat(50)
    private var diamCrossHairs = CGFloat(250)
    private var diamInner = CGFloat(170)
    private var diamOuter = CGFloat(200)
    private var radiusSphere = CGFloat(125)
    private var ticSpacing = CGFloat(30)

    init() {
        let screenSize = UIScreen.main.bounds.size
        self.screenCenter = [screenSize.width / 2, screenSize.height / 2]
        let smaller = screenSize.width < screenSize.height ? screenSize.width : screenSize.height
        setupDimensions(width: smaller)
    }

    var body: some View {
        ZStack {
            Rectangle()
                .fill(COLOR_BACKGROUND)
                .frame(
                    maxWidth: .infinity,
                    maxHeight: .infinity,
                    alignment: .center
                )
                .measureSize(perform: {
                    // The screen size captured here is smaller than the full screen obtained earlier
                    self.screenCenter = [$0.width / 2, $0.height / 2]
                })
            Circle()
                .fill(COLOR_MARGIN)
                .frame(
                    width: diamOuter,
                    height: diamOuter,
                    alignment: .center
                )
            Circle()
                .fill(COLOR_TARGET)
                .frame(
                    width: diamInner,
                    height: diamInner,
                    alignment: .center
                )
            ForEach(1...COUNT_CIRCLES, id: \.self) {
                let diameter = ticSpacing * CGFloat($0)
                Circle()
                    .stroke(lineWidth: DIMEN_LINE_WIDTH)
                    .fill(COLOR_LINE)
                    .frame(
                        width: diameter,
                        height: diameter,
                        alignment: .center
                    )
            }
            Circle()
                .stroke(lineWidth: DIMEN_LINE_WIDTH)
                .fill(COLOR_LINE)
                .frame(
                    width: diamOuter,
                    height: diamOuter,
                    alignment: .center
                )
            RoundedRectangle(cornerRadius: DIMEN_LINE_ROUNDING)
                .fill(COLOR_LINE)
                .frame(
                    width: DIMEN_LINE_WIDTH,
                    height: diamCrossHairs,
                    alignment: .center
                )
            RoundedRectangle(cornerRadius: DIMEN_LINE_ROUNDING)
                .fill(COLOR_LINE)
                .frame(
                    width: diamCrossHairs,
                    height: DIMEN_LINE_WIDTH,
                    alignment: .center
                )
            Group {
                let bubble: [Double] = convertCoordinates(position: motionHandler.getPosition())
                let x = screenCenter[0] + self.radiusSphere * bubble[0]
                let y = screenCenter[1] + self.radiusSphere * bubble[1]
                Circle()
                    .fill(COLOR_BUBBLE)
                    .frame(
                        width: diamBubble,
                        height: diamBubble,
                        alignment: .center
                    )
                    .position(CGPoint(x: x, y: y))
            }
        }
        .onAppear(perform: motionHandler.start)
        .onDisappear(perform: motionHandler.stop)
    }

    mutating func setupDimensions(width: Double) {
        self.diamCrossHairs = width - DIMEN_TARGET_MARGIN
        self.diamOuter = self.diamCrossHairs - DIMEN_CROSS_HAIR_MARGIN
        self.diamInner = self.diamOuter - DIMEN_RIM_MARGIN
        self.ticSpacing = self.diamInner / CGFloat(COUNT_CIRCLES)
        // The bubble should fit inside of the innermost circle
        self.diamBubble = self.ticSpacing - (DIMEN_LINE_WIDTH / 2)
        self.radiusSphere = (self.diamOuter - diamBubble) / 2
    }

    func getOrientation() -> UIInterfaceOrientation {
        guard let scene = UIApplication.shared.windows.first?.windowScene
        else { return UIInterfaceOrientation.portrait }
        return scene.interfaceOrientation
    }

    func convertCoordinates(position: [Double]) -> [Double] {
        switch getOrientation() {
        case .landscapeRight:
            return [position[1], -position[0], position[2]]
        case .landscapeLeft:
            return [-position[1], position[0], position[2]]
        default:
            return position
        }
    }
}

struct LevelingView_Previews: PreviewProvider {
    static var previews: some View {
        LevelingView()
    }
}
