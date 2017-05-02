package com.example.marc4492.neuralmath;

/**
 * This class keep in memory a list of the replaced char
 * Created by Mathieu Boucher on 2017-04-20.
 */

public class ReplacedChar {

    private int position;
    private char oldChar;
    private char newChar = '\0';

    public ReplacedChar(char oldC, int pos) {
        position = pos;
        oldChar = oldC;
    }

    public int getPosition() {
        return position;
    }

    public char getOldChar() {
        return oldChar;
    }

    public char getNewChar() {
        return newChar;
    }

    public void setNewChar(char newChar) {
        this.newChar = newChar;
    }

}
