//
//  Clearing.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/6/21.
//

import SwiftUI

struct Clearing: View {
    @Binding var config: Configuration

    @State private var dimensions = CGPoint(x:0, y:0)
    @State private var touchPoint = CGPoint(x:0, y:0)

    var touches: some Gesture {
        // https://stackoverflow.com/a/60219793
        DragGesture(minimumDistance: 0)
            .onChanged({ touch in
                self.touchPoint = touch.location
                self.config.updateSelection(
                    touch: touchPoint - dimensions
                )
            })
            .onEnded({ touch in
                self.touchPoint = touch.location
                self.config.endSelection()
            })
    }

    var body: some View {
        Rectangle()
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
            .foregroundColor(Color("Clearing"))
            .measureSize(perform: {
                self.dimensions = $0 / 2
                config.setLimits(
                    screen: Coordinate(coordinate: dimensions)
                )
            })
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
            .overlay(Perimeter(config: $config))
            .overlay(TetherView(config: $config))
            .overlay(PlatformView(config: $config))
            .overlay(PerimeterLabels(config: $config))
            .overlay(AnchorView(config: $config))
            .overlay(TetherIconView(config: $config))
            .gesture(touches)
    }
}

struct Clearing_Previews: PreviewProvider {
    static var previews: some View {
        Clearing(config: .constant(Configuration(anchors: Anchors())))
    }
}
