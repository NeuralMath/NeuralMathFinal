package com.example.marc4492.neuralmath;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class qui contient le réseau de neurones et qui obtient l'équation en string
 *
 * @author Marc4492
 * 10 février 2017
 */

public class ImageDecoder {
    private ArrayList<Bitmap> listChar;
    private NeuralNetwork network;
    private String[] charList;

    private int squaredPixNumber;

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

        squaredPixNumber = (int) Math.sqrt(input);
        charList = charListing;

        network = new NeuralNetwork(input, hidden, output, training, database, listener);
    }

    /**
     * Obtient l'équation en string
     *
     * @param btm L'image à décoder
     * @return l'équation en String
     * @throws IOException S'il y a des problème avec l'image
     */
    public String findSting(Bitmap btm) throws IOException {
        listChar.clear();
        String line = "";

        setIOPixels(btm);

        //Split toutes les chars
        splitChar(btm);

        for(int i = 0; i < listChar.size(); i++)
        {
            String path = Environment.getExternalStorageDirectory().getPath() + "/NeuralMath/";
            File f = new File(path + String.valueOf(i) +  ".png");
            try {
                FileOutputStream out = new FileOutputStream(f);
                listChar.get(i).compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
            }catch (Exception e) {
                Log.e("Image decoder", "saving", e);
            }
        }

        //Add le char dans l'eq
        for (int i = 0; i < listChar.size(); i++) {
            int[] pix = getIOPixels(listChar.get(i));
            int index = network.getAnwser(pix);
            line += charList[index];
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
            listChar.add(resize(fillImage(mCInnerFirst.getImage()), squaredPixNumber, squaredPixNumber));
    }

    /**
     * Changer la grandeur de l'image en gardant le ratio
     *
     * @param bitmap        L'image
     * @return              L'image resized
     * @throws IOException    S'il y a des problèmes
     */
    private Bitmap resize(Bitmap bitmap, int width, int height) throws IOException
    {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    /**
     * Set l'image en noir et blanc
     *
     * @param btm   L'image à convertir
     */
    private void setIOPixels(Bitmap btm)
    {
        int pixel;
        for(int i = 0; i < btm.getWidth(); i++) {
            for (int j = 0; j < btm.getHeight(); j++) {
                pixel = btm.getPixel(i, j);
                if (Color.red(pixel) < 0x0C && Color.green(pixel) < 0x0C && Color.blue(pixel) < 0x0C)
                    btm.setPixel(i, j, 0xFF000000);
                else
                    btm.setPixel(i, j, 0xFFFFFFFF);
            }
        }
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

    private Bitmap fillImage(Bitmap btm)
    {
        int width = btm.getWidth();
        int height = btm.getHeight();

        int borderSize = 5;

        Bitmap newImage;
        Canvas canvas;

        if(width < height)
        {
            newImage = Bitmap.createBitmap(height + 2*borderSize, height + 2*borderSize, btm.getConfig());
            canvas = new Canvas(newImage);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(btm, (newImage.getWidth()-width)/2, borderSize, null);
            return newImage;
        }
        else if (height < width)
        {
            newImage = Bitmap.createBitmap(width + 2*borderSize, width + 2*borderSize, btm.getConfig());
            canvas = new Canvas(newImage);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(btm, borderSize, (newImage.getHeight()-height)/2, null);
            return newImage;
        }
        else
        {
            newImage = Bitmap.createBitmap(width + 2*borderSize, height + 2*borderSize, btm.getConfig());
            canvas = new Canvas(newImage);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(btm, borderSize, borderSize, null);
            return newImage;
        }
    }
}