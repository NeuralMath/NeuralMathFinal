package com.example.marc4492.neuralmath;

/**
 * This class keep in memory a list of the replaced char
 * Created by Mathieu Boucher on 2017-04-20.
 */

class ReplacedChar {

    private int position;
    private char oldChar;
    private char newChar = '\0';

    ReplacedChar(char oldC, int pos) {
        position = pos;
        oldChar = oldC;
    }

    int getPosition() {
        return position;
    }

    char getNewChar() {
        return newChar;
    }

    void setNewChar(char newChar) {
        this.newChar = newChar;
    }

}
