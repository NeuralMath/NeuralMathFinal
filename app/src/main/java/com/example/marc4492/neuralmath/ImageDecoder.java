package com.example.marc4492.neuralmath;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Class qui contient le réseau de neurones et qui obtient l'équation en string
 *
 * @author Marc4492
 * 10 février 2017
 */

class ImageDecoder {
    private String[] charList;
    private NeuralNetwork network;

    private final int OUTPUT;

    private int originalTolWidth;
    private boolean appendMode = false;
    private int lastIndex = 0;

    private ArrayList<MathChar> listCharDetected = new ArrayList<>();

    private Context context;

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
    ImageDecoder(Context c, final int input, final int hidden, final int output, final double training, final SQLiteDatabase database, String[] charListing) throws IOException {
        context = c;
        charList = charListing;
        network = new NeuralNetwork(input, hidden, output, training, database);
        OUTPUT = output;
    }

    void setAppendMode(boolean appendM) {
        appendMode = appendM;
    }

    /**
     * Obtient l'équation en string
     *
     * @param btm L'image à décoder
     * @return l'équation en String
     * @throws IOException S'il y a des problème avec l'image
     */
    String findSting(Bitmap btm) throws IOException {
        ArrayList<MathChar> listChar;
        int totalHeight = btm.getHeight();
        int totalWidth = btm.getWidth();

        MathChar.emptyList();

        //Split tous les chars
        listChar = splitChar(btm);

        if(appendMode)
            listCharDetected.addAll(listChar);
        else
            listCharDetected = listChar;

        //Trier par ordre croisant de l'ordre d'arriver (x)
        Collections.sort(listChar, new Comparator<MathChar>() {
            @Override
            public int compare(MathChar mathChar, MathChar otherMatChar) {
                return mathChar.getXStart() - otherMatChar.getXStart();
            }
        });

        for(int i = 0; i < listChar.size(); i++) {
            //int index = network.getAnwser(getIOPixels(listChar.get(i).getImage()));
            //listChar.get(i).setValue(charList[index]);
            listChar.get(i).setValue(String.valueOf(i));
        }

        originalTolWidth = (int) (totalWidth *0.1);
        int originalTolHeight = (int) (totalHeight * 0.1);

        return postTreatment(replaceChar(listChar, originalTolWidth, originalTolHeight));
    }

    /**
     * Replacer les exposant, les indices et les fractions
     *
     * @param listChar      Liste des l'élements à comparer
     * @return              La String résultante
     */
    private String replaceChar(ArrayList<MathChar> listChar, int toleranceWidth, int toleranceHeight) {
        String line = "";
        boolean notCheckingLast = false;
        int indexToLook = 0;
        ArrayList<MathChar> listFraction = new ArrayList<>();

        //si le premier char n'est pas dans un fraction
        int index;
        if(listChar.get(0).getIsInFraction() == 0) {
            line += listChar.get(0).getValue();
            setIndexOfString(line, 0);
            index = 1;
        }
        else
            index = 0;

        //Repalcer les caractères
        for(; index < listChar.size(); index++)
        {
            if(!notCheckingLast)
                indexToLook = index - 1;

            //Si fraction
            if(listChar.get(index).getIsInFraction() > 0) {
                notCheckingLast = true;
                indexToLook = index;
                listFraction.clear();
                while (index < listChar.size() && listChar.get(index).getIsInFraction() > 0) {
                    listFraction.add(listChar.get(index));
                    index++;
                }
                line = findFraction(line, listFraction, toleranceHeight/2);
                if(index < listChar.size())
                    index--;
            }
            //Si un =
            else if(Math.abs(listChar.get(index).getWidth() - listChar.get(indexToLook).getWidth()) < toleranceWidth/2 && Math.abs(listChar.get(index).getXMiddle() - listChar.get(indexToLook).getXMiddle()) < toleranceWidth/2 && Math.abs(listChar.get(index).getYEnd() - listChar.get(indexToLook).getYStart()) < 1.5*toleranceHeight)
                line = line.substring(0, line.length()-1) + "=";

            //Si un à côté de l'autre
            else if(isBeside(listChar.get(indexToLook), listChar.get(index), toleranceHeight)) {
                notCheckingLast = false;
                line += listChar.get(index).getValue();
                setIndexOfString(line, index);
            }

            //Si un exposant
            else if(listChar.get(index).getYEnd() <= listChar.get(indexToLook).getYMiddle()) {
                notCheckingLast = true;
                line = findExposant(listChar, line, toleranceHeight/2, index);
            }

            //Si un indice
            else if(listChar.get(index).getYStart() > listChar.get(indexToLook).getYMiddle()) {
                notCheckingLast = true;
                line = findIndice(listChar, line, toleranceHeight/2, index);
            }
            else
                Toast.makeText(context, "Le découpage de caractère ne s'est pas déroulé normalement", Toast.LENGTH_LONG).show();
        }

        if(appendMode)
            lastIndex += line.length();
        else
            lastIndex = 0;

        return line;
    }

    /**
     * Vérification si deux élements sont un à coté de l'autre selon une tolérance.
     *
     * @param first                 Premier élement à comparer
     * @param second                Premier élement à comparer
     * @param tolerance             Tolérance autorisée
     * @return                      Si les deux élements sont un à coté de l'autre
     */
    private boolean isBeside(MathChar first, MathChar second, int tolerance) {
        return Math.abs(second.getYMiddle() - first.getYMiddle()) <= tolerance;
    }

    /**
     * Split les différents caractère de l'image
     *
     * @param btm               L'image à analyser
     * @throws IOException        S'il y a des problèmes
     */
    private ArrayList<MathChar> splitChar(Bitmap btm) throws IOException
    {
        MathChar mC = new MathChar(btm, 0, 0, btm.getWidth(), btm.getHeight(), 0);
        mC.splitChar(true);
        return MathChar.getStaticList();
    }


    /**
     * Obtenir les exposants dans une équation
     *
     * @param listChar              List des élements à vérifier
     * @param line                  String comprenant le début de l'équation
     * @param toleranceHeight       La tolérence en hauteur pour accepter qu'ils sont un a coté de l'autre
     * @param index                 Index de la premiere postion à vérifier dans la list
     * @return                      La String complèter avec les exposants
     */
    private String findExposant(ArrayList<MathChar> listChar, String line, int toleranceHeight, int index)
    {
        line += "^(" + listChar.get(index).getValue();
        setIndexOfString(line, index);

        if(index < listChar.size()-1) {
            index++;
            while (index < listChar.size()) {
                //S'il sont un à coté de l'autre
                if(isBeside(listChar.get(index - 1), listChar.get(index), toleranceHeight)) {
                    line += listChar.get(index).getValue();
                    setIndexOfString(line, index);
                }
                //S'il sont en exposants
                else if(listChar.get(index).getYEnd() <= listChar.get(index-1).getYMiddle())
                    line = findExposant(listChar, line, toleranceHeight, index);

                //Sinon sortir
                else
                    break;
                index++;
            }
        }
        line += ")";

        return line;
    }

    /**
     * Obtenir les indices dans une équation
     *
     * @param listChar              List des élements à vérifier
     * @param line                  String comprenant le début de l'équation
     * @param toleranceHeight       La tolérence en hauteur pour accepter qu'ils sont un a coté de l'autre
     * @param index                 Index de la premiere postion à vérifier dans la list
     * @return                      La String complèter avec les indices
     */
    private String findIndice(ArrayList<MathChar> listChar, String line, int toleranceHeight, int index)
    {
        line += "_(" + listChar.get(index).getValue();
        setIndexOfString(line, index);

        if(index < listChar.size()-1) {
            index++;
            while (index < listChar.size()) {
                //S'il sont un à coté de l'autre
                if(isBeside(listChar.get(index - 1), listChar.get(index), toleranceHeight)) {
                    line += listChar.get(index).getValue();
                    setIndexOfString(line, index);
                }

                //S'il sont en indices
                else if(listChar.get(index).getYStart() > listChar.get(index -1).getYMiddle())
                    line = findIndice(listChar, line, toleranceHeight, index);

                //Sinon sortir
                else
                    break;
                index++;
            }
        }
        line += ")";

        return line;
    }

    private void setIndexOfString(String line, int index) {
        listCharDetected.get(index).setIndexInString((line.length()-1)+lastIndex);
    }

    /**
     * Obtenir les fractions dans la liste fournie
     *
     * @param line                      Le début de l'équation
     * @param list                      La liste à vérifier
     * @param tolerenceHeight           La tolérence en hauteur pour accepter qu'ils sont un a coté de l'autre
     * @return                          La String complèté avec les fractions
     */
    private String findFraction(String line, ArrayList<MathChar> list, int tolerenceHeight)
    {
        ArrayList<MathChar> listTopFraction = new ArrayList<>();
        ArrayList<MathChar> listBottomFraction = new ArrayList<>();
        int indexBarreFraction = 0;

        line += "(";


        //Tri up down de la ligne de fraction
        for(int i = 1; i < list.size(); i++) {
            list.get(i).setIsInFraction(list.get(i).getIsInFraction() - 1);
            if (list.get(i).getYEnd() < list.get(indexBarreFraction).getYStart())
                listTopFraction.add(list.get(i));
            else if (list.get(i).getYStart() > list.get(indexBarreFraction).getYEnd())
                listBottomFraction.add(list.get(i));
        }
        line += replaceChar(listTopFraction, originalTolWidth, tolerenceHeight/2);

        line += ")/(";

        line += replaceChar(listBottomFraction, originalTolWidth, tolerenceHeight/2);

        line += ")";
        return line;
    }

    /**
     * Traiter les problèmes potentielles et récurant
     *
     * @param line      L'équation à revérifier
     * @return          L'équation vérifié
     */
    private String postTreatment(String line)
    {
        line = line.replaceAll("1og", "log");
        line = line.replaceAll("s1n", "sin");
        line = line.replace("c0s", "cos");
        line = line.replace("o,", "0,");
        line = line.replace("o.", "0.");
        return line;
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


    /**
     * Remplir l'image (centé) pour les grandeures de l'AI
     *
     * @param btm       L'image à remplir
     * @return          L'image remple
     */
    private Bitmap fillImage(Bitmap btm)
    {
        int width = btm.getWidth();
        int height = btm.getHeight();

        int borderSize = 5;

        Bitmap newImage;
        Canvas canvas;

        //Pour faire un carré
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

    /**
     *
     *
     * @param list
     * @throws IOException
     */
    void trainNN(ArrayList<ReplacedChar> list) throws IOException {
        final int[][] trainning = new int[list.size()][];
        final int[][] results = new int[list.size()][OUTPUT];
        MathChar wanted = null;

        for (int[] val : results)
            Arrays.fill(val, 0);


        for (int i = 0; i < list.size(); i++) {
            for (MathChar mC : listCharDetected) {
                if (mC.getIndexInString() == list.get(i).getPosition()) {
                    wanted = mC;
                    break;
                }
            }

            if (wanted != null) {
                trainning[i] = getIOPixels(fillImage(wanted.getImage()));

                if (Arrays.asList(charList).contains(list.get(i).getNewChar()))
                    results[i][Arrays.asList(charList).indexOf(list.get(i).getNewChar())] = 1;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            network.trainAll(trainning, results);
                        } catch (IOException ex) {
                            Log.e("NeuralNetwork", "Trainning", ex);
                        }
                    }
                });
            }
        }
        clearData();
    }

    void clearData()
    {
        listCharDetected = new ArrayList<>();
        lastIndex = 0;
    }

    public boolean isReady()
    {
        return network.isReady();
    }
}