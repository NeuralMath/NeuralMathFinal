package com.example.marc4492.neuralmath;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.FileOutputStream;

public class DrawingActivity extends AppCompatActivity {

    private DrawingPage drawPage;
    private ImageDecoder imageDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_layout);

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
                onBackPressed();
            }
        });

        Intent i = getIntent();
        imageDecoder = MainActivity.getImageDecoder();
        imageDecoder.setAppendMode(true);

        String value = i.getStringExtra("LAYOUT");

        if(value.equals("true"))
            drawPage.setLayoutForRightHanded();
        else
            drawPage.setLayoutForLeftHanded();
    }

    @Override
    public void onBackPressed() {
        sendEquation();
        imageDecoder.clearData();
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
     * @param btm       L'image
     */
    public void setBitmap(Bitmap btm)
    {
        try {
            FileOutputStream out;
            try {
                out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/NeuralMath/bob.jpg");
                btm.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            }

            drawPage.getTextEquation().append(imageDecoder.findSting(btm));
        }
        catch (Exception ex)
        {
            Log.e("DrawingActivity", "SetBitmap", ex);
        }
    }
}
