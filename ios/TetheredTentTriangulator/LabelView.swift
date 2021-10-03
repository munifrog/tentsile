//
//  LabelView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 10/3/21.
//

import SwiftUI

struct LabelView: View {
    var value: Float
    var offset_x: Float
    var offset_y: Float
    var a_x: Float
    var a_y: Float
    var b_x: Float
    var b_y: Float
    var body: some View {
        Text(String(format: "%3.1f", value))
            .font(.title)
            .colorInvert()
            .position(
                x: CGFloat(offset_x + (a_x + b_x) / 2),
                y: CGFloat(offset_y + (a_y + b_y) / 2)
            )
    }
}

struct LabelView_Previews: PreviewProvider {
    static var previews: some View {
        LabelView(
            value: 32.57,
            offset_x: 430,
            offset_y: 430,
            a_x: 100,
            a_y: 200,
            b_x: 200,
            b_y: 300
        )
        .colorInvert()
    }
}
