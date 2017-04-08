package com.example.marc4492.neuralmath;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;


/**
 * Author: Created by Mathieu on 2017-02-22.
 * This is a view that allow the user to draw on the screen and that save a png of the drawing
 *
 * Code based on a tutorial by Sue Smith
 * link: https://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-essential-functionality--mobile-19328
 */

public class DrawingView extends View {

    Paint drawPaint;
    private Path path = new Path();

    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private Handler saveHandler;

    private DrawnListener listener;

    /**
     * Constructeur par défault
     *
     * @param c     Le context de l'App
     */
    public DrawingView(Context c) {
        super(c);

        drawPaint = new Paint(Paint.DITHER_FLAG);
        drawPaint.setAntiAlias(true);
        drawPaint.setColor(Color.parseColor("#000000"));
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(20);
        setWillNotDraw(false);

        saveHandler = new Handler();

        setBackgroundResource(R.drawable.colored_border);
    }

    @Override
    protected void onSizeChanged(int w, int h, int width, int height) {
        super.onSizeChanged(w, h, width, height);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
        canvas.drawPath(path, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(path, drawPaint);
                path.reset();
                break;
            default:
                return false;
        }
        invalidate();

        //Creation of a saving timer that save only at the end of the drawing
        saveHandler.removeCallbacks(run); //remove the saving timer
        saveHandler.postDelayed(run, 2000); //create a new saving timer

        return true;
    }

    /**
     * Clear the canvas and stop the saving timer
     */
    public void clear(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
        saveHandler.removeCallbacks(run);
    }

    /**
     * Set le listener pour le callback
     *
     * @param dL        Le listener
     */
    public void setListener(DrawnListener dL)
    {
        listener = dL;
    }

    /**
     * The runnable that calls saveCharacter and clear functions after a set delay
     */
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            setDrawingCacheEnabled(true);
            listener.drawn(getDrawingCache());
            setDrawingCacheEnabled(false);
            clear();
        }
    };

    /**
     * Un listener custom pour l'event quand le user a fini d'ecrire
     */
    public interface DrawnListener
    {
        void drawn(Bitmap b);
    }
}