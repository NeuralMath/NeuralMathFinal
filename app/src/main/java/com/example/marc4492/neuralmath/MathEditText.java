package com.example.marc4492.neuralmath;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This custom edit text disable undesired function such as the spellchecker and the copy/cut
 * Created by Mathieu Boucher on 2017-03-29.
 */

public class MathEditText extends android.support.v7.widget.AppCompatEditText{

    public MathEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        /* ------------------------------------------------
         * code from: http://stackoverflow.com/a/12331404
         * author: Zain Ali
         * consulted date: 1 April 2017
         */
        //disable the copy, cut and other context menu
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        //--------------------------------------------------

    }

    /**
     * This override function disable the text drag and drop
     * @param event
     * @return
     */
    @Override
    public boolean onDragEvent(DragEvent event) {
        return  true;
    }

    /**
     * This override function disable the spellchecker
     */
    @Override
    public boolean isSuggestionsEnabled() {
        return true;
    }

}
