//
//  FAQView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 5/26/22.
//

import SwiftUI

struct FAQView: View {
    @State var show = false

    let Q: String
    let A: String
    let id = UUID()

    var body: some View {
        // https://stackoverflow.com/a/67188383
        VStack(spacing: 0) {
            Text(Q)
                .font(.system(size: 20))
                .bold()
                .colorInvert()
                .padding(16)
                .frame(maxWidth: .infinity, alignment: .center)
                // https://stackoverflow.com/a/58349215
                .fixedSize(horizontal: false, vertical: true)
                .onTapGesture { show.toggle() }
                .background(Color("Clearing"))
            if show {
                Text(A)
                    .font(.system(size: 16))
                    .padding([.horizontal,.bottom], 24)
                    .padding(.top, 12)
                    .frame(maxWidth: .infinity, alignment: .center)
                    // https://stackoverflow.com/a/58349215
                    .fixedSize(horizontal: false, vertical: true)
                    .background(Color.clear)
            }
            Divider()
                .frame(height: 2, alignment: .center)
                .background(Color.white)
        }
        .background(Color.white)
    }
}

struct FAQView_Previews: PreviewProvider {
    static var previews: some View {
        FAQView(
            show: true,
            Q: "What does the yellow triangle indicate?",
            A: "The yellow triangle indicates that you will not have sufficient strap length to wrap around the tree according to recommendations.\n\nSetup using trees requires an extension or tree-protector strap for the tree with this symbol over it."
        )
    }
}
