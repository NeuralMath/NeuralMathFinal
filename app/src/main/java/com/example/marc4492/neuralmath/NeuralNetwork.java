package com.example.marc4492.neuralmath;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.io.IOException;

/**
 * Class qui contient le réseau de neurones et qui peut obtenir la valeur de sortie du réseau
 *
 * @author Marc4492
 * 10 février 2017
 */
public class NeuralNetwork {

    private SQLiteDatabase database;

    private final int INPUT;
    private final int HIDDEN;
    private final int OUTPUT;

    private final double[][] weightsItoH;
    private final double[][] weightsHtoO;

    private final String tableNameItoH = "weights_i_to_h";
    private final String tableNameHtoO = "weights_h_to_o";

    private final int[] inputValues;
    private final Neuron[][] reseau;

    private final double trainingRate;

    private boolean oneArrayDone = false;
    private OnNetworkReady listener;

    /**
     * Constructeur du réseau, création des neurones
     *
     * @param inputLayer    Nombre de neurons dans la première couche
     * @param hiddenLayer   Nombre de neurons dans la deuxième couche
     * @param outputLayer   Nombre de neurons dans la troisième couche
     * @param training      Vitesse de l'apprentisage
     * @param db            Database pour les données du reseau
     * @param l             Listener pour avoir l'état du reseau
     * @throws IOException S'il ya des problème de lecture des fichiers
     */
    public NeuralNetwork(int inputLayer, int hiddenLayer, int outputLayer, double training, SQLiteDatabase db, final OnNetworkReady l) throws IOException {
        database = db;
        listener = l;

        INPUT = inputLayer;
        HIDDEN = hiddenLayer;
        OUTPUT = outputLayer;
        trainingRate = training;

        inputValues = new int[INPUT + 1];
        //Set bias commun
        inputValues[INPUT] = 1;

        //Creation des tableaux de weight avec les +1 pour les bias
        weightsItoH = new double[INPUT + 1][HIDDEN + 1];
        weightsHtoO = new double[HIDDEN + 1][OUTPUT];

        updateWeights();

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

    public void updateWeights() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                readData(weightsItoH, tableNameItoH);
                if (oneArrayDone)
                    listener.ready(true);
                else
                    oneArrayDone = true;
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                readData(weightsHtoO, tableNameHtoO);
                if (oneArrayDone)
                    listener.ready(true);
                else
                    oneArrayDone = true;
            }
        }).start();
    }

    /**
     * Obtenir la valeur du réseau
     *
     * @param input Le tableau de double pour les entrées
     * @return La position de la neurone avec la plus haute valeur
     */
    public int getAnwser(int[] input) {
        System.out.println(input.length);
        int posMax = 0;

        //Copier les valeurs sans touché à la valeur de bias
        System.arraycopy(input, 0, inputValues, 0, INPUT);

        computes();

        //Trouver la position de la plus grande valeur d'output
        for (int i = 1; i < reseau[1].length; i++)
            if (reseau[1][i].getOutput() > reseau[1][posMax].getOutput())
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
        for (int i = 0; i < reseau[0].length - 1; i++) {
            //Calculer la valeur d'entré d'une neurone
            for (int j = 0; j < inputValues.length; j++)
                somme += inputValues[j] * weightsItoH[j][i];

            //Calculer la valeur de la neurone
            reseau[0][i].computes(somme);
            somme = 0;
        }

        //Exécuter les neurones du deuxième étage et passer les valeurs à l'autre couche
        for (int i = 0; i < reseau[1].length; i++) {
            //Calculer la valeur d'entré d'une neurone
            for (int j = 0; j < reseau[0].length; j++)
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
     * @param trainingSet Les données d'entrée de chaque neurone pour tous les exemple
     * @param resultat    La valeurs de chaques neurones d'output pour chaque exemple
     * @throws IOException S'il y a un problème d'écriture dans un fichier
     */
    public void trainAll(int[][] trainingSet, int[][] resultat) throws IOException {
        for (int i = 0; i < trainingSet.length; i++) {
            //Copier les valeurs sans touché à la valeur de bias
            System.arraycopy(trainingSet[i], 0, inputValues, 0, INPUT);
            //Calculer les valeurs
            computes();

            deepLearningAlgo(resultat[i]);
        }

        //Enregistrement des données
        saveData(weightsItoH, tableNameItoH);
        saveData(weightsHtoO, tableNameHtoO);
    }

    /**
     * Entrainer le réseau une fois
     *
     * @param trainingSet Les données d'entrée de chaque neurone pour un exemple
     * @param resultat    La valeurs de chaques neurones d'output pour l'exemple en question
     * @throws IOException S'il y a un problème d'écriture dans un fichier
     */
    public void trainOnce(int[] trainingSet, int[] resultat) throws IOException {
        //Copier les valeurs sans touché à la valeur de bias
        System.arraycopy(trainingSet, 0, inputValues, 0, INPUT);
        //Calculer les valeurs
        computes();

        deepLearningAlgo(resultat);

        //Enregistrement des données
        saveData(weightsItoH, tableNameItoH);
        saveData(weightsHtoO, tableNameHtoO);
    }

    /**
     * L'algorithme qui effectue l'apprentisage
     *
     * @param resultat Le tableau des valeure supposé de l'output layer
     */
    private void deepLearningAlgo(int resultat[]) {
        //Valeurs intermediaire
        double[] values = new double[OUTPUT];

        //Stocastic gradient descent  HIDDEN -> OUTPUT
        for (int i = 0; i <= HIDDEN; i++) {
            for (int j = 0; j < OUTPUT; j++) {
                values[j] = (reseau[1][j].getOutput() - resultat[j]) * reseau[1][j].getOutput() * (1 - reseau[1][j].getOutput());
                weightsHtoO[i][j] -= trainingRate * values[j] * reseau[0][i].getOutput();
            }
        }

        //Stocastic gradient descent  INPUT -> HIDDEN
        for (int k = 0; k <= INPUT; k++) {
            for (int i = 0; i <= HIDDEN; i++) {
                double sommation = 0;

                for (int j = 0; j < OUTPUT; j++)
                    sommation += values[j] * weightsHtoO[i][j];

                weightsItoH[k][i] -= trainingRate * sommation * reseau[0][i].getOutput() * (1 - reseau[0][i].getOutput()) * inputValues[k];
            }
        }
    }


    //********************* Fichiers et database pour les variables du réseaux *********************\\


    /**
     * Écriture d'un tableau deux dimension dans une base de données
     *
     * @param values            Le tableau à écrire
     * @param nameTable         Nom de la table de la BD
     */
    private void saveData(double[][] values, String nameTable)
    {
        //From
        //http://stackoverflow.com/a/19637484
        String sql = "INSERT INTO " + nameTable + " VALUES(valeur) values (?);";

        database.beginTransaction();
        SQLiteStatement stmt = database.compileStatement(sql);

        for (double[] val : values) {
            for (double innerVal : val) {
                stmt.bindDouble(1, innerVal);
                stmt.executeInsert();
                stmt.clearBindings();
            }
        }

        database.setTransactionSuccessful();
        database.endTransaction();
    }

    /**
     * Lecture d'un tableau deux dimension depuis une base de données.
     *
     * @param array                     Le tableau à lire
     * @param tableName                 Nom de la table à lire
     * @throws IOException              S'il y a des problème de lecture dans le fichier ou que le fichier n'a pas les bonnes tailles. (nbs lignes/colonnes)
     * @throws NumberFormatException    Si le texte n'est pas en double
     */
    private void readData(double[][] array, String tableName) throws NumberFormatException {
        int i = 0;
        Cursor result = database.rawQuery("Select * from " + tableName, null);

        while(result.moveToNext())
        {
            array[(int) Math.floor(i/array[0].length)][i%array[0].length] = Double.parseDouble(result.getString(0));
            i++;
        }
        result.close();
    }

    public interface OnNetworkReady
    {
        void ready(boolean ready);
    }
}