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
            .overlay(Perimeter(config: $config))
            .overlay(TetherView(setup: config.getDrawableSetup()))
            .overlay(PlatformView(setup: config.getDrawableSetup()))
            .overlay(PerimeterLabels(setup: config.getDrawableSetup(), units: config.units))
            .overlay(AnchorView(config: $config))
            .overlay(TetherIconView(setup: config.getDrawableSetup(), symbols: config.symbols))
            .overlay(PlatformLabelsView(setup: config.getDrawableSetup(), units: config.units))
            .gesture(touches)
    }
}

struct Clearing_Previews: PreviewProvider {
    @State private static var config = Configuration(
        Coordinate(x: 160.0, y: 217.0)
    )

    static var previews: some View {
        Clearing(config: $config)
    }
}
