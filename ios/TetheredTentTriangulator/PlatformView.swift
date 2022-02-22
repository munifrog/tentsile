//
//  PlatformView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 10/2/21.
//

import SwiftUI

struct PlatformView: View {
    var setup: DrawableSetup

    var body: some View {
        HStack {
            if setup.getCanDrawPlatform() {
                setup.getPlatformPath()
                    .asPathView()
                    .foregroundColor(Color("Platform"))
            } else {
                EmptyView()
            }
        }
    }
}

struct PlatformView_Previews: PreviewProvider {
    private static var config = Configuration()

    static var previews: some View {
        PlatformView(
            setup: config.getDrawableSetup()
        )
    }
}
