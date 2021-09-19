//
//  Clearing.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/6/21.
//

import SwiftUI

struct Clearing: View {
    @State var dimensions = CGPoint(x:0, y:0)
    @State var touchPoint = CGPoint(x:0, y:0)
    @State var configuration = Configuration()

    var touches: some Gesture {
        // https://stackoverflow.com/a/60219793
        DragGesture(minimumDistance: 0)
            .onChanged({ touch in
                self.touchPoint = touch.location
                self.configuration.updateSelection(
                    touch: touchPoint - dimensions
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
            .measureSize(perform: {
                self.dimensions = $0 / 2
                configuration.setLimits(
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
            .overlay(Perimeter(config: configuration))
            .overlay(TetherView(config: configuration))
            .overlay(PerimeterLabels(config: configuration))
            .overlay(AnchorView(config: configuration))
            .gesture(touches)
    }
}

struct Clearing_Previews: PreviewProvider {
    static var previews: some View {
        Clearing()
    }
}
