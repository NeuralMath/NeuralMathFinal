package com.example.marc4492.neuralmath;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.IOException;
import java.util.ArrayList;

public class MathChar {

    private Bitmap image;

    private int xStart = 0;
    private int yStart = 0;
    private int xEnd;
    private int yEnd;
    private int xMiddle;
    private int yMiddle;
    private int width = 0;

    private int isInFraction = -1;

    private int indexInString = 0;

    private String value = "";

    private ArrayList<MathChar> listInner = new ArrayList<>();
    private static ArrayList<MathChar> listFinal = new ArrayList<>();

    public MathChar(Bitmap b, int x, int y, int w, int h, int isFrac) {
        image = b;
        xStart = x;
        yStart = y;

        xEnd = x + w;
        yEnd = y + h;

        isInFraction = isFrac;

        xMiddle = (xStart + xEnd)/2;
        yMiddle = (yStart + yEnd)/2;

        width = w;
    }

    public Bitmap getImage() {
        return image;
    }

    public static ArrayList<MathChar> getStaticList() {
        return listFinal;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public int getXStart() {
        return xStart;
    }

    public int getYStart() {
        return yStart;
    }

    public int getYEnd() {
        return yEnd;
    }

    public int getWidth() {
        return width;
    }

    public int getXMiddle() {
        return xMiddle;
    }

    public int getYMiddle() {
        return yMiddle;
    }

    public int getIsInFraction() {
        return isInFraction;
    }

    public void setIsInFraction(int inFraction) {
        isInFraction = inFraction;
    }

    public int getIndexInString() {
        return indexInString;
    }

    public void setIndexInString(int index) {
        indexInString = index;
    }

    public static void emptyList()
    {
        listFinal.clear();
    }

    /**
     * Split toutes les char dans l'image
     * @param isSplitVertical      Si l'on split verticalement ou pas
     * @throws IOException  S'il y a des problemes avec le splitage
     */
    public void splitChar(boolean isSplitVertical) throws IOException {
        if (isSplitVertical)
            splitVertical();
        else
            splitHorizontal();

        //Si le découpage est fini
        if(listInner.size() == 0)
            listFinal.add(this);
        else
            for (MathChar mC : listInner)
                mC.splitChar(!isSplitVertical);
    }




    /**
     * Split les différents caractère de l'image de facon vertical
     *
     * @throws IOException      S'il y a des problèmes
     */
    private void splitVertical() throws IOException
    {
        ArrayList<Integer> listBlack = new ArrayList<>();
        int pixel;
        int newWidth;

        //Check chaque colonne pour voir si elle est blanche : check si + noir que blanc
        for(int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                pixel = image.getPixel(i, j);
                if (Color.red(pixel) +  Color.green(pixel) + Color.blue(pixel) < 50) {
                    listBlack.add(i);
                    break;
                }
            }
        }

        //Si l'image n'est pas toute noir
        if(listBlack.size() > 0 && listBlack.size() < image.getWidth()-1)
        {
            int start = listBlack.get(0);
            int i = 1;
            //Passer au travers de toutes les lignes
            while(i < listBlack.size())
            {
                //Trouver le end du char
                while(i < listBlack.size() && listBlack.get(i) - listBlack.get(i-1) <= 3)
                    i++;

                if(listBlack.get(i-1)-start != 0) {
                    if (listBlack.get(i - 1) != 0)
                        newWidth = listBlack.get(i - 1) - start;
                    else
                        newWidth = listBlack.get(i);

                    listInner.add(new MathChar(crop(image, start, 0, newWidth, image.getHeight()), start + xStart, yStart, newWidth, image.getHeight(), isInFraction));
                }

                if(i < listBlack.size()-1)
                    start = listBlack.get(i);
                i++;
            }
        }
    }

    /**
     * Split les différents caractère de l'image de facon horizontal
     *
     * @throws IOException      S'il y a des problèmes
     */
    private void splitHorizontal() throws IOException
    {
        ArrayList<Integer> listBlack = new ArrayList<>();
        int pixel;
        int newHeight;

        //Check chaque colonne pour voir si elle est blanche : check chaque couleurs pour les val hex
        for(int i = 0; i < image.getHeight() ; i++) {
            for (int j = 0; j <image.getWidth(); j++) {
                pixel = image.getPixel(j, i);
                if(Color.red(pixel) +  Color.green(pixel) + Color.blue(pixel) < 50) {
                    listBlack.add(i);
                    break;
                }
            }
        }

        //Si l'image n'est pas toute noir
        if(listBlack.size() > 0 && listBlack.size() < image.getHeight()-1)
        {
            int start = listBlack.get(0);
            int i = 1;
            //Passer au travers de toutes les lignes
            while(i < listBlack.size())
            {
                //Trouver le end du char
                while(i < listBlack.size() && listBlack.get(i) - listBlack.get(i-1) <= 3)
                    i++;

                if(listBlack.get(i-1)-start != 0) {
                    if (listBlack.get(i - 1) != 0)
                        newHeight = listBlack.get(i - 1) - start;
                    else
                        newHeight = listBlack.get(i);

                    listInner.add(new MathChar(crop(image, 0, start, image.getWidth(), newHeight), xStart, start + yStart, image.getWidth(), newHeight, isInFraction));
                }

                if(i < listBlack.size())
                    start = listBlack.get(i);
                i++;
            }

            //Probablement des fractions
            if(listInner.size() >= 7) {
                for (MathChar mC : listInner)
                    mC.isInFraction = isInFraction + 3;
            }
            else if(listInner.size() >= 5) {
                for (MathChar mC : listInner)
                    mC.isInFraction = isInFraction + 2;
            }
            else if(listInner.size() >= 3)
            {
                for (MathChar mC : listInner)
                    mC.isInFraction = isInFraction + 1;
            }
        }
    }

    /**
     * Rogner l'image selon les paramètre
     *
     * @param bitmap            L'image à rogner
     * @param startX            Le point de départ en X dans l'image d'origine
     * @param startY            Le point de départ en Y dans l'image d'origine
     * @param width             La largeur voulu de l'image
     * @param height            La hauteur voulu de l'image
     * @return                  L'image rogner
     * @throws IOException      S'il y a des problèmes
     */
    private Bitmap crop(Bitmap bitmap, int startX, int startY, int width, int height) throws IOException
    {
        return Bitmap.createBitmap(bitmap, startX, startY, width, height);
    }
}
