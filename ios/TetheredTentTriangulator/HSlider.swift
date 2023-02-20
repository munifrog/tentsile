//
//  HSlider.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/5/21.
//

import SwiftUI

struct HSlider: View {
    @Binding var position: Float
    @State private var isEditing = false
    var start: Float = 1
    var finish: Float = 100

    var body: some View {
        VStack {
            Slider(
                value: $position,
                in: start...finish,
                step: 1,
                onEditingChanged: { editing in
                    isEditing = editing
                }
            )
            .padding(.horizontal)
            .accentColor(isEditing ? .blue : .green)
        }
    }
}

struct HSlider_Previews: PreviewProvider {
    static var previews: some View {
        HSlider(position: .constant(25.0))
    }
}
