//
//  ViewSize.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 9/17/21.
//
//  https://swiftuirecipes.com/blog/getting-size-of-a-view-in-swiftui

import SwiftUI

struct ViewSizeModifier: ViewModifier {
    func body(content: Content) -> some View {
        content.background(GeometryReader { geometry in
            Color.clear.preference(key: SizePreferenceKey.self, value: geometry.size)
        })
    }
}

struct SizePreferenceKey: PreferenceKey {
    static var defaultValue: CGSize = .zero
    static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
        value = nextValue()
    }
}

extension View {
    func measureSize(perform action: @escaping (CGSize) -> Void) -> some View {
        self.modifier(ViewSizeModifier())
            .onPreferenceChange(SizePreferenceKey.self, perform: action)
    }
}
