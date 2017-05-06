package com.example.marc4492.neuralmath;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class DrawingActivity extends AppCompatActivity {

    private DrawingPage drawPage;
    private ImageDecoder imageDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_layout);

        Intent i = getIntent();
        imageDecoder = MainActivity.getImageDecoder();
        imageDecoder.setAppendMode(true);
        String value = i.getStringExtra("LAYOUT");
        drawPage = (DrawingPage) findViewById(R.id.drawPage);

        String langue = getIntent().getStringExtra("LANGUE");

        MainActivity.changementDeLangue(langue, this);

        drawPage.getDrawView().setListener(new DrawingView.DrawnListener() {
            @Override
            public void drawn(Bitmap b) {
                setBitmap(b);
            }
        });
        drawPage.getDoneButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        drawPage.getButtonClearText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawPage.getTextEquation().setText("");
                imageDecoder.clearData();
            }
        });



        if (value.equals("true"))
            drawPage.setLayoutForRightHanded();
        else
            drawPage.setLayoutForLeftHanded();
    }

    @Override
    public void onBackPressed() {
        sendEquation();
        super.onBackPressed();
    }

    public void sendEquation() {
        Intent intent = new Intent();
        intent.putExtra("EQUATION", drawPage.getTextEquation().getText().toString());
        setResult(RESULT_OK, intent);
    }

    /**
     * Set le bitmap pour l'AI
     *
     * @param btm L'image
     */
    public void setBitmap(Bitmap btm) {
        try {
            drawPage.getTextEquation().append(imageDecoder.findSting(btm));
        } catch (Exception ex) {
            Log.e("DrawingActivity", "SetBitmap", ex);
        }
    }
}
