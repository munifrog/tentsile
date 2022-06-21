//
//  HContentView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/23/21.
//

import SwiftUI

struct HContentView: View {
    @Binding var config: Configuration
    @Binding var showFaq: Bool

    private let pickerHeight = CGFloat(30.0)
    private let sliderWidth = CGFloat(50.0)

    var body: some View {
        let limits = config.getLimits()
        VStack(spacing: 0) {
            HStack {
                Text("Tethered Tent Triangulator")
                    .font(.headline)
                    .foregroundColor(Color.white)
                Spacer()
                MenuView(
                    config: $config,
                    showFaq: $showFaq
                )
            }
            .padding(.vertical, 16)
            .padding(.horizontal)
            .background(Color("ThemePrimary"))
            ZStack {
                HStack {
                    Clearing(config: $config)
                    VSliderView(
                        position: $config.scale,
                        height: CGFloat(limits.y) * 2 - pickerHeight,
                        width: sliderWidth,
                        offsetY: pickerHeight / 2
                    )
                }
                VStack {
                    HStack {
                        Spacer()
                        PlatformPicker(config: $config)
                        .frame(height: pickerHeight)
                        .background(Color.clear)
                    }
                    Spacer()
                }
            }
        }
    }
}

struct HContentView_Previews: PreviewProvider {
    @State private static var config = Configuration(
        Coordinate(x: 120.0, y: 217.0)
    )

    static var previews: some View {
        HContentView(
            config: $config,
            showFaq: .constant(false)
        )
    }
}
