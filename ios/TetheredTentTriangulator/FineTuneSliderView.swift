//
//  FineTuneSliderView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 2/19/23.
//

import SwiftUI

struct FineTuneSliderView: View {
    @Binding var config: Configuration
    @State private var isEditing = false
    private let FINE_TUNE_ALLOWANCE: Float = 25.0

    var touches: some Gesture {
        // https://stackoverflow.com/a/60219793
        DragGesture(minimumDistance: 0)
            .onEnded({ touch in
                self.config.updateSelection(touch: Coordinate(x:0.0, y:0.0))
            })
    }

    var body: some View {
        if config.isFineTuning() {
            ZStack {
                Rectangle()
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                    .foregroundColor(Color("Dimmed"))
                    .gesture(touches)
                VStack {
                    Text(config.getSelectedPerimeterString())
                        .font(.largeTitle)
                        .foregroundColor(Color("FontPerimeter"))
                    HSlider(
                        position: $config.fineTuneOffset,
                        start: -FINE_TUNE_ALLOWANCE,
                        finish: FINE_TUNE_ALLOWANCE
                    )
                }
            }
        }
    }
}

struct FineTuneSliderView_Previews: PreviewProvider {
    @State private static var config = Configuration(
        Coordinate(x: 160.0, y: 217.0)
    )

    static var previews: some View {
        FineTuneSliderView(
            config: $config
        )
    }
}
