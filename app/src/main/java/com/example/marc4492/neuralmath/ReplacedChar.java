package com.example.marc4492.neuralmath;

/**
 * This class keep in memory a list of the replaced char
 * Created by Mathieu Boucher on 2017-04-20.
 */

public class ReplacedChar {

    private int position;
    private char oldChar;
    private char newChar;

    public ReplacedChar(char oldChar, int position) {
        this.position = position;
        this.oldChar = oldChar;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public char getOldChar() {
        return oldChar;
    }

    public void setOldChar(char oldChar) {
        this.oldChar = oldChar;
    }

    public char getNewChar() {
        return newChar;
    }

    public void setNewChar(char newChar) {
        this.newChar = newChar;
    }

}
