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
            Q: "How do I close this FAQ page?",
            A: "Swipe left or right to dismiss this page.\n\nIf your swipe gesture is more up or down than left or right you will engage the scroll feature instead. In that case try swiping again."),
        FAQView(
            Q: "How accurately do I need to measure?",
            A: "The App itself is accurate to one centimeter or 3/8 imperial inches.\n\nYou can use the app to get the measurements sufficiently close to then be able to align the tent according to the manufacturers instructions."),
        FAQView(
            Q: "How do I get the app to match my measurements?",
            A: "When you tap the label halfway between the trees, the app should open an interface for fine-tuning its value.\n\nDo not worry about entering a more precise value than this app allows. Less precise measurements are likely close enough for what you need to setup. Use this App to get close enough to follow the manufacturers instructions."),
        FAQView(
            Q: "What if my measurement goes off the screen?",
            A: "Depending on the issue, you have a few options. You can ...\n\n... drag the tether intersection point (if visible) to move all the anchors simultaneously.\n\n... rotate the configuration (when tent is drawn) by dragging a point that is not part of the configuration.\n\n... tilt the device and modify anchors in that orientation.\n\n... reset the anchors using the menu option.\n\n... change the scale of the units using the slider."),
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
            Q: "Where can I find the level tool?",
            A: "Look for \"Level\" in the menu."),
        FAQView(
            Q: "What does the orange triangle indicate?",
            A: "The orange triangle indicates that setup is possible, but involves a non-intuitive \"trick\".\n\nWith the ratchet detached from the tent, simultaneously pass the ratchet through the strap loop and the strap through the ratchet loop to cinch them together. Then pass the strap through the tent D-ring, around the tree, and into the ratchet. Tighten as usual.\n\nAlternatively, with the ratchet attached to the tent D-ring, pass the ratchet through the strap loop. Then wrap the strap around the tree and into the ratchet. Tighten as usual."),
        FAQView(
            Q: "What does the yellow triangle indicate?",
            A: "The yellow triangle indicates that you will not have sufficient strap length to wrap around the tree according to recommendations.\n\nSetup using trees requires an extension or tree-protector strap for the tree with this symbol over it."),
        FAQView(
            Q: "What does the red circle and slash indicate?",
            A: "The red circle and slash indicates that (ideal) setup is not possible.\n\nThe tree with the red circle and slash over it is too close."),
        FAQView(
            Q: "What are the small circles shown on the tethers?",
            A: "The small circles are supposed to represent transition points, where the tethers change. For example, the tent corners, ratchets, and straps."),
        FAQView(
            Q: "How can I use a fourth tree with this app?",
            A: "If you have an extra ratchet and strap, you can use a fourth tree to redirect the how one of the tethers aligns. To use this technique with this app follow these steps:\n  (1) Measure the distance between two of the trees, placing their markers accordingly.\n  (2) Place the third marker so that its tether passes somewhere between the third and fourth trees. (Note that the position of the third marker does not matter very much as long as the tether direction is correct.)\n  (3) Attach the straps to the first two trees using the measurements computed with the app.\n  (4) Loosely attach the third strap to the third tree.\n  (5) Loop the fourth ratchet around the third strap.\n  (6) Attach the fourth strap to the fourth tree\n  (7) Finally, tighten the third and fourth straps until all tethers are correctly aligned with the tent or hammock."),
        FAQView(
            Q: "Do I still need to use the tabs on the sides of my tent?",
            A: "That would be best. This app tries to get you close to the point where you can then align the tethers with the sight-indicator tabs on the sides of the non-equal-sided tents and hammocks.")
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
