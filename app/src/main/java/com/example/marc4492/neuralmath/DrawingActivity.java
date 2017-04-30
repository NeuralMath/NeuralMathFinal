package com.example.marc4492.neuralmath;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class DrawingActivity extends AppCompatActivity {

    private DrawingPage drawPage;
    private ImageDecoder imageDecoder;

    private MathKeyboard mathKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_layout);

        mathKeyboard = (MathKeyboard) findViewById(R.id.keyboardDrawing);
        mathKeyboard.setListener(new MathKeyboard.OnStringReadyListener() {
            @Override
            public void done(String value) {
                onBackPressed();
            }
        });

        drawPage = (DrawingPage) findViewById(R.id.drawPage);
        drawPage.getDrawView().setListener(new DrawingView.DrawnListener() {
            @Override
            public void drawn(Bitmap b) {
                setBitmap(b);
            }
        });
        drawPage.getDoneButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //http://stackoverflow.com/a/14292451/5224674
                //Pour passer la reponse à lactivité principale
                Intent intent = new Intent();
                intent.putExtra("EQUATION", drawPage.getTextEquation().getText().toString());
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        final int largeurScreen = displaymetrics.widthPixels;

        drawPage.getTxtEquation().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mathKeyboard.openKeyboard(drawPage.getTxtEquation(), largeurScreen);
            }
        });



        Intent i = getIntent();
        imageDecoder = MainActivity.getImageDecoder();

        String value = i.getStringExtra("LAYOUT");

        if(value.equals("true"))
            drawPage.setLayoutForRightHanded();
        else
            drawPage.setLayoutForLeftHanded();
    }

    @Override
    public void onBackPressed() {
        if(mathKeyboard.getVisibility() == View.VISIBLE)
            mathKeyboard.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }

    /**
     * Set le bitmap pour l'AI
     *
     * @param btm       L'image
     */
    public void setBitmap(Bitmap btm)
    {
        try {
            drawPage.getTextEquation().append(imageDecoder.findSting(btm));
        }
        catch (Exception ex)
        {
            Log.e("DrawingActivity", "SetBitmap", ex);
        }
    }
}
