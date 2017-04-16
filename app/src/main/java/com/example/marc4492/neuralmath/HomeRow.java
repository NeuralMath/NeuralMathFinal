package com.example.marc4492.neuralmath;

import android.content.Context;
import android.view.View;

/**
 * Created by Mathieu on 2017-02-08.
 */

public class HomeRow extends View {

    private int image;
    private String text;



    public HomeRow(Context c, int image, String text) {
        super(c);
        this.image = image;
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public void setText(String val)
    {
        text = val;
    }
}
