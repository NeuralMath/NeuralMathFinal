package com.example.marc4492.neuralmath;

/**
 * Class qui contient l'exécution d'une neurone selon son input
 *
 * @author Marc4492
 * 10 février 2017
 */
class Neuron {
    private double output;

    /**
     * Constructeur pour les neurones
     * Set l'output à 1.0 pour les neurones de bias
     */
    Neuron() {
        output = 1.0;
    }

    /**
     * Obtenir l'output de la neurone
     *
     * @return  L'output
     */
    double getOutput() {
        return output;
    }

    /**
     * Calculer la valeur de l'output
     *
     * @param input     La valeur de l'input
     */
    void computes(double input) {
        output = 1 / (1 + Math.exp(-input));
    }
}