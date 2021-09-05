//
//  HSlider.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/5/21.
//

import SwiftUI

struct HSlider: View {
    @State private var position = 25.0
    @State private var isEditing = false

    var body: some View {
        VStack {
            Slider(
                value: $position,
                in: 1...100,
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
        HSlider()
    }
}
