//
//  PlatformRotator.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 10/9/21.
//

import SwiftUI

struct PlatformRotator: View {
    @Binding var config: Configuration

    var body: some View {
        Group {
            if config.getPlatform().rotates,
               let center = config.center {
                Button(action: rotate) {
                    Label("Rotate", systemImage:
                            center.flips ? "arrow.counterclockwise" : "arrow.clockwise")
                        .labelStyle(IconOnlyLabelStyle())
                        .frame(alignment: .center)
                }
                .background(Color.clear)
            }
        }
    }

    func rotate() {
        config.rotate()
    }
}

struct PlatformRotator_Previews: PreviewProvider {
    static var previews: some View {
        PlatformRotator(config: .constant(Configuration()))
    }
}
