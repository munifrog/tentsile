//
//  DraggableView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 6/2/22.
//

import SwiftUI

// https://www.hackingwithswift.com/books/ios-swiftui/moving-views-with-draggesture-and-offset
struct DraggableView: View {
    @Binding var isPresented: Bool
    @State private var offset = CGSize.zero

    private let transition: CGFloat = 100

    var swipe: some Gesture {
        DragGesture()
            .onChanged({ swipe in
                self.offset = swipe.translation
            })
            .onEnded({ swipe in
                if abs(offset.width) > transition {
                    isPresented.toggle()
                } else {
                    offset = .zero
                }
            })
    }

    var body: some View {
        Rectangle()
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
            .background(Color.white)
            .overlay(
                FAQListView()
            )
            .offset(x: offset.width, y: 0)
            .opacity(2 - Double(abs(offset.width / transition)))
            .gesture(swipe)
    }
}

struct DraggableView_Previews: PreviewProvider {
    static var previews: some View {
        DraggableView(isPresented: .constant(true))
    }
}
