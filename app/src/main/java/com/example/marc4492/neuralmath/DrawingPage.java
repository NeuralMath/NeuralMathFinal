package com.example.marc4492.neuralmath;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Author: Created by Mathieu on 2017-03-08.
 * A layout containing a DrawingView, some options button and a output area
 * This layout can be set for right-handed or left-handed
 */

public class DrawingPage extends LinearLayout {

    private DrawingView drawView;
    private LinearLayout layoutBtn;
    private TextView txtEquation;

    private Button btnDone;
    private Button btnClearText;
    private Button btnBack;

    /**
     * Creation des deux parties de la page
     *
     * @param context Context de l'app
     * @param attrs   ?
     */
    public DrawingPage(Context context, AttributeSet attrs) {
        super(context, attrs);

        drawView = new DrawingView(context);
        drawView.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));

        txtEquation = new TextView(context);
        txtEquation.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f));
        txtEquation.setGravity(Gravity.CENTER);
        txtEquation.setTextSize(20);

        /* ------------------------------------------------
         * code from: http://stackoverflow.com/a/13975236
         * author: Eddie Sullivan
         * consulted date: 22 March 2017
         */
        // Update the EditText so it won't popup Android's own keyboard, since I have my own.
        txtEquation.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });

        //--------------------------------------------------

        Button btnRetry = new Button(context);
        btnRetry.setText(R.string.clear);
        //setting the button click listener
        btnRetry.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                drawView.clear();
            }
        });

        btnClearText = new Button(context);
        btnClearText.setText(R.string.btn_clear_eq);


        btnDone = new Button(context);
        btnDone.setText(R.string.done);

        btnBack = new Button(context);
        btnBack.setText(R.string.annuler_txt);

        Button btnEreaseOne = new Button(context);
        btnEreaseOne.setText(R.string.eff_lettre);
        btnEreaseOne.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtEquation.getText().length() > 0) {
                    DrawingActivity.getImageDecoder().removeOne();
                    txtEquation.setText(txtEquation.getText().subSequence(0, txtEquation.getText().length() - 1));
                }
            }
        });

        //Creation of the button layout
        layoutBtn = new LinearLayout(context);
        layoutBtn.setOrientation(LinearLayout.VERTICAL);
        layoutBtn.addView(btnRetry);
        layoutBtn.addView(btnEreaseOne);
        layoutBtn.addView(btnClearText);
        layoutBtn.addView(btnBack);
        layoutBtn.addView(btnDone);

        setOrientation(LinearLayout.HORIZONTAL);
    }

    public Button getButtonClearText()
    {
        return btnClearText;
    }

    public Button getButtonCancel()
    {
        return btnBack;
    }

    /**
     * Getter de l'edittext pour l'eq
     *
     * @return EditText
     */
    public TextView getTextEquation() {
        return txtEquation;
    }

    /**
     * Getter du drawing view
     *
     * @return Drawing View
     */
    public DrawingView getDrawView() {
        return drawView;
    }

    /**
     * Getter du bouton finish
     *
     * @return Le bouton
     */
    public Button getDoneButton() {
        return btnDone;
    }

    /**
     * Set the layout for left-handed User
     */
    public void setLayoutForLeftHanded() {
        removeAllViewsInLayout();
        addView(layoutBtn);
        addView(drawView);
        addView(txtEquation);
    }

    /**
     * Set the layout for right-handed User
     */
    public void setLayoutForRightHanded() {
        removeAllViewsInLayout();
        addView(txtEquation);
        addView(drawView);
        addView(layoutBtn);
    }
}