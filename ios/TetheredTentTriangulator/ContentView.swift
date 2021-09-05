//
//  ContentView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/5/21.
//

import SwiftUI

struct ContentView: View {
    var body: some View {
        VStack {
            PlatformPicker(platform: .stingray)
            HSlider()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
