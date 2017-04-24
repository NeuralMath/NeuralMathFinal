package com.example.marc4492.neuralmath;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import java.io.IOException;
import java.util.ArrayList;

public class MathChar {

    private Bitmap image;
    private int xStart = 0;
    private int yStart = 0;
    private int xEnd;
    private int yEnd;
    private int width = 0;
    private int height = 0;
    private String value ="";

    private MathChar right = null;
    private MathChar top = null;
    private MathChar bottom = null;

    private ArrayList<MathChar> listInner = new ArrayList<>();
    private static ArrayList<MathChar> listFinal = new ArrayList<>();

    public MathChar(Bitmap b, int x, int y, int w, int h) {
        image = b;
        xStart = x;
        yStart = y;
        xEnd = x + w;
        yEnd = y + h;
        width = w;
        height = h;
    }

    public MathChar(MathChar mC)
    {
        image = mC.image;
        xStart = mC.xStart;
        yStart = mC.yStart;
        xEnd = mC.xEnd;
        yEnd = mC.yEnd;
        width = mC.width;
        height = mC.height;
        right = mC.right;
        top = mC.top;
        bottom = mC.bottom;
    }

    public Bitmap getImage() {
        return image;
    }

    public ArrayList<MathChar> getListChar() {
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

    public int getXEnd() {
        return xEnd;
    }

    public int getYEnd() {
        return yEnd;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public MathChar getRight() {
        return right;
    }

    public MathChar getTop() {
        return top;
    }

    public MathChar getBottom() {
        return bottom;
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

        if (listInner.size() == 0)
        {
            boolean needAdd = true;
            for(MathChar mC : listFinal)
            {
                if(mC.getTop() != null) {
                    for (MathChar mCInner : mC.getTop().getListChar()) {
                        if (mCInner == this) {
                            needAdd = false;
                            break;
                        }
                    }
                }

                if(needAdd && mC.getBottom() != null) {
                    for (MathChar mCInner : mC.getBottom().getListChar()) {
                        if (mCInner == this) {
                            needAdd = false;
                            break;
                        }
                    }
                }
            }
            if(needAdd)
                listFinal.add(this);
        }

        else
            /*for (int i = 0; i < listInner.size(); i++) {
                if (vertical) {
                    if (i != listInner.size() - 1) {
                        listInner.get(i).right = listInner.get(i + 1);
                    }
                } else {
                    if (i != listInner.size() - 1) {
                        if (i == 0) {
                            listInner.get(i).bottom = listInner.get(i + 1);
                        } else if (i == listInner.size() - 1) {
                            listInner.get(i).top = listInner.get(i - 1);
                        } else {
                            listInner.get(i).top = listInner.get(i - 1);
                            listInner.get(i).bottom = listInner.get(i + 1);
                        }
                    }
                }

            }*/
            for(MathChar mC : listInner)
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
                if (Color.red(pixel) +  Color.green(pixel) + Color.blue(pixel) < 320) {
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
            for(int j = listInner.size()-1; j > 0; j--) {
                listInner.get(j - 1).right = listInner.get(j);
                listInner.remove(j);
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
                if(Color.red(pixel) +  Color.green(pixel) + Color.blue(pixel) < 320) {
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
            MathChar temp = null;
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
            for(int j = listInner.size()-1; j > 0; j--) {
                listInner.get(j - 1).bottom = listInner.get(j);
                listInner.remove(j);
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
