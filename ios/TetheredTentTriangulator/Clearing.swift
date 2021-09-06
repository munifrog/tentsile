//
//  Clearing.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/6/21.
//

import SwiftUI

struct Clearing: View {
    var body: some View {
        Rectangle()
            .aspectRatio(0.66667, contentMode: .fit)
            .foregroundColor(Color("Clearing"))
    }
}

struct Clearing_Previews: PreviewProvider {
    static var previews: some View {
        Clearing()
    }
}
