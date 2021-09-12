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

    var body: some View {
        Rectangle()
            .aspectRatio(0.66667, contentMode: .fit)
            .foregroundColor(Color("Clearing"))
            .gesture(
                // https://stackoverflow.com/a/60219793
                DragGesture(minimumDistance: 0)
                    .onChanged({ (touch) in
                        self.touchPoint = touch.location
                    })
                    .onEnded({ (touch) in
                        self.touchPoint = touch.location
                    })
            )
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
            .overlay(
                AnchorView(anchors: configuration.anchors)
            )
    }
}

struct Clearing_Previews: PreviewProvider {
    static var previews: some View {
        Clearing()
    }
}
