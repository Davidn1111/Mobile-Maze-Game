package edu.wm.cs.cs301.DavidNi.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.core.content.ContextCompat;
import edu.wm.cs.cs301.DavidNi.R;


public class MazePanel extends View implements P5PanelF21{

    // Constant width and height shared among MazePanels
    private final int viewWidth = Constants.VIEW_WIDTH;
    private final int viewHeight = Constants.VIEW_HEIGHT;
    // Current Color used by View
    private int color;
    // The Bitmap of the MazePanel
    private Bitmap mpBitmap;
    // Internal Canvas for MazePanel
    private Canvas mpCanvas;
    // Internal Paint for MazePanel
    private Paint mpPaint;
    // Shader for walls
    private final BitmapShader wallShader = new BitmapShader(BitmapFactory.decodeResource(getResources(),R.drawable.wall),
            Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    // Shader for the sky in background
    private final BitmapShader skyShader = new BitmapShader(BitmapFactory.decodeResource(getResources(),R.drawable.skygame),
            Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    // Shader for the ground in background
    private final BitmapShader groundShader = new BitmapShader(BitmapFactory.decodeResource(getResources(),R.drawable.ground),
            Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);


    /**
     * XML Constructor for MazePanel.
     * Initializes internal paint, color, bitmap, and canvas of MazePanel.
     * MazePanel is a custom view intended to display the maze game.
     * @param context Current Context
     * @param attributeSet Attributes of MazePanel specified by XML layout
     */
    public MazePanel (Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        // Create a new paint with default color white
        mpPaint = new Paint();
        color = Color.WHITE;
        mpPaint.setColor(color);
        // Initialize bitmap
        mpBitmap = Bitmap.createBitmap(viewWidth,viewHeight,Bitmap.Config.ARGB_8888);
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
        canvas.drawBitmap(mpBitmap,0,0,mpPaint);
        Log.v("MazePanel","MazePanel onDraw called");
    }

    /**
     * Method to draw the buffer image on given Canvas obtained from the superclass.
     */
    public void update() {
        // Force view to be drawn on UI Canvas
        invalidate();
        Log.v("MazePanel","Committing image to UI");
    }

    /**
     * Commits all accumulated drawings to the UI.
     * Substitute for MazePanel.update method.
     */
    @Override
    public void commit() {
        // Force view to be drawn on UI Canvas
        invalidate();
        Log.v("MazePanel","Committing image to UI");
    }

    /**
     * Tells if instance is able to draw.
     * This ability depends on the MazePanel's Canvas existence (not being null).
     * @return true if drawing is possible, false if not.
     */
    @Override
    public boolean isOperational() {
        return (this.mpCanvas != null);
    }

    /**
     * Sets the color for future drawing requests. The color setting
     * will remain in effect till this method is called again
     * with a different color.
     * @param argb gives the alpha,red,green,and blue encoded value of the color
     */
    @Override
    public void setColor(int argb) {
        this.color = argb;
        this.mpPaint.setColor(argb);
        Log.v("MazePanel","Setting color");
    }

    /**
     * Returns the ARGB value for the current color setting.
     * @return integer ARGB value
     */
    @Override
    public int getColor() {
        return this.color;
    }

    /**
     * Helper method for addBackground()
     * Determine the background color for the top and bottom
     * rectangle as a blend between starting color settings
     * of yellowWM and lightGray towards goldWM and greenWM as final
     * color settings close to the exit
     * @param percentToExit describes how far it is to the exit as a percentage value
     * @param top is true for the top rectangle, false for the bottom
     * @return the color to use for the background rectangle
     */
    private int getBackgroundColor(float percentToExit, boolean top) {
        // Colors for compass are set in color
        Color greenWM = Color.valueOf(ContextCompat.getColor(getContext(), R.color.greenWM));
        Color goldWM = Color.valueOf(ContextCompat.getColor(getContext(), R.color.goldWM));
        Color yellowWM = Color.valueOf(ContextCompat.getColor(getContext(), R.color.yellowWM));

        return top? blend(yellowWM, goldWM, percentToExit) :
                blend(Color.valueOf(Color.LTGRAY), greenWM, percentToExit);
    }

    /**
     * Helper method for getBackgroundColor()
     * Calculates the weighted average of the two given colors.
     * The weight for the first color is expected to be between
     * 0 and 1. The weight for the other color is then 1-weight0.
     * The result is the weighted average of the red, green, and
     * blue components of the colors. The resulting alpha value
     * for transparency is the max of the alpha values of both colors.
     * @param fstColor is the first color
     * @param sndColor is the second color
     * @param weightFstColor is the weight of fstColor, {@code 0.0 <= weightFstColor <= 1.0}
     * @return blend of both colors as weighted average of their rgb values
     */
    private int blend(Color fstColor, Color sndColor, float weightFstColor) {
        if (weightFstColor < 0.1)
            return sndColor.toArgb();
        if (weightFstColor > 0.95)
            return fstColor.toArgb();
        float r = weightFstColor * fstColor.red() + (1-weightFstColor) * sndColor.red();
        float g = weightFstColor * fstColor.green() + (1-weightFstColor) * sndColor.green();
        float b = weightFstColor * fstColor.blue() + (1-weightFstColor) * sndColor.blue();
        float a = Math.max(fstColor.alpha(), sndColor.alpha());

        // Create mix color used calculated argb values
        Color mix = Color.valueOf(r,g,b,a);
        // Return the argb value of the mixed color
        return mix.toArgb();
    }

    /**
     * Draws two solid rectangles to provide a background.
     * Note that this also erases any previous drawings.
     * Top half of the background is the sky, painted using skyShader.
     * Bottom half of the background is the ground, painted using groundShader.
     * @param percentToExit gives the distance to exit
     */
    @Override
    public void addBackground(float percentToExit) {
        Paint backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        // Draw the sky
        backgroundPaint.setShader(skyShader);
        mpCanvas.drawRect(0, 0, viewWidth, viewHeight/2,backgroundPaint);
        // Draw the ground
        backgroundPaint.setShader(groundShader);
        mpCanvas.drawRect(0, viewHeight/2, viewWidth, viewHeight,backgroundPaint);

        /*
        // dynamic color setting top rectangle:
        setColor(getBackgroundColor(percentToExit, true));
        this.addFilledRectangle(0, 0, viewWidth, viewHeight/2);
        // dynamic color setting bottom rectangle:
        setColor(getBackgroundColor(percentToExit, false));
        this.addFilledRectangle(0, viewHeight/2, viewWidth, viewHeight);
         */
        Log.v("MazePanel","Drawing background with percent: " + percentToExit);
    }

    /**
     * Adds a filled rectangle.
     * The rectangle is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis.
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the rectangle
     * @param height is the height of the rectangle
     */
    @Override
    public void addFilledRectangle(int x, int y, int width, int height) {
        mpPaint.setStyle(Paint.Style.FILL);
        mpCanvas.drawRect(x,y,x+width,y+height,mpPaint);

        Log.v("MazePanel","Drawing filled rectangle");
    }

    /**
     * Adds a filled polygon.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    @Override
    public void addFilledPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        // New path representing polygon
        Path polyPath = new Path();
        // Start path at xPoints[0],yPoints[0]
        polyPath.moveTo(xPoints[0],yPoints[0]);
        // Draw lines according to given x and y points
        for(int i = 1; i < xPoints.length;i++) {
            polyPath.lineTo(xPoints[i],yPoints[i]);
        }
        // Finish path by returning to original point
        polyPath.lineTo(xPoints[0],yPoints[0]);
        // Set paint style to be FILL for a filled polygon
        mpPaint.setStyle(Paint.Style.FILL);
        // Draw polygon on internal canvas
        mpCanvas.drawPath(polyPath,mpPaint);

        Log.v("MazePanel","Drawing filled polygon");
    }

    public void addWall(int[] xPoints,int[] yPoints, int nPoints){
        // New path representing polygon
        Path polyPath = new Path();
        // Start path at xPoints[0],yPoints[0]
        polyPath.moveTo(xPoints[0],yPoints[0]);
        // Draw lines according to given x and y points
        for(int i = 1; i < xPoints.length;i++) {
            polyPath.lineTo(xPoints[i],yPoints[i]);
        }
        // Finish path by returning to original point
        polyPath.lineTo(xPoints[0],yPoints[0]);

        // Paint wall using wall shader
        Paint wallPaint  = new Paint();
        wallPaint.setStyle(Paint.Style.FILL);
        wallPaint.setShader(wallShader);
        // Draw polygon on internal canvas
        mpCanvas.drawPath(polyPath,wallPaint);

        // Add black border for wall
        setColor(Color.BLACK);
        mpPaint.setStrokeWidth(7f);
        addPolygon(xPoints,yPoints,nPoints);
        mpPaint.setStrokeWidth(0f);

        Log.v("MazePanel","Drawing filled polygon");
    }

    /**
     * Adds a polygon.
     * The polygon is not filled.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    @Override
    public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        // New path representing polygon
        Path polyPath = new Path();
        // Start path at xPoints[0],yPoints[0]
        polyPath.moveTo(xPoints[0],yPoints[0]);
        // Draw lines according to given x and y points
        for(int i = 1; i < xPoints.length;i++) {
            polyPath.lineTo(xPoints[i],yPoints[i]);
        }
        // Finish path by returning to original point
        polyPath.lineTo(xPoints[0],yPoints[0]);
        // Set paint style to be STROKE for an unfilled polygon
        mpPaint.setStyle(Paint.Style.STROKE);
        // Draw polygon on internal canvas
        mpCanvas.drawPath(polyPath,mpPaint);

        Log.v("MazePanel","Drawing unfilled polygon");
    }

    /**
     * Adds a line.
     * A line is described by {@code (x,y)} coordinates for its
     * starting point and its end point.
     * Uses stroke width of 4.
     * @param startX is the x-coordinate of the starting point
     * @param startY is the y-coordinate of the starting point
     * @param endX is the x-coordinate of the end point
     * @param endY is the y-coordinate of the end point
     */
    @Override
    public void addLine(int startX, int startY, int endX, int endY) {
        mpPaint.setStyle(Paint.Style.FILL);
        mpPaint.setStrokeWidth(4f);
        mpCanvas.drawLine(startX,startY,endX,endY,mpPaint);
        mpPaint.setStrokeWidth(0f);

        Log.v("MazePanel","Drawing line");
    }

    /**
     * Adds a filled oval.
     * The oval is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis.
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the oval
     * @param height is the height of the oval
     */
    @Override
    public void addFilledOval(int x, int y, int width, int height) {
        // Set the paint style to fill
        mpPaint.setStyle(Paint.Style.FILL);
        // Draw oval on internal canvas
        mpCanvas.drawOval(x,y,x+width,y+height,mpPaint);

        Log.v("MazePanel","Drawing oval");
    }

    /**
     * Adds the outline of a circular or elliptical arc covering the specified rectangle.
     * The resulting arc begins at startAngle and extends for arcAngle degrees,
     * using the current color.
     * @param x the x coordinate of the upper-left corner of the arc to be drawn.
     * @param y the y coordinate of the upper-left corner of the arc to be drawn.
     * @param width the width of the arc to be drawn.
     * @param height the height of the arc to be drawn.
     * @param startAngle the beginning angle.
     * @param arcAngle the angular extent of the arc, relative to the start angle.
     */
    @Override
    public void addArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        mpPaint.setStyle(Paint.Style.STROKE);
        // Create RectF to represent arc
        RectF arc = new RectF(x,y,x+width,y+height);
        // Add arc to internal canvas
        mpCanvas.drawArc(arc,startAngle,arcAngle,false,mpPaint);

        Log.v("MazePanel","Drawing arc");
    }

    /**
     * Adds a string at the given position using default android font at size 40.
     * Used to label compass directions for maze game.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param str the string
     */
    @Override
    public void addMarker(float x, float y, String str) {
        mpPaint.setStyle(Paint.Style.FILL);
        mpPaint.setTextSize(40);
        // Adjust placement due to centering issue for compass
        mpCanvas.drawText(str,x-13,y+13,mpPaint);

        Log.v("MazePanel","Adding to canvas, text: "+ str);
    }

    /**
     * Method prints a Log warning message describing given hint and its value.
     * @param hintKey the key of the hint to be set.
     * @param hintValue the value indicating preferences for the specified hint category.
     */
    @Override
    public void setRenderingHint(P5RenderingHints hintKey, P5RenderingHints hintValue) {
        Log.v("MazePanel", "Hint: " + hintKey + ": " + hintValue);
    }

    /**
     * Method that prints out a static image to test if MazePanel methods are working as intended.
     * Image consists of a black background, red circle, green oval, yellow rectangle,
     * blue triangle/3-gon, several cyan lines, and white text "Hello World".
     * @param c Given canvas to draw the images
     */
    private void myTestImage(Canvas c) {
        // Black background
        setColor(Color.BLACK);
        addFilledRectangle(0,0,viewWidth,viewHeight);

        // Red circle
        setColor(Color.RED);
        addFilledOval(0,0,200,200);

        // Green oval
        setColor(Color.GREEN);
        addFilledOval(350,200,50,50);

        // Yellow Rectangle
        setColor(Color.YELLOW);
        addFilledRectangle(400,500,300,300);

        // Blue Polygon (triangle)
        setColor(Color.BLUE);
        int[] x = {100,700,200};
        int[] y = {300,850,600};
        addFilledPolygon(x,y,3);

        // Cyan lines
        setColor(Color.CYAN);
        addLine(0,0,500,1000);
        addLine(0,0,300,1000);
        addLine(30,70,500,788);
        addLine(20,800,400,900);

        // White text of "Hello World!"
        setColor(Color.WHITE);
        addMarker(700,900,"Hello World!");

        Log.v("MazePanel","Drawing test image");
    }
}