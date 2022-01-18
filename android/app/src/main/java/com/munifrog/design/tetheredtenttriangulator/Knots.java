package com.munifrog.design.tetheredtenttriangulator;

enum Symbol {
    safe,
    tricky,
    scarce,
    impossible
}

class Knots {
    float [][] knots;
    Symbol symbol;

    Knots(float [][] knots, Symbol symbol) {
        this.knots = knots;
        this.symbol = symbol;
    }
}
