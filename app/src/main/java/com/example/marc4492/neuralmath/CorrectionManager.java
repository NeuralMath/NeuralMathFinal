package com.example.marc4492.neuralmath;

import java.util.ArrayList;

/**
 * Manage the correction of the equation
 * Created by Mathieu Boucher on 2017-04-20.
 */

class CorrectionManager {

    private ArrayList<ReplacedChar> replacedCharList;

    private ArrayList<Integer> indexList; //index of the deleted char that did not got replaced
    private int correctionCounter; //A counter that count the corrected character

    /**
     * the constructor initialise the different variables
     */
    CorrectionManager() {
        correctionCounter = 0;
        replacedCharList = new ArrayList<>();
        indexList = new ArrayList<>();
    }

    /**
     * return the number of char that has been deleted but not replaced
     *
     * @return the number of char that has been deleted but not replaced
     */
    int getCorrectionCounter() {
        return correctionCounter;
    }

    /**
     * Save in memory the deleted char
     *
     * @param character the deleted char
     * @param index     the position of the char
     */
    void deleteChar(Character character, int index) {
        ReplacedChar temp = new ReplacedChar(character, index);

        replacedCharList.add(temp);
        indexList.add(index);
        correctionCounter++;
    }

    /**
     * Save the modified character
     *
     * @param character the modified char
     * @param index     the position of the char
     */
    void addChar(Character character, int index) {
        int deletedChar = 0;//count the unreplaced char before this one;
        for (int i = 0; i < indexList.size(); i++) {
            if (indexList.get(i) < index)
                deletedChar++;
        }


        //finding the replaced char and save them together
        for (int i = 0; i < replacedCharList.size(); i++) {
            if (replacedCharList.get(i).getPosition() - deletedChar == index) {
                replacedCharList.get(i).setNewChar(character);
                removeFromList(indexList, index + deletedChar);
                break;
            }
        }
        correctionCounter--;
    }

    /**
     * remove a int from an arrayList
     *
     * @param list  the ArrayList of int
     * @param value the value
     */
    private void removeFromList(ArrayList<Integer> list, int value) {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i) == value)
                list.remove(i);
    }

    /**
     * This method find if there is a deleted char with no replacement yet at a position
     *
     * @param position the position of the char
     * @return If it add an deleted char
     */
    boolean hasDeletedCharAt(int position) {
        int deletedChar = 0;//count the unreplaced char before this one;
        for (int i = 0; i < indexList.size(); i++) {
            if (indexList.get(i) < position)
                deletedChar++;
        }

        for (int i = 0; i < indexList.size(); i++)
            if (position + deletedChar == indexList.get(i))
                return true;

        return false;
    }

    /**
     * This method find if there is succession of deleted char with no replacement yet at a position
     *
     * @param position the position of the first char
     * @param length   the length of the succession
     * @return If there is succession of deleted char with no replacement yet at a position
     */
    boolean hasDeletedCharAt(int position, int length) {
        for (int i = 0; i < length; i++)
            if (!hasDeletedCharAt(position + i))
                return false;

        return true;
    }


    /**
     * return the list of replaced char
     *
     * @return return the list of replaced char
     */
    ArrayList<ReplacedChar> getReplacedCharList() {
        return replacedCharList;
    }
}