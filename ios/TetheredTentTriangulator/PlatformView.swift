//
//  PlatformView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 10/2/21.
//

import SwiftUI

struct PlatformView: View {
    @Binding var config: Configuration

    var body: some View {
        HStack {
            if let _ = config.center {
                config.getPath().asPathView()
            } else {
                EmptyView()
            }
        }
    }
}

struct PlatformView_Previews: PreviewProvider {
    static var previews: some View {
        PlatformView(config: .constant(Configuration(anchors: Anchors())))
    }
}
