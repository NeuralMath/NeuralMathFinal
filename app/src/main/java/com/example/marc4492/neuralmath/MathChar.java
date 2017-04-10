package com.example.marc4492.neuralmath;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.IOException;
import java.util.ArrayList;

public class MathChar {
    private Bitmap image;
    private int xStart = 0;
    private int yStart = 0;
    private int width = 0;
    private int height = 0;

    private ArrayList<MathChar> listInner = new ArrayList<>();

    private static ArrayList<MathChar> listFinal = new ArrayList<>();

    public MathChar(Bitmap b, int x, int y, int w, int h) {
        image = b;
        xStart = x;
        yStart = y;
        width = w;
        height = h;
    }

    public Bitmap getImage() {
        return image;
    }

    public ArrayList<MathChar> getListChar() {
        return listFinal;
    }



    public int getXStart() {
        return xStart;
    }

    public int getYStart() {
        return yStart;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Split toutes les char dans l'image
     * @param vertical      Si l'on split verticalement ou pas
     * @throws IOException  S'il y a des problemes avec le splitage
     */
    public void splitChar(boolean vertical) throws IOException {
        if (vertical)
            splitVertical();
        else
            splitHorizontal();

        if(listInner.size() == 0)
            listFinal.add(this);
        else
            for (MathChar mC : listInner)
                    mC.splitChar(!vertical);
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
                if (Color.red(pixel) < 0x0C && Color.green(pixel) < 0x0C && Color.blue(pixel) < 0x0C) {
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
                while(i < listBlack.size() && listBlack.get(i) - listBlack.get(i-1) == 1)
                    i++;

                newWidth = listBlack.get(i-1)-start;


                listInner.add(new MathChar(crop(image, start, 0, newWidth, image.getHeight()), start + xStart, yStart, newWidth, image.getHeight()));

                if(i < listBlack.size())
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
                if(Color.red(pixel) < 0x0C && Color.green(pixel) < 0x0C && Color.blue(pixel) < 0x0C)
                {
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
                while(i < listBlack.size() && listBlack.get(i) - listBlack.get(i-1) == 1)
                    i++;

                newHeight = listBlack.get(i-1)-start;

                listInner.add(new MathChar(crop(image, 0, start,image .getWidth(), newHeight), xStart, start + yStart, image.getWidth(), newHeight));

                if(i < listBlack.size())
                    start = listBlack.get(i);
                i++;
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
