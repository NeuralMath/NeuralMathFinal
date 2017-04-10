package com.example.marc4492.neuralmath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Class qui contient le réseau de neurones et qui peut obtenir la valeur de sortie du réseau
 *
 * @author Marc4492
 * 10 février 2017
 */
public class NeuralNetwork {
    private final int INPUT;
    private final int HIDDEN;
    private final int OUTPUT;

    private final String fileWeightsItoH;
    private final String fileWeightsHtoO;

    private final double[][] weightsItoH;
    private final double[][] weightsHtoO;

    private final int[] inputValues;
    private final Neuron[][] reseau;

    private final double trainingRate;

    /**
     * Constructeur du réseau, création des neurones
     *
     * @param inputLayer                        Nombre de neurons dans la première couche
     * @param hiddenLayer                       Nombre de neurons dans la deuxième couche
     * @param outputLayer                       Nombre de neurons dans la troisième couche
     * @param training                          Vitesse de l'apprentisage
     * @param fileWIH                           Path du fichier pour les poids entre l'input et le hidden layer
     * @param fileWHO                           Path du fichier pour les poids entre le hidden et le output layer
     *
     * @throws IOException                      S'il ya des problème de lecture des fichiers
     */
    public NeuralNetwork(int inputLayer, int hiddenLayer, int outputLayer, double training, String fileWIH, String fileWHO) throws IOException {
        INPUT = inputLayer;
        HIDDEN = hiddenLayer;
        OUTPUT = outputLayer;
        trainingRate = training;

        fileWeightsItoH = fileWIH;
        fileWeightsHtoO = fileWHO;

        inputValues = new int[INPUT + 1];
        //Set bias commun
        inputValues[INPUT] = 1;

        //Creation des tableaux de weight avec les +1 pour les bias
        weightsItoH = new double[INPUT + 1][HIDDEN + 1];
        weightsHtoO = new double[HIDDEN + 1][OUTPUT];

        //Read les valeurs dans les fichiers
        readFile(fileWeightsItoH, weightsItoH);
        readFile(fileWeightsHtoO, weightsHtoO);

        //Création des layer du réseau avec une neurone de plus dans le hidden layer pour le bias
        reseau = new Neuron[][]
                {
                        new Neuron[HIDDEN + 1],
                        new Neuron[OUTPUT]
                };

        //Création des neuronnes
        for (int i = 0; i < reseau.length; i++)
            for (int j = 0; j < reseau[i].length; j++)
                reseau[i][j] = new Neuron();
    }

    /**
     * Obtenir la valeur du réseau
     *
     * @param input     Le tableau de double pour les entrées
     * @return          La position de la neurone avec la plus haute valeur
     */
    public int getAnwser(int[] input)
    {
        System.out.println(input.length);
        int posMax = 0;

        //Copier les valeurs sans touché à la valeur de bias
        System.arraycopy(input, 0, inputValues, 0, INPUT);

        computes();

        //Trouver la position de la plus grande valeur d'output
        for(int i = 1; i < reseau[1].length; i++)
            if(reseau[1][i].getOutput() > reseau[1][posMax].getOutput())
                posMax = i;

        return posMax;
    }

    /**
     * Exectuer le réseau de neurones
     */
    private void computes() {
        double somme = 0;
        //Exécuter les neurones du premier étage et passer les valeurs à l'autre couche.
        //Ne pas exécuter la neurone de bias d'où le -1
        for(int i = 0; i < reseau[0].length-1; i++)
        {
            //Calculer la valeur d'entré d'une neurone
            for(int j = 0; j < inputValues.length; j++)
                somme += inputValues[j] * weightsItoH[j][i];

            //Calculer la valeur de la neurone
            reseau[0][i].computes(somme);
            somme = 0;
        }

        //Exécuter les neurones du deuxième étage et passer les valeurs à l'autre couche
        for(int i = 0; i < reseau[1].length; i++)
        {
            //Calculer la valeur d'entré d'une neurone
            for(int j = 0; j < reseau[0].length; j++)
                somme += reseau[0][j].getOutput() * weightsHtoO[j][i];

            //Calculer la valeur de la neurone
            reseau[1][i].computes(somme);
            somme = 0;
        }
    }



    //********************* Entrainement du réseau *********************\\


    /**
     * Entrainer le réseau
     *
     * @param trainingSet       Les données d'entrée de chaque neurone pour tous les exemple
     * @param resultat          La valeurs de chaques neurones d'output pour chaque exemple
     *
     * @throws IOException      S'il y a un problème d'écriture dans un fichier
     */
    public void trainAll(int[][] trainingSet, int[][] resultat) throws IOException {
        for(int i = 0; i < trainingSet.length; i++) {
            //Copier les valeurs sans touché à la valeur de bias
            System.arraycopy(trainingSet[i], 0, inputValues, 0, INPUT);
            //Calculer les valeurs
            computes();

            deepLearningAlgo(resultat[i]);
        }

        //Enregistrement des données
        writeFile(fileWeightsItoH, weightsItoH);
        writeFile(fileWeightsHtoO, weightsHtoO);
    }

    /**
     * Entrainer le réseau une fois
     *
     * @param trainingSet           Les données d'entrée de chaque neurone pour un exemple
     * @param resultat              La valeurs de chaques neurones d'output pour l'exemple en question
     *
     * @throws IOException          S'il y a un problème d'écriture dans un fichier
     */
    public void trainOnce(int[] trainingSet, int[] resultat) throws IOException {
        //Copier les valeurs sans touché à la valeur de bias
        System.arraycopy(trainingSet, 0, inputValues, 0, INPUT);
        //Calculer les valeurs
        computes();

        deepLearningAlgo(resultat);

        //Enregistrement des données
        writeFile(fileWeightsItoH, weightsItoH);
        writeFile(fileWeightsHtoO, weightsHtoO);
    }

    /**
     * L'algorithme qui effectue l'apprentisage
     *
     * @param resultat Le tableau des valeure supposé de l'output layer
     */
    private void deepLearningAlgo(int resultat[])
    {
        //Stocastic gradient descent  HIDDEN -> OUTPUT
        for(int i = 0; i <= HIDDEN; i++)
            for(int j = 0; j < OUTPUT; j++)
                weightsHtoO[i][j] -= trainingRate * (reseau[1][j].getOutput() - resultat[j]) * reseau[1][j].getOutput() * (1 - reseau[1][j].getOutput()) * reseau[0][i].getOutput();

        //Stocastic gradient descent  INPUT -> HIDDEN
        for(int k = 0; k <= INPUT; k++)
        {
            for(int i = 0; i <= HIDDEN; i++)
            {
                double sommation = 0;

                for(int j = 0; j < OUTPUT; j++)
                    sommation += (reseau[1][j].getOutput() - resultat[j]) * reseau[1][j].getOutput() * (1 - reseau[1][j].getOutput()) * weightsHtoO[i][j];

                weightsItoH[k][i] -= trainingRate * sommation * reseau[0][i].getOutput() * (1-reseau[0][i].getOutput()) * inputValues[k];
            }
        }
    }



    //********************* Fichier pour les variables du réseaux *********************\\





    /**
     * Remettre toutes les valeurs à 1.0 et les écrire dans le fichier
     *
     * @param inputLayer                        Nombre de neurons dans la première couche
     * @param hiddenLayer                       Nombre de neurons dans la deuxième couche
     * @param outputLayer                       Nombre de neurons dans la troisième couche
     * @param fileWIH                           Path du fichier pour les poids entre l'input et le hidden layer
     * @param fileWHO                           Path du fichier pour les poids entre le hidden et le output layer
     *
     * @throws IOException                      S'il ya des problème de lecture de fichier
     */
    public void resetDatas(int inputLayer, int hiddenLayer, int outputLayer, String fileWIH, String fileWHO) throws IOException
    {
        //Reset des tableaus de weight incluant les bias
        double[][] weightIH = new double[inputLayer+1][hiddenLayer+1];
        double[][] weightHO = new double[hiddenLayer+1][outputLayer];

        //Affectation des variables de facon random entre [0.5, -0.5]
        for(int i = 0; i < weightHO.length; i++)
            for(int j = 0; j < weightHO[i].length; j++)
                weightHO[i][j] = Math.random() - 0.5;

        for(int i = 0; i < weightIH.length; i++)
            for(int j = 0; j < weightIH[i].length; j++)
                weightIH[i][j] = Math.random() - 0.5;

        //Écriture dans les fichiers
        writeFile(fileWHO, weightHO);
        writeFile(fileWIH, weightIH);
    }

    /**
     * Écriture d'un tableau deux dimension dans un fichier texte. (Créer s'il n'existe pas)
     *
     * @param filePath          La direction du fichier
     * @param array             Le tableau à écrire
     *
     * @throws IOException      S'il y a des problème d'écriture dans le fichier
     */
    private void writeFile(String filePath, double[][] array) throws IOException
    {
        //Creation du fichier s'il n'existe pas
        File file = new File(filePath);
        if(!file.exists())
            file.createNewFile();

        //Écriture sou forme de tableau : [...,...,...] séparé par des \n entre les dimensions
        StringBuilder sb = new StringBuilder();
        for(double[] values : array)
            sb.append(Arrays.toString(values)).append("\r\n");

        //Écriture dans le fichier
        FileOutputStream writer = new FileOutputStream(file, false);
        writer.write(sb.toString().getBytes());
        writer.close();
    }

    /**
     * Lecture d'un tableau deux dimension depuis un fichier texte.
     *
     * @param filePath                      La direction du fichier
     * @param array                         Le tableau à lire
     * @throws IOException                  S'il y a des problème de lecture dans le fichier ou que le fichier n'a pas les bonnes tailles. (nbs lignes/colonnes)
     * @throws NumberFormatException        Si le texte n'est pas en double
     */
    private void readFile(String filePath, double[][] array) throws IOException, NumberFormatException
    {
        //Obtention du fichier
        File file = new File(filePath);
        if(!file.exists())
            throw new IOException("The file isn't valid");

        //Facon efficace de lire dans un fichier texte
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader reader = new BufferedReader(isr);

        StringBuilder sb = new StringBuilder();

        //Lecture jusqu'à la fin du fichier
        String line;
        while((line = reader.readLine()) != null)
            sb.append(line).append("\r\n");

        reader.close();
        isr.close();
        fis.close();

        //Cretation des tableaux selon le fichier
        String[] lines = sb.toString().split("\r\n");
        String[][] linesElements = new String[lines.length][];

        if(lines.length != array.length)
            throw new IOException("Pas le bon nombre de lignes dans le ficher.");

        //Lecture de chaque élément du tableau deux dimension en String
        for(int i = 0; i < lines.length-1; i++)
        {
            linesElements[i] = lines[i].trim().replace("[", "").replace("]", "").split(",");
            if(linesElements[i].length != array[i].length)
                throw new IOException("Pas le bon nombre d'élements dans la ligne " + i + ".");
        }

        //Convertion en double[][]
        for(int i = 0; i < lines.length-1; i++)
            for(int j = 0; j < linesElements[i].length; j++)
                array[i][j] = Double.parseDouble(linesElements[i][j]);
    }
}