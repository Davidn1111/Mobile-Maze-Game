package edu.wm.cs.cs301.DavidNi.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import edu.wm.cs.cs301.DavidNi.gui.P5PanelF21.P5RenderingHints;

public class MazePanel extends View implements P5PanelF21{

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

    /**
     * Method to draw the buffer image on a graphics object that is
     * obtained from the superclass.
     * Warning: do not override getGraphics() or drawing might fail.
     */
    public void update() {
        //paint(getGraphics());
    }

    /**
     * Commits all accumulated drawings to the UI.
     * Substitute for MazePanel.update method.
     */
    @Override
    public void commit() {
        //update();
    }

    /**
     * Tells if instance is able to draw.
     * This ability depends on the MazePanel's graphic existing (not being null).
     * Substitute for code that checks if graphics object for drawing is not null.
     * @return true if drawing is possible, false if not.
     */
    @Override
    public boolean isOperational() {
        //return (graphics != null);
        return false;
    }

    /**
     * Sets the color for future drawing requests. The color setting
     * will remain in effect till this method is called again
     * with a different color.
     * Substitute for Graphics.setColor method.
     * @param argb gives the alpha,red,green,and blue encoded value of the color
     */
    @Override
    public void setColor(int argb) {
        /*
        // Use bitshift and masking to get argb values from hexcode.
        int alpha = (argb >> 24) & 0xff;
        int red = (argb >> 16) & 0xff;
        int green = (argb >> 8) & 0xff;
        int blue = argb & 0xff;

        // Set the color of graphics to the color described by given rgb values
        this.color = new Color(red,green,blue,alpha);
        this.rgbInt = argb;
        this.graphics.setColor(color);
         */
    }

    /**
     * Returns the ARGB value for the current color setting.
     * @return integer ARGB value
     */
    @Override
    public int getColor() {
        //return rgbInt;
        return 0;
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
    private Color getBackgroundColor(float percentToExit, boolean top) {
        /*
        final Color greenWM = Color.decode("#115740");
        final Color goldWM = Color.decode("#916f41");
        final Color yellowWM = Color.decode("#FFFF99");

        return top? blend(yellowWM, goldWM, percentToExit) :
                blend(Color.lightGray, greenWM, percentToExit);
         */
        return Color.valueOf(0xffff0000);
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
    private Color blend(Color fstColor, Color sndColor, double weightFstColor) {
        /*
        if (weightFstColor < 0.1)
            return sndColor;
        if (weightFstColor > 0.95)
            return fstColor;
        double r = weightFstColor * fstColor.getRed() + (1-weightFstColor) * sndColor.getRed();
        double g = weightFstColor * fstColor.getGreen() + (1-weightFstColor) * sndColor.getGreen();
        double b = weightFstColor * fstColor.getBlue() + (1-weightFstColor) * sndColor.getBlue();
        double a = Math.max(fstColor.getAlpha(), sndColor.getAlpha());

        return new Color((int) r, (int) g, (int) b, (int) a);
        */
        return Color.valueOf(0xffff0000);
    }

    /**
     * Draws two solid rectangles to provide a background.
     * Note that this also erases any previous drawings.
     * The color setting adjusts to the distance to the exit to
     * provide an additional clue for the user.
     * Colors transition from black to gold and from grey to green.
     * Substitute for FirstPersonView.drawBackground method.
     * @param percentToExit gives the distance to exit
     */
    @Override
    public void addBackground(float percentToExit) {
        /*
        // black rectangle in upper half of screen
        // graphics.setColor(Color.black);
        // dynamic color setting:
        graphics.setColor(getBackgroundColor(percentToExit, true));
        graphics.fillRect(0, 0, viewWidth, viewHeight/2);
        // grey rectangle in lower half of screen
        // graphics.setColor(Color.darkGray);
        // dynamic color setting:
        graphics.setColor(getBackgroundColor(percentToExit, false));
        graphics.fillRect(0, viewHeight/2, viewWidth, viewHeight/2);
        */
    }

    /**
     * Adds a filled rectangle.
     * The rectangle is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis.
     * Substitute for Graphics.fillRect() method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the rectangle
     * @param height is the height of the rectangle
     */
    @Override
    public void addFilledRectangle(int x, int y, int width, int height) {
        //graphics.fillRect(x, y, width, height);

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
     * Substitute for Graphics.fillPolygon() method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    @Override
    public void addFilledPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        //graphics.fillPolygon(xPoints, yPoints, nPoints);
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
     * Substitute for Graphics.drawPolygon method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    @Override
    public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        //graphics.drawPolygon(xPoints, yPoints, nPoints);
    }

    /**
     * Adds a line.
     * A line is described by {@code (x,y)} coordinates for its
     * starting point and its end point.
     * Substitute for Graphics.drawLine method
     * @param startX is the x-coordinate of the starting point
     * @param startY is the y-coordinate of the starting point
     * @param endX is the x-coordinate of the end point
     * @param endY is the y-coordinate of the end point
     */
    @Override
    public void addLine(int startX, int startY, int endX, int endY) {
        //graphics.drawLine(startX, startY, endX, endY);
    }

    /**
     * Adds a filled oval.
     * The oval is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis.
     * Substitute for Graphics.fillOval method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the oval
     * @param height is the height of the oval
     */
    @Override
    public void addFilledOval(int x, int y, int width, int height) {
        //graphics.fillOval(x, y, width, height);
    }

    /**
     * Adds the outline of a circular or elliptical arc covering the specified rectangle.
     * The resulting arc begins at startAngle and extends for arcAngle degrees,
     * using the current color.
     * Substitute for Graphics.drawArc method
     * @param x the x coordinate of the upper-left corner of the arc to be drawn.
     * @param y the y coordinate of the upper-left corner of the arc to be drawn.
     * @param width the width of the arc to be drawn.
     * @param height the height of the arc to be drawn.
     * @param startAngle the beginning angle.
     * @param arcAngle the angular extent of the arc, relative to the start angle.
     */
    @Override
    public void addArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        //graphics.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    /**
     * Adds a string at the given position using font "Serif-PLAIN-16".
     * Substitute for CompassRose.drawMarker method
     * @param x the x coordinate
     * @param y the y coordinate
     * @param str the string
     */
    @Override
    public void addMarker(float x, float y, String str) {
        /*
        // Create a markerFont, always set to "Serif-PLAIN-16"
        Font markerFont = Font.decode("Serif-PLAIN-16");

        GlyphVector gv = markerFont.createGlyphVector(graphics.getFontRenderContext(), str);
        Rectangle2D rect = gv.getVisualBounds();
        // need to update x, y by half of rectangle width, height
        // to serve as x, y coordinates for drawing a GlyphVector
        x -= rect.getWidth() / 2;
        y += rect.getHeight() / 2;

        graphics.drawGlyphVector(gv, x, y);
        */
    }

    @Override
    public void setRenderingHint(P5RenderingHints hintKey, P5RenderingHints hintValue) {

    }
}
