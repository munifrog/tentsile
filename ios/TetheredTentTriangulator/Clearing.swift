//
//  Clearing.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/6/21.
//

import SwiftUI

struct Clearing: View {
    @State var touchPoint = CGPoint(x:0, y:0)
    @State var configuration = Configuration()

    var touches: some Gesture {
        // https://stackoverflow.com/a/60219793
        DragGesture(minimumDistance: 0)
            .onChanged({ touch in
                let screenSize = UIScreen.main.bounds
                let halfWidth: CGFloat = screenSize.width / 2.0
                let halfHeight: CGFloat = screenSize.height / 2.0

                self.touchPoint = touch.location
                self.configuration.updateSelection(
                    coordinate: Coordinate(
                        x: touch.location.x - halfWidth,
                        y: touch.location.y - halfHeight
                    )
                )
            })
            .onEnded({ touch in
                self.touchPoint = touch.location
                self.configuration.endSelection()
            })
    }

    var body: some View {
        Rectangle()
            .aspectRatio(0.66667, contentMode: .fit)
            .foregroundColor(Color("Clearing"))
            .gesture(touches)
            .overlay(
                Text("(\(Int(touchPoint.x)), \(Int(touchPoint.y)))")
                    .font(.title)
                    .colorInvert()
                )
            .overlay(
                Circle()
                    .fill(Color.green)
                    .frame(width: 15, height: 15, alignment: .center)
                    .position(touchPoint)
                )
            .overlay(Perimeter(anchors: configuration.anchors))
            .overlay(AnchorView(anchors: configuration.anchors))
    }
}

struct Clearing_Previews: PreviewProvider {
    static var previews: some View {
        Clearing()
    }
}
