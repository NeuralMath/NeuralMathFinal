package com.example.marc4492.neuralmath;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * custom keyboard
 * Created by Mathieu Boucher on 2017-03-13.
 */

public class MathKeyboard extends GridLayout {

    private Button[] key;
    private Button confirmBtn;

    private  CorrectionManager correctionManager;

    //the correction mode will keep in memory the modification of the equation and will
    // tell the neural network the correction to improve the accuracy of the neural network
    private boolean correctionMode;
    private boolean keyboardIsOpen;

    private ImageButton backspaceBtn;
    private MathEditText typingZone;
    private int screenWidth;
    private Handler backspaceHandler;

    private OnStringReadyListener listener;

    private String[] keyText = {


            "q", "w", //0 to 25 for letters
            "e", "r",
            "t", "y",
            "u", "i",
            "o", "p",
            "a", "s",
            "d", "f",
            "g", "h",
            "j", "k",
            "l", ".",
            "z", "x",
            "c", "v",
            "b", "n",

            "m", ",",

            "1", "2", // 28 to 37 for number
            "3", "4",
            "5", "6",
            "7", "8",
            "9", "0",

            "+", "-", //38 to 55 for symbols
            "•", "/",
            "÷", "=",
            "≠", "≤",
            "≥", "<",
            ">", "±",
            "(", ")",
            "[", "]",
            "!", "xy",


            //56 to 74 for greek letters and other
            "—", "∞",
            "|", "′",
            "ഽ", "√",
            "α", "π",
            "β", "Δ",
            "μ", "φ",
            "Σ", "θ",
            "λ", "ω",
            "δ", "σ",


            "sin", "cos", //75 to 83 for the functions
            "tan", "sin",
            "cos", "tan",
            "lim", "ln",
            "log", "",
            "", "",
            "", "",
            "", "",
            "", "",
    };

    public MathKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);

        setColumnCount(10);

        setBackgroundResource(R.color.Gray);

        //params for common buttons
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);

        //creating button and adding them to the layout
        key = new Button[41];
        for(int i = 0; i < 28; i++){
            key[i] = new Button(context);
            key[i].getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
            key[i].setLayoutParams(btnParams);
            key[i].setOnClickListener(keyClickListener);
            key[i].setText(keyText[i]);
            key[i].setTransformationMethod(null);
            addView(key[i]);
        }


        //Adding backspace Button
        backspaceBtn = new ImageButton(context);
        backspaceBtn.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
        backspaceBtn.setLayoutParams(btnParams);
        backspaceBtn.setImageResource(R.drawable.backspace);
        backspaceBtn.setOnTouchListener(backspaceTouchListener);
        addView(backspaceBtn);

        //button 27 to 30 are button to change between symbol and letters
        for(int i = 28; i < 32; i++){
            key[i] = new Button(context);
            key[i].getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
            key[i].setLayoutParams(btnParams);
            key[i].setOnClickListener(keyClickListener);
            key[i].setTransformationMethod(null);
            addView(key[i]);
        }

        key[28].setText("abc");
        key[28].getBackground().setColorFilter(new LightingColorFilter(0xAAAABB00, 0xFFAA0000)); //setting the button to be highlighted
        key[29].setText("#+=");
        key[30].setText("βΔഽ");
        key[31].setText("fnc");


        //button 32 to 41 are functions buttons
        for(int i = 32; i < 41; i++){
            key[i] = new Button(context);
            key[i].getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
            key[i].setLayoutParams(btnParams);
            key[i].setOnClickListener(keyClickListener);
            key[i].setTransformationMethod(null);
        }


        //Adding confirm Button
        confirmBtn = new Button(context);
        confirmBtn.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
        confirmBtn.setLayoutParams(btnParams);
        confirmBtn.setText(R.string.ok);
        confirmBtn.setOnClickListener(confirmClickListener);
        addView(confirmBtn);


        backspaceHandler = new Handler(); //initialization of the Handler

        keyboardIsOpen = false;

        //initialisation of the correction manager
        correctionManager = new CorrectionManager();
    }

    public void setListener(OnStringReadyListener listener) {
        this.listener = listener;
    }

    /**
     * This method open the mathematical keyboard
     * @param writingZone   The textView where we want to  write
     * @param screenW       The screen dimensions
     */
    public void openKeyboard(MathEditText writingZone, int screenW){
        if(!keyboardIsOpen){
            screenWidth = screenW;
            setKeyWidth();
            setVisibility(VISIBLE);
            typingZone = writingZone;
            keyboardIsOpen = true;
        }
    }



    /**
     * Setting the button layout param and the width
     */
    private void setKeyWidth(){
        int keyWidth = screenWidth / 10;


        for(int i = 0; i < 28; i++){
            ViewGroup.LayoutParams params = key[i].getLayoutParams();
            params.width = keyWidth;
            key[i].setLayoutParams(params);
        }

        for(int i = 28; i < 32; i++){
            //setting rowSpan
            GridLayout.LayoutParams LP = (GridLayout.LayoutParams)key[i].getLayoutParams();
            LP.columnSpec = GridLayout.spec((i - 28)*2, 2);
            key[i].setLayoutParams(LP);

            ViewGroup.LayoutParams params = key[i].getLayoutParams();
            params.width = keyWidth * 2;
            key[i].setLayoutParams(params);
        }

        //backspace button
        GridLayout.LayoutParams backspaceLP = (GridLayout.LayoutParams)backspaceBtn.getLayoutParams();
        backspaceLP.columnSpec = GridLayout.spec(8, 2);
        backspaceBtn.setLayoutParams(backspaceLP);

        ViewGroup.LayoutParams params = backspaceBtn.getLayoutParams();
        params.width = keyWidth * 2;
        backspaceBtn.setLayoutParams(params);

        //confirm button
        GridLayout.LayoutParams confirmLP = (GridLayout.LayoutParams)confirmBtn.getLayoutParams();
        confirmLP.columnSpec = GridLayout.spec(8, 2);
        confirmBtn.setLayoutParams(confirmLP);

        params = confirmBtn.getLayoutParams();
        params.width = keyWidth * 2;
        confirmBtn.setLayoutParams(params);
    }

    /**
     * Listener called when a key is pressed
     */
    final OnClickListener keyClickListener = new OnClickListener() {
        public void onClick(final View v) {
            Button clickedKey = (Button) v;
            SpannableStringBuilder exponentBuilder;
            switch(clickedKey.getText().toString()) {
                case "#+=":
                    changeToSymbols();
                    changeHighlightedBtn(clickedKey);
                    break;
                case "abc":
                    changeToLetters();
                    changeHighlightedBtn(clickedKey);
                    break;
                case "βΔഽ":
                    changeToGreek();
                    changeHighlightedBtn(clickedKey);
                    break;
                case "fnc":
                    changeToFunction();
                    changeHighlightedBtn(clickedKey);
                    break;
                case "lim":
                    if(!correctionMode) {
                        typingZone.getText().insert(typingZone.getSelectionStart(), "lim[⟶]");
                        typingZone.setSelection(typingZone.getSelectionStart() - 2);
                    }else if(correctionManager.getCorrectionCounter() >= 3 && correctionManager.hasDeletedCharAt(typingZone.getSelectionStart(), clickedKey.getText().length())){
                        for(int i = 0; i < clickedKey.getText().length(); i++)
                            correctionManager.addChar(clickedKey.getText().charAt(i), typingZone.getSelectionStart() + i);

                        typingZone.getText().insert(typingZone.getSelectionStart(), "lim");
                    }
                    break;
                case "sin":
                case "cos":
                case "tan":
                case "ln":
                case "log":
                case "ഽ":
                    if(!correctionMode){
                        typingZone.getText().insert(typingZone.getSelectionStart(), clickedKey.getText().toString() + "()");
                        typingZone.setSelection(typingZone.getSelectionStart() - 1);
                    }else if(correctionManager.getCorrectionCounter() >= clickedKey.getText().length() && correctionManager.hasDeletedCharAt(typingZone.getSelectionStart(), clickedKey.getText().length())){
                        for(int i = 0; i < clickedKey.getText().length(); i++)
                            correctionManager.addChar(clickedKey.getText().charAt(i), typingZone.getSelectionStart() + i);

                        typingZone.getText().insert(typingZone.getSelectionStart(), clickedKey.getText().toString());
                    }
                    break;
                case "sin-1":
                case "cos-1":
                case "tan-1":
                    if(!correctionMode) {
                        exponentBuilder = new SpannableStringBuilder(clickedKey.getText().toString() + "()");
                        exponentBuilder.setSpan(new SuperscriptSpan(), 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        exponentBuilder.setSpan(new RelativeSizeSpan(0.75f), 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        typingZone.getText().insert(typingZone.getSelectionStart(), exponentBuilder);
                        typingZone.setSelection(typingZone.getSelectionStart() - 1);
                    }else if(correctionManager.getCorrectionCounter() >= clickedKey.getText().length() && correctionManager.hasDeletedCharAt(typingZone.getSelectionStart(), 5)){
                        for(int i = 0; i < clickedKey.getText().length(); i++)
                            correctionManager.addChar(clickedKey.getText().charAt(i), typingZone.getSelectionStart() + i);

                        exponentBuilder = new SpannableStringBuilder(clickedKey.getText().toString());
                        exponentBuilder.setSpan(new SuperscriptSpan(), 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        exponentBuilder.setSpan(new RelativeSizeSpan(0.75f), 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        typingZone.getText().insert(typingZone.getSelectionStart(), exponentBuilder);
                        typingZone.setSelection(typingZone.getSelectionStart() - 1);
                    }
                    break;
                case "xy":
                    if(!correctionMode){
                        exponentBuilder = new SpannableStringBuilder("^()");
                        exponentBuilder.setSpan(new SuperscriptSpan(), 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        exponentBuilder.setSpan(new RelativeSizeSpan(0.75f), 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        typingZone.getText().insert(typingZone.getSelectionStart(), exponentBuilder);
                        typingZone.setSelection(typingZone.getSelectionStart() - 1);
                    }else if(correctionManager.getCorrectionCounter() > 0 && correctionManager.hasDeletedCharAt(typingZone.getSelectionStart())){
                        correctionManager.addChar('^', typingZone.getSelectionStart());
                        exponentBuilder = new SpannableStringBuilder("^");
                        exponentBuilder.setSpan(new SuperscriptSpan(), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        exponentBuilder.setSpan(new RelativeSizeSpan(0.75f), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        typingZone.getText().insert(typingZone.getSelectionStart(), exponentBuilder);
                    }


                    break;
                default:
                    if(!correctionMode)
                        typingZone.getText().insert(typingZone.getSelectionStart(), clickedKey.getText().toString());
                    else if(correctionManager.getCorrectionCounter() > 0 && correctionManager.hasDeletedCharAt(typingZone.getSelectionStart())){
                        correctionManager.addChar(clickedKey.getText().toString().charAt(0), typingZone.getSelectionStart());
                        typingZone.getText().insert(typingZone.getSelectionStart(), clickedKey.getText().toString());
                    }
                    break;
            }
        }
    };

    /**
     * Listener called when the backspace is touched
     */
    final OnTouchListener backspaceTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                String typingText = String.valueOf(typingZone.getText().subSequence(0 ,typingZone.getSelectionStart()));
                if(typingText.length() >= 5){
                    typingText = typingText.substring(typingText.length() - 5, typingText.length());
                    if(typingText.equals("sin-1")|| typingText.equals("cos-1") || typingText.equals("tan-1"))
                        for(int i = 0; i<4; i++){
                            if(typingZone.getSelectionStart() > 0 && correctionMode)
                                correctionManager.deleteChar(typingZone.getText().toString().charAt(typingZone.getSelectionStart() - 1), typingZone.getSelectionStart() - 1);
                            typingZone.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                        }
                }

                if(typingText.length() >= 3){
                    typingText = typingText.substring(typingText.length() - 3, typingText.length());
                    if (typingText.equals("sin")|| typingText.equals("cos") || typingText.equals("tan")|| typingText.equals("lim")|| typingText.equals("log"))
                        for(int i = 0; i<2; i++){
                            if(typingZone.getSelectionStart() > 0 && correctionMode)
                                correctionManager.deleteChar(typingZone.getText().toString().charAt(typingZone.getSelectionStart()- 1), typingZone.getSelectionStart() - 1);
                            typingZone.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                        }
                }

                if(typingText.length() >= 2){
                    typingText = typingText.substring(typingText.length() - 2, typingText.length());
                    if(typingText.equals("ln")){
                        if(typingZone.getSelectionStart() > 0 && correctionMode)
                            correctionManager.deleteChar(typingZone.getText().toString().charAt(typingZone.getSelectionStart() - 1), typingZone.getSelectionStart() - 1);
                        typingZone.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                    }
                }
                backspaceHandler.postDelayed(run, 0);
            }


            if(event.getAction() == MotionEvent.ACTION_UP)
                backspaceHandler.removeCallbacks(run);

            return true;
        }
    };

    /**
     * Listener called when the ok button is clicked
     */
    final OnClickListener confirmClickListener = new OnClickListener() {
        public void onClick(final View v) {
            String temp = typingZone.getText().toString();

            temp = temp.replaceAll("sin-1", "arcsin");
            temp = temp.replaceAll("cos-1", "arccos");
            temp = temp.replaceAll("tan-1", "arctan");
            temp = temp.replaceAll("•", "*");
            temp = temp.replaceAll("π", "/PI/");
            temp = temp.replaceAll("e", "/e/");
            temp = temp.replaceAll("÷", "/");


            typingZone.setText(temp);

            listener.done(temp);
        }
    };

    /**
     * change the current keyboard key to the symbols
     */
    private void changeToSymbols(){
        if(key[32].getText().toString().equals("sin")){
            for(int i = 0; i < 9; i++){
                removeViewAt(2*i);
                addView(key[2*i], 2*i);
                addView(key[2*i + 1], 2*i+ 1);
                key[32 + i].setText("");
            }
        }

        for(int i = 0; i < 28; i++)
            key[i].setText(keyText[i + 28]);

        SpannableStringBuilder exponentBuilder = new SpannableStringBuilder("xy");

        exponentBuilder.setSpan(new SuperscriptSpan(), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        exponentBuilder.setSpan(new RelativeSizeSpan(0.75f), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        key[27].setText(exponentBuilder);
    }

    /**
     * change the current keyboard key to letters
     */
    private void changeToLetters(){

        if(key[32].getText().toString().equals("sin")){
            for(int i = 0; i < 9; i++){
                removeViewAt(2*i);
                addView(key[2*i], 2*i);
                addView(key[2*i + 1], 2*i+ 1);
                key[32 + i].setText("");
            }
        }

        for (int i = 0; i < 28; i++)
            key[i].setText(keyText[i]);

        key[27].getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
    }

    /**
     * change the current keyboard key to greek letters and other
     */
    private void changeToGreek(){

        if(key[32].getText().toString().equals("sin")){
            for(int i = 0; i < 9; i++){
                removeViewAt(2*i);
                addView(key[2*i], 2*i);
                addView(key[2*i + 1], 2*i+ 1);
                key[32 + i].setText("");
            }
        }

        for (int i = 0; i < 10; i++)
            key[i].setText(keyText[i + 28]);

        for (int i = 10; i < 28; i++)
            key[i].setText(keyText[i + 46]);

        key[27].getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));

    }

    /**
     * change the current keyboard key to the function key
     */
    private void changeToFunction() {
        if(key[32].getText().toString().equals("")){
            for (int i = 0; i < 9; i++){
                removeViewAt(i);
                removeViewAt(i);
                addView(key[i + 32], i);

                //lim Button
                GridLayout.LayoutParams LimLP = (GridLayout.LayoutParams)key[32 + i].getLayoutParams();
                LimLP.columnSpec = GridLayout.spec((2*i)%10, 2);
                key[32 + i].setLayoutParams(LimLP);

                ViewGroup.LayoutParams params = key[32 + i].getLayoutParams();
                params.width = screenWidth /5;
                key[32 + i].setLayoutParams(params);

                key[32 + i].setText(keyText[i + 74]);
            }

            //setting the -1 in exponent
            for(int i = 0; i < 3; i++){
                SpannableStringBuilder exponentBuilder = new SpannableStringBuilder(keyText[77 + i] + "-1");

                exponentBuilder.setSpan(new SuperscriptSpan(), 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                exponentBuilder.setSpan(new RelativeSizeSpan(0.75f), 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                key[35 + i].setText(exponentBuilder);
            }

            for (int i = 0; i < 28; i++)
                key[i].setText("");

            key[27].getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
        }
    }

    /**
     * Change the highlighted button
     * @param btn the button tha we want to be highlighted
     */
    private void changeHighlightedBtn(Button btn){
        for(int i = 0; i < 4; i++)
            key[i + 28].getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));

        btn.getBackground().setColorFilter(new LightingColorFilter(0xAAAABB00, 0xFFAA0000));
    }

    /**
     * The runnable that calls delete while the key is held
     */
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if(typingZone.getSelectionStart() > 0 && correctionMode)
                correctionManager.deleteChar(typingZone.getText().toString().charAt(typingZone.getSelectionStart() - 1), typingZone.getSelectionStart() - 1);
            typingZone.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            backspaceHandler.postDelayed(run, 100);
        }
    };

    /**
     * able or disable the correction mode
     * @param mode true ---> the correction mode is on; false ---> the correction mode is off
     */
    public void setCorrectionMode(boolean mode){
        correctionMode = mode;
    }

    interface OnStringReadyListener {
        void done(String value);
    }

    /**
     * return the list of replaced char
     * @return      Arraylist des char replaced
     */
    public ArrayList<ReplacedChar> getReplacedCharList() {
        return correctionManager.getReplacedCharList();
    }
}
