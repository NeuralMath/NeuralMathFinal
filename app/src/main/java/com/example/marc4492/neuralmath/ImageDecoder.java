package com.example.marc4492.neuralmath;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class qui contient le réseau de neurones et qui obtient l'équation en string
 *
 * @author Marc4492
 * 10 février 2017
 */

public class ImageDecoder {
    private ArrayList<MathChar> listChar;
    private NeuralNetwork network;
    private String[] charList;

    private int totalWidth;
    private int totalHeight;

    /**
     * Contructeur qui initialise le réseau
     *
     * @param input       Nombre de neurones d'input dans le réseau
     * @param hidden      Nombre de neurones de hidden dans le réseau
     * @param output      Nombre de neurones d'output dans le réseau
     * @param training    Training rate du reseau
     * @param charListing List des char avec leur index dans le réseau
     *
     * @throws IOException S'il y a des problèmes de fichier, ...
     */
    public ImageDecoder(final int input, final int hidden, final int output, final double training, final SQLiteDatabase database, String[] charListing, NeuralNetwork.OnNetworkReady listener) throws IOException {
        listChar = new ArrayList<>();
        charList = charListing;

        //network = new NeuralNetwork(input, hidden, output, training, database, listener);
    }

    /**
     * Obtient l'équation en string
     *
     * @param btm L'image à décoder
     * @return l'équation en String
     * @throws IOException S'il y a des problème avec l'image
     */
    public String findSting(Bitmap btm) throws IOException {
        totalHeight = btm.getHeight();
        totalWidth = btm.getWidth();

        listChar.clear();
        String line = "";

        //Split toutes les chars
        splitChar(btm);
        int index = 0;
        //Add le char dans l'eq
        for (int i = 0; i < listChar.size(); i++) {
            int[] pix = getIOPixels(listChar.get(i).getImage());
            //int index = network.getAnwser(pix);

            listChar.get(i).setValue(charList[index]);

            //TO DO
            //line += charList[index];
            line += replaceChar();
        }

        return line;
    }

    /**
     * Split les différents caractère de l'image
     *
     * @param btm               L'image à analyser
     * @throws IOException        S'il y a des problèmes
     */
    private void splitChar(Bitmap btm) throws IOException
    {
        MathChar mC = new MathChar(btm, 0, 0, btm.getWidth(), btm.getHeight());
        mC.getListChar().clear();
        mC.splitChar(true);
        for(MathChar mCInnerFirst : mC.getListChar())
            listChar.add(mCInnerFirst);
    }

    /**
     * Get un array une dimension de la valeur binaire des pixels d'une iimage
     *
     * @param bitmap        L'image
     * @return              Un tableau de int selon les pixels(1 ou 0)
     * @throws IOException    S'il y a des problèmes
     */
    private int[]getIOPixels(Bitmap bitmap) throws IOException
    {
        ArrayList<Integer> pixels = new ArrayList<>();

        //Selon la valeur du pixel, 1 ou 0
        int pixel;
        for(int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                pixel = bitmap.getPixel(i, j);
                if(Color.red(pixel) < 0x0C && Color.green(pixel) < 0x0C && Color.blue(pixel) < 0x0C)
                    pixels.add(1);
                else
                    pixels.add(0);
            }
        }

        //Transformation en array de int
        int[] inputValues = new int[bitmap.getWidth()*bitmap.getHeight()];
        for(int i = 0; i < pixels.size(); i++)
            inputValues[i] = pixels.get(i);

        return inputValues;
    }

    public String replaceChar()
    {
        //Tolerance de 5%
        final int toleranceHeight = (int) (totalHeight *0.05);
        final int toleranceWidth = (int) (totalWidth*0.05);
        String buildedEq = "";
        int index = 0;

        //Ajouter la tolerence
        /*
        if(char.getXStart - tolerenceWidth < char2.getXStart && char.getXStart + tolerenceWidth > char2.getXStart)
        {
            //Un par dessus l'autre

        }

        if(char.getXStart + char.getWidth > char2.getXStart)
        {
            //Ou fraction

            if(char.getYStart > char2.getYStart)
                char_(char2)
             else
                char^(char2)
         }

         */

        //get le premier char
        for(int i = 0; i < listChar.size(); i++)
            if(listChar.get(i).getXStart() < listChar.get(index).getXStart())
                index = i;

        buildedEq += listChar.get(index).getValue();


        //TO DO
        //Call post traitment
        return buildedEq;
    }
}