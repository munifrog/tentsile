//
//  TetherView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/15/21.
//

import SwiftUI

struct TetherView: View {
    @Binding var config: Configuration

    var body: some View {
        if let center = config.center {
            let b_index = center.flips ? 2 : 1
            let c_index = center.flips ? 1 : 2
            let extremes = config.getExtremities()
            let limits = config.getLimits()
            let pixelsPerMeter = config.getImageScale()
            let strap = config.getPlatform().strap
            let aTether: [Coordinate] = config.util.getSegmentKnots(
                start: limits + center.p,
                extremity: extremes[0],
                end: limits + config.anchors.a,
                pixelsPerMeter: pixelsPerMeter,
                strap: strap
            )
            let aCount: Int = aTether.count
            let bTether: [Coordinate] = config.util.getSegmentKnots(
                start: limits + center.p,
                extremity: extremes[b_index],
                end: limits + config.anchors.b,
                pixelsPerMeter: pixelsPerMeter,
                strap: strap
            )
            let bCount: Int = bTether.count
            let cTether: [Coordinate] = config.util.getSegmentKnots(
                start: limits + center.p,
                extremity: extremes[c_index],
                end: limits + config.anchors.c,
                pixelsPerMeter: pixelsPerMeter,
                strap: strap
            )
            let cCount: Int = cTether.count
            let knotSize: CGFloat = 7
            let tetherWidth: CGFloat = 3
            ZStack {
                ForEach(1..<aCount, id: \.self) { i in
                    let index = aTether.count - i
                    let previousIndex = index - 1
                    let color: Color = getColor(index)
                    let knot = CGPoint(
                        x: CGFloat(aTether[index].x),
                        y: CGFloat(aTether[index].y)
                    )
                    Rectangle()
                        .foregroundColor(.clear)
                        .overlay(Path() { path in
                            path.move(to: CGPoint(
                                x: CGFloat(aTether[previousIndex].x),
                                y: CGFloat(aTether[previousIndex].y)
                            ));
                            path.addLine(to: knot)
                        }.stroke(style: StrokeStyle(lineWidth: tetherWidth))
                        .foregroundColor(color)
                        )
                        .overlay(
                            Circle()                            .fill(color)
                                .frame(width: knotSize, height: knotSize, alignment: .center)
                                .position(knot)
                        )
                }
                ForEach(1..<bCount, id: \.self) { i in
                    let index = bTether.count - i
                    let previousIndex = index - 1
                    let color: Color = getColor(index)
                    let knot = CGPoint(
                        x: CGFloat(bTether[index].x),
                        y: CGFloat(bTether[index].y)
                    )
                    Rectangle()
                        .foregroundColor(.clear)
                        .overlay(Path() { path in
                            path.move(to: CGPoint(
                                x: CGFloat(bTether[previousIndex].x),
                                y: CGFloat(bTether[previousIndex].y)
                            ));
                            path.addLine(to: knot)
                        }.stroke(style: StrokeStyle(lineWidth: tetherWidth))
                        .foregroundColor(color)
                        )
                        .overlay(
                            Circle()                            .fill(color)
                                .frame(width: knotSize, height: knotSize, alignment: .center)
                                .position(knot)
                        )
                }
                ForEach(1..<cCount, id: \.self) { i in
                    let index = cTether.count - i
                    let previousIndex = index - 1
                    let color: Color = getColor(index)
                    let knot = CGPoint(
                        x: CGFloat(cTether[index].x),
                        y: CGFloat(cTether[index].y)
                    )
                    Rectangle()
                        .foregroundColor(.clear)
                        .overlay(Path() { path in
                            path.move(to: CGPoint(
                                x: CGFloat(cTether[previousIndex].x),
                                y: CGFloat(cTether[previousIndex].y)
                            ));
                            path.addLine(to: knot)
                        }.stroke(style: StrokeStyle(lineWidth: tetherWidth))
                        .foregroundColor(color)
                        )
                        .overlay(
                            Circle()                            .fill(color)
                                .frame(width: knotSize, height: knotSize, alignment: .center)
                                .position(knot)
                        )
                }
            }
        }
    }

    private func getColor(_ index: Int) -> Color {
        if index > 2 {
            if index % 2 == 0 {
                return Color("TetherExtension")
            } else {
                return Color("TetherProvided")
            }
        } else if index > 1 {
            return Color("TetherProvided")
        } else {
            return Color("TetherUnder")
        }
    }
}

struct TetherView_Previews: PreviewProvider {
    static var previews: some View {
        TetherView(config: .constant(Configuration(anchors: Anchors())))
    }
}
