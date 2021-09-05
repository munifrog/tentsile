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
    @State var platform: Platform

    var body: some View {
        HStack {
            Picker("\(platform.rawValue)", selection: $platform) {
                ForEach(Platform.allCases.reversed()) { p in
                    Text(p.rawValue).tag(p)
                }
            }
        }
        .padding()
        .pickerStyle(MenuPickerStyle())
    }
}

struct PlatformPicker_Previews: PreviewProvider {
    static var previews: some View {
        PlatformPicker(platform: .stingray)
    }
}
