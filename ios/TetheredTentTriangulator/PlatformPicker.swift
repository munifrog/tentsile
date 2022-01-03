//
//  PlatformPicker.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/5/21.
//

import SwiftUI

enum Platform: String, CaseIterable, Identifiable {
    case connect = "Connect Tent"
    case duo = "Duo Hammock"
    case flite = "Flite Tent"
    case stingray = "Stingray Tent"
    case t_mini = "T-Mini Hammock"
    case trillium = "Trillium Hammock"
    case trillium_xl = "Trillium XL Hammock"
    case trilogy = "Trilogy Tent"
    case una = "Una Tent"
    case universe = "Universe Tent"
    case vista = "Vista Tent"

    var id: String { self.rawValue }
}

struct PlatformPicker: View {
    @Binding var config: Configuration

    var body: some View {
        HStack {
            Picker("\(config.platform.rawValue)", selection: $config.platform) {
                ForEach(Platform.allCases) { p in
                    Text(p.rawValue).tag(p)
                }
            }
            .pickerStyle(MenuPickerStyle())
            PlatformRotator(config: $config)
        }
        .padding(.horizontal)
        .frame(height: 30, alignment: .center)
    }
}

struct PlatformPicker_Previews: PreviewProvider {
    static var previews: some View {
        PlatformPicker(config: .constant(Configuration()))
    }
}
