package com.munifrog.design.tetheredtenttriangulator;

// Impossible indicates when the platform is too close to the anchor point.
// Scarce indicates when the tether will be too short to wrap around a tree at recommended diameter
// Tricky indicates when the setup is possible, but requires further understanding (e.g., when the
// ratchet is too close to the tree, you can still make the setup work by attaching the ratchet and
// strap to the platform tip and wrap both around the tree like a tear-drop shape).
// Safe indicates no issues or a typical working setup.
//
// We use the same enumerations for the symbol drawn and the level of symbol verbosity desired.
// This is because there is a direct inverse relation between the symbol in use and how many symbols
// the user wants to see. We want to sense the least restrictive scenarios (i.e., safe) to display
// the most restrictive (i.e., impossible). We could define a "quiet" option after "impossible", but
// since "safe" draws nothing (and hence has no verbosity), we can shift the verbosity for all these
// down by one, making "impossible" the "quiet" option. This is not intuitive, hence this message.
//
// In effect, the app will draw symbols greater than the stored verbosity:
//   safe(0) can draw tricky(1), scarce(2), or impossible(3)
//   tricky(1) can draw scarce(2) or impossible(3)
//   scarce(2) can draw impossible(3)
//   impossible draws nothing (i.e., quiet)
//
enum Symbol {
    safe, // least restricted; most sensitive; most verbose
    tricky,
    scarce,
    impossible; // most restricted; least sensitive; least verbose

    public static Symbol next(Symbol symbol) {
        switch (symbol) {
            case safe:
                return tricky;
            case tricky:
                return scarce;
            default: // scarce, impossible
                return impossible;
        }
    }

    public static Symbol prev(Symbol symbol) {
        switch (symbol) {
            case impossible:
                return scarce;
            case scarce:
                return tricky;
            default: // safe, tricky
                return safe;
        }
    }

    public static Symbol getMoreRestrictiveSymbol(Symbol left, Symbol right) {
        if (left.compareTo(right) < 0) {
            return right;
        } else {
            return left;
        }
    }
}
