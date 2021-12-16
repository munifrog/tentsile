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
        if let knots = config.knots {
            let knotSize: CGFloat = 7
            let tetherWidth: CGFloat = 3

            let a = knots.a
            let b = knots.b
            let c = knots.c
            let aCount: Int = a.count
            let bCount: Int = b.count
            let cCount: Int = c.count
            ZStack {
                ForEach(1..<aCount, id: \.self) { i in
                    let index = aCount - i
                    let previousIndex = index - 1
                    let color: Color = getColor(index)
                    let knot = CGPoint(a[index])
                    Rectangle()
                        .foregroundColor(.clear)
                        .overlay(Path() { path in
                            path.move(to: CGPoint(a[previousIndex]));
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
                    let index = bCount - i
                    let previousIndex = index - 1
                    let color: Color = getColor(index)
                    let knot = CGPoint(b[index])
                    Rectangle()
                        .foregroundColor(.clear)
                        .overlay(Path() { path in
                            path.move(to: CGPoint(b[previousIndex]));
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
                    let index = cCount - i
                    let previousIndex = index - 1
                    let color: Color = getColor(index)
                    let knot = CGPoint(c[index])
                    Rectangle()
                        .foregroundColor(.clear)
                        .overlay(Path() { path in
                            path.move(to: CGPoint(c[previousIndex]));
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
