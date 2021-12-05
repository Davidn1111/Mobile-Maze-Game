package edu.wm.cs.cs301.DavidNi.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.core.content.ContextCompat;
import edu.wm.cs.cs301.DavidNi.R;


public class MazePanel extends View implements P5PanelF21{

    // Constant width and height shared among MazePanels
    final int viewWidth = 1050;
    final int viewHeight = 1050;
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
        // Draw the Test Image
        myTestImage(canvas);
        canvas.drawBitmap(mpBitmap,0,0,mpPaint);
    }

    /**
     * Method to draw the buffer image on given Canvas obtained from the superclass.
     */
    public void update() {
        // Force view to be drawn on given Canvas
        invalidate();
    }

    /**
     * Commits all accumulated drawings to the UI.
     * Substitute for MazePanel.update method.
     */
    @Override
    public void commit() {
        invalidate();
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
     * The color setting adjusts to the distance to the exit to
     * provide an additional clue for the user.
     * Colors transition from black to gold and from grey to green.
     * @param percentToExit gives the distance to exit
     */
    @Override
    public void addBackground(float percentToExit) {
        // dynamic color setting top rectangle:
        setColor(getBackgroundColor(percentToExit, true));
        this.addFilledRectangle(0, 0, viewWidth, viewHeight/2);
        // dynamic color setting bottom rectangle:
        setColor(getBackgroundColor(percentToExit, false));
        this.addFilledRectangle(0, viewHeight/2, viewWidth, viewHeight);
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
        mpCanvas.drawRect(x,y,width,height,mpPaint);
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
        polyPath.moveTo(xPoints[0],yPoints[0]);
        // Set paint style to be FILL for a filled polygon
        mpPaint.setStyle(Paint.Style.FILL);
        // Draw polygon on internal canvas
        mpCanvas.drawPath(polyPath,mpPaint);
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
        polyPath.moveTo(xPoints[0],yPoints[0]);
        // Set paint style to be STROKE for an unfilled polygon
        mpPaint.setStyle(Paint.Style.STROKE);
        // Draw polygon on internal canvas
        mpCanvas.drawPath(polyPath,mpPaint);
    }

    /**
     * Adds a line.
     * A line is described by {@code (x,y)} coordinates for its
     * starting point and its end point.
     * @param startX is the x-coordinate of the starting point
     * @param startY is the y-coordinate of the starting point
     * @param endX is the x-coordinate of the end point
     * @param endY is the y-coordinate of the end point
     */
    @Override
    public void addLine(int startX, int startY, int endX, int endY) {
        mpPaint.setStyle(Paint.Style.FILL);
        mpCanvas.drawLine(startX,startY,endX,endY,mpPaint);
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
        mpCanvas.drawOval(x,y,width,height,mpPaint);
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
        RectF arc = new RectF(x,y,width,height);
        // Add arc to internal canvas
        mpCanvas.drawArc(arc,startAngle,arcAngle,false,mpPaint);
    }

    /**
     * Adds a string at the given position using default android font at size 30
     * @param x the x coordinate
     * @param y the y coordinate
     * @param str the string
     */
    @Override
    public void addMarker(float x, float y, String str) {
        mpPaint.setStyle(Paint.Style.FILL);
        mpPaint.setTextSize(30);
        mpCanvas.drawText(str,x,y,mpPaint);
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
     * blue triangle/3-gon, and several cyan lines.
     * @param c Given canvas to draw the images
     */
    private void myTestImage(Canvas c) {
        // Black background
        setColor(Color.BLACK);
        addFilledRectangle(0,0,1000,1000);
        // Red circle
        setColor(Color.RED);
        addFilledOval(0,0,200,200);
        // Green oval
        setColor(Color.GREEN);
        addFilledOval(350,200,50,50);
        // Yellow Rectangle
        setColor(Color.YELLOW);
        addFilledRectangle(800,800,300,300);
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
    }
}
