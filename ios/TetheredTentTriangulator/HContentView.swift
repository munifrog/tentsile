//
//  HContentView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/23/21.
//

import SwiftUI

struct HContentView: View {
    @Binding var config: Configuration

    private let pickerHeight = CGFloat(30.0)
    private let sliderWidth = CGFloat(50.0)

    var body: some View {
        let limits = config.getLimits()
        VStack {
            HStack {
                Text("Tethered Tent Triangulator")
                    .font(.headline)
                    .colorInvert()
                Spacer()
                MenuView(config: $config)
            }
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
                        Group {
                            PlatformPicker(platform: $config.platform)
                            PlatformRotator(config: $config)
                                .padding(.trailing)
                        }
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
    static var previews: some View {
        HContentView(config: .constant(Configuration()))
    }
}
