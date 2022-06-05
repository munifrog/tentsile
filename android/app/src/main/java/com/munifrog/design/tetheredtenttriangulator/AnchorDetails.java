package com.munifrog.design.tetheredtenttriangulator;

class AnchorDetails {
   double[] distances = new double[3];
   Symbol[] symbols = new Symbol[3];

   AnchorDetails(double[] distances, Symbol[] symbols) {
      for (int i = 0; i < 3; i++) {
         this.distances[i] = distances[i];
         this.symbols[i] = symbols[i];
      }
   }
}
