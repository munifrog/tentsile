//
//  VContentView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/23/21.
//

import SwiftUI

struct VContentView: View {
    @Binding var config: Configuration
    @Binding var showFaq: Bool

    var body: some View {
        VStack(spacing:0) {
            HStack {
                Text("Tentsile Triangulator")
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
            PlatformPicker(config: $config)

            VStack {
                HSlider(position: $config.scale)
                Spacer()
                Clearing(config: $config)
            }
            .overlay(FineTuneSliderView(config: $config))
        }
    }
}

struct VContentView_Previews: PreviewProvider {
    @State private static var config = Configuration(
        Coordinate(x: 160.0, y: 217.0)
    )

    static var previews: some View {
        VContentView(
            config: $config,
            showFaq: .constant(false)
        )
    }
}
