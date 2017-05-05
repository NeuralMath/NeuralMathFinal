package com.example.marc4492.neuralmath;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Alex on 26/03/2017.
 * Classe permettant d'avoir un textView avec un nombre maximal de lignes
 * Inspiré de : https://codexplo.wordpress.com/2013/09/07/android-expandable-textview/
 */

public class ExpandableTextView extends android.support.v7.widget.AppCompatTextView implements View.OnClickListener {
    private static final int MAX_LINES = 1;
    private int currentMaxLines = Integer.MAX_VALUE;

    public ExpandableTextView(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(this);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        post(new Runnable() {
            public void run() {
                if ((getLineCount() > MAX_LINES))
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.see_more, 0);      // Mise de l'image see_more si les lignes de texte dans le textView sont plus grande que le nb de lignes maximales.
                else
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                setMaxLines(MAX_LINES);
            }
        });
    }

    @Override
    public void setMaxLines(int maxLines) {
        currentMaxLines = maxLines;
        super.setMaxLines(maxLines);
    }

    public int getMyMaxLines() {
        return currentMaxLines;
    }

    @Override
    public void onClick(View v) {
        /* Toggle between expanded collapsed states */
        if (getMyMaxLines() == Integer.MAX_VALUE)
            setMaxLines(MAX_LINES);
        else
            setMaxLines(Integer.MAX_VALUE);
    }


    public int getMaxLines() {
        return MAX_LINES;
    }
}