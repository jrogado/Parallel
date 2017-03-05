/*
  Created by Jose Rogado on 22-02-2017.
  Mandelbrot set generation using ForkJoinPool and RecursiveAction
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import javax.swing.*;

import static java.lang.Runtime.getRuntime;

public class MandelbrotForkJoin extends JFrame {
    // Image Dimensions
    private static int sizeX = 4096;
    private static int sizeY = 4096;
    // Visible zone coordinates
	private static double CxMin = -2.5;
    private static double CxMax = 1.5;
    private static double CyMin = -2.0;
	private static double CyMax = 2.0;
	// Pixel size
    private static double pixelWidth = (CxMax - CxMin) / sizeX;
    private static double pixelHeight = (CyMax - CyMin) / sizeX;

    // Color map for painting
    private static int colorMap[];
    private static int colorMapSize;
    // Number of iterations
    private final int MAXITERATIONS = 200;
    // Fork/Join Threshold
    private static int forkjoinThreshold = 128;
    // The image buffer
    private BufferedImage image;

    private double zx, zy, cX, cY, tmp;

    private final ForkJoinPool forkJoinPool;

    private MandelbrotForkJoin() {

        super("Mandelbrot Set");

        setBounds(0, 0, sizeX, sizeY);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        forkJoinPool = new ForkJoinPool();
        int nCores = getRuntime().availableProcessors();

        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        System.out.println("Generating fractal with " + getWidth() +" by "
                + getHeight() + " pixels using " + forkJoinPool.getParallelism() + " threads and " + nCores + " cores");
        forkJoinPool.invoke(new taskMandel(0,1));
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

    public static void main(String[] args) {
        initColorMap(32, Color.RED, Color.GREEN, Color.BLUE);
        long startTime = System.currentTimeMillis();
        new MandelbrotForkJoin().setVisible(false);
        long stopTime = System.currentTimeMillis();
        long parallelTime = stopTime - startTime;
        System.out.println("Generating fractal with " + forkjoinThreshold + " tasks took " + parallelTime + " ms");
    }
    /**
     *
     * Uses RecursiveAction to calculate the mandelbrot of each X,Y point
     *
     */
    class taskMandel extends RecursiveAction {

        private int line;
        private int slices;

        private taskMandel(int line, int slices) {
            this.line = line;
            this.slices = slices;
        }

        private void computeSerial() {
            // System.out.println("computeSerial line: " + line +" , slices " + slices);
            // Slice dimension in coordinates
            double partWidth = CxMax - CxMin;
            double partHeight = (CyMax - CyMin) / slices;
            // Slice dimension in pixels
            int partSizeX = sizeX;
            int partSizeY = sizeY / slices;
            int lineSize = partSizeY*sizeX;
            // Starting point coordinates
            double cx0 = CxMin;
            double cy0 = CyMin + line*partHeight;
            // Iterate for each line in slice
            for (int iy = 0; iy < partSizeY; iy++) {
                double cy = cy0 + iy * pixelHeight;
                // Iterate for all pixel in line
                for (int ix = 0; ix < partSizeX; ix++) {
                    double cx = cx0 + ix * pixelWidth;
                    double x = 0;
                    double y = 0;
                    double xx = 0;
                    double yy = 0;

                    double magnitudeSquared = 0;
                    int iteration = 0;
                    int color;
                    // Calculate Mandel function for point (cx, cy)
                    while ((iteration < MAXITERATIONS) && (magnitudeSquared < 4))
                    {
                        y = 2*x*y + cy;
                        x = xx-yy + cx;
                        xx = x*x;
                        yy = y*y;
                        magnitudeSquared = xx+yy;
                        iteration++;
                    }
                    if (iteration == MAXITERATIONS)
                    { /*  interior of Mandelbrot set = black */
                        color = 0;
                    }
                    else
                    { /* exterior of Mandelbrot set = colorMap */
                        color = colorMap[iteration%colorMapSize];
                    }
                    // Calculate absolute pixel position
                    int py = line*partSizeY + iy;
                    // int px = ix;
                    image.setRGB(ix, py, color);
                }
            }
        }

        @Override
        protected void compute() {
            if (slices >= forkjoinThreshold) {
                computeSerial();
                return;
            }
            int newSlices = 2*slices;
            int newLine = 2*line;
            // Needs validation !!
            invokeAll(new taskMandel(newLine, newSlices), new taskMandel(newLine+1, newSlices));
        }
    }

    /**
     * Creates the colorMap array which contains RGB colors as integers,
     * interpolated through the given colors with colors.length * stepSize
     * steps
     *
     * @param stepSize The number of interpolation steps between two colors
     * @param colors The colors for the map
     */
    private static  void initColorMap(int stepSize, Color ... colors)
    {
        colorMap = new int[stepSize*colors.length];
        int index = 0;
        for (int i=0; i<colors.length-1; i++)
        {
            Color c0 = colors[i];
            int r0 = c0.getRed();
            int g0 = c0.getGreen();
            int b0 = c0.getBlue();

            Color c1 = colors[i+1];
            int r1 = c1.getRed();
            int g1 = c1.getGreen();
            int b1 = c1.getBlue();

            int dr = r1-r0;
            int dg = g1-g0;
            int db = b1-b0;

            for (int j=0; j<stepSize; j++)
            {
                double alpha = (float)j / (stepSize-1);
                int r = (int)(r0 + alpha * dr);
                int g = (int)(g0 + alpha * dg);
                int b = (int)(b0 + alpha * db);
                int rgb =
                        (r << 16) | (g <<  8) | (b);
                colorMap[index++] = rgb;
            }
        }
        colorMapSize = colorMap.length;
        System.out.println("ColorMap Size: " + colorMapSize);

    }
}
