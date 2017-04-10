package com.example.marc4492.neuralmath;

/**
 * Created by Mathieu on 2017-02-08.
 */

public class HomeRow {

    private int image;
    private String text;

    public HomeRow(int image, String text) {
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
