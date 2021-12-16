//
//  AnchorIconView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 12/14/21.
//

import SwiftUI

struct AnchorIconView: View {
    var icon: AnchorIcon
    
    var body: some View {
        switch icon {
        case AnchorIcon.impossible:
            Image(systemName: "nosign")
                .resizable()
                .scaledToFit()
                .background(Color.clear)
                .foregroundColor(Color.red)
        case AnchorIcon.warning:
            Image(systemName: "exclamationmark.triangle.fill")
                .resizable()
                .scaledToFit()
                .background(Color.clear)
                .foregroundColor(Color.yellow)
        case AnchorIcon.tricky:
            Image(systemName: "exclamationmark.triangle.fill")
                .resizable()
                .scaledToFit()
                .background(Color.clear)
                .foregroundColor(Color.orange)
        default:
            EmptyView()
        }
    }
}

struct AnchorIconView_Previews: PreviewProvider {
    static var previews: some View {
        AnchorIconView(icon: AnchorIcon.warning)
    }
}
