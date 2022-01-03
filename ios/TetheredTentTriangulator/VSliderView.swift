//
//  VSliderView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/23/21.
//

import SwiftUI

struct VSliderView: View {
    @State private var isEditing = false
    @Binding var position: Float

    var height: CGFloat = 200.0
    var width: CGFloat = 40.0
    var offsetX: CGFloat = 0.0
    var offsetY: CGFloat = 0.0

    private let padding = CGFloat(20.0) // top and bottom combined

    var body: some View {
        ZStack {
            let frameH = height - padding
            let frameW = width
            Rectangle()
                .frame(width: frameW,
                       height: frameH)
                .foregroundColor(.clear)
                .overlay(
                    Slider(
                        value: $position,
                        in: 1...100,
                        step: 1,
                        onEditingChanged: { editing in
                            isEditing = editing
                        }
                    )
                        .accentColor(isEditing ? .blue : .green)
                        .rotationEffect(Angle(degrees: 270))
                        .frame(width: frameH,
                               height: frameW,
                               alignment: .center)
                        .offset(x: offsetX, y: offsetY)
                )
        }
    }
}

struct VSliderView_Previews: PreviewProvider {
    static var previews: some View {
        VSliderView(
            position: .constant(25.0),
            height: 100
        )
    }
}
