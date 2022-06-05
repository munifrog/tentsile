//
//  FAQListView.swift
//  TetheredTentTriangulator
//
//  Created by Karl Arthur on 5/26/22.
//

import SwiftUI

struct FAQListView: View {

    @State private var faqs: [FAQView] = [
        FAQView(
            Q: "How accurately do I need to measure?",
            A: "The App itself is accurate to one decimeter or imperial foot.\n\nYou can use the app to get the measurements sufficiently close to then be able to align the tent according to the manufacturers instructions."
        ),
        FAQView(
            Q: "What if my measurement goes off the screen?",
            A: "Depending on the issue, you have a few options. You can ...\n\n... drag the tether intersection point (if visible) to move all the anchors simultaneously.\n\n... tilt the device and modify anchors in that orientation.\n\n... reset the anchors using the menu option.\n\n... change the scale of the units using the slider."),
        FAQView(
            Q: "Do I need to know how thick the tree is?",
            A: "You do not need to know where the center of the tree is. Instead, imagine a knot on the surface of each tree about where the tent tether would attach. Measure the distance from the imaginary knot on one tree to the imaginary knot on the next anchoring tree, etc."),
        FAQView(
            Q: "What am I measuring?",
            A: "You are measuring the distances between approximate locations of the knots."),
        FAQView(
            Q: "Why is my tent not in the list?",
            A: "Try dragging the list to see tents that are not in view. Otherwise, if it is a newer product, we may need to add it. In the meantime try using a tent or hammock with a similar \"footprint\" as yours."),
        FAQView(
            Q: "What tools will I need?",
            A: "It helps to have a measuring tool handy, such as measuring tape or laser measuring tool. But, you could perform the measurements using consistently-spaced units, such as equally-spaced knots in a (low-stretch) string, sufficiently consistent paces, or even the straps that come with the tent. The point here is to measure using consistent units."),
        FAQView(
            Q: "What does the orange triangle indicate?",
            A: "The orange triangle indicates that setup is possible, but involves a non-intuitive \"trick\".\n\nWrap the strap around the tree, as you usually do. Pass the ratchet, rather than the loose end of the strap, through the strap loop. Then tighten the strap as usual."),
        FAQView(
            Q: "What does the yellow triangle indicate?",
            A: "The yellow triangle indicates that you will not have sufficient strap length to wrap around the tree according to recommendations.\n\nSetup using trees requires an extension or tree-protector strap for the tree with this symbol over it."),
        FAQView(
            Q: "What does the red X indicate?",
            A: "The red X indicates that (ideal) setup is not possible.\n\nThe tree with the red X over it is too close."),
        FAQView(
            Q: "How do I close this FAQ page?",
            A: "Swipe left or right to dismiss this page.\n\nIf your swipe gesture is more up or down than left or right you will engage the scroll feature instead. In that case try swiping again.")
    ]

    var body: some View {
        ScrollView {
            // https://stackoverflow.com/a/67188383
            VStack(spacing: 0) {
                ForEach(faqs, id: \.id) { faq in
                    faq
                }
            }
        }
        .background(Color.white)
    }
}

struct FAQListView_Previews: PreviewProvider {
    static var previews: some View {
        FAQListView()
    }
}
