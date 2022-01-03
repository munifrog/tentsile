//
//  VContentView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/23/21.
//

import SwiftUI

struct VContentView: View {
    @Binding var config: Configuration

    var body: some View {
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

            PlatformPicker(config: $config)

            VStack {
                HSlider(position: $config.scale)
                Spacer()
                Clearing(config: $config)
            }
        }
    }
}

struct VContentView_Previews: PreviewProvider {
    static var previews: some View {
        VContentView(config: .constant(Configuration()))
    }
}
