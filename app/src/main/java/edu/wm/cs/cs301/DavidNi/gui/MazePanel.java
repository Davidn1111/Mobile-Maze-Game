package edu.wm.cs.cs301.DavidNi.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class MazePanel extends View {

    // Current Color used by View
    private int color;
    // The Bitmap of the MazePanel
    private Bitmap mpBitmap;
    // Internal Canvas for MazePanel
    private Canvas mpCanvas;
    // Internal Paint for MazePanel
    private Paint mpPaint;

    /**
     * XML Constructor for MazePanel.
     * Initializes internal paint, color, bitmap, and canvas of MazePanel.
     * MazePanel is a custom view intended to display the maze game.
     * @param context Current Context
     * @param attributeSet Attributes of MazePanel specified by XML layout
     */
    public MazePanel (Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        // Create a new paint with default color red
        this.mpPaint = new Paint();
        this.color = Color.RED;
        mpPaint.setColor(color);
        // Initialize bitmap
        mpBitmap = Bitmap.createBitmap(310,284,Bitmap.Config.ARGB_8888);
        // Initialize canvas
        mpCanvas = new Canvas(mpBitmap);
    }

    /**
     * Method that renders the view contents (specified by this particular MazeView) onto a given canvas.
     * @param canvas Canvas object custom view uses to draw itself
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw Red Rectangle for testing
        Rect test = new Rect(0,0, 100, 100);
        canvas.drawRect(test,mpPaint);
        canvas.drawBitmap(mpBitmap,0,0,mpPaint);
    }
}
