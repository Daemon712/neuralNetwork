package ru.foobarbaz.neuralnetwork.som;

import ru.foobarbaz.neuralnetwork.function.distance.DistanceFunction;
import ru.foobarbaz.neuralnetwork.function.distance.ManhattanDistance;
import ru.foobarbaz.neuralnetwork.som.logic.SelfOrganizingMap;
import ru.foobarbaz.neuralnetwork.som.logic.SelfOrganizingMapImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Visual2DSomTest {
    private static final int CLUSTERS = 10;
    private static final int POINTS = 10;
    private static final int ERAS = 10;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test 2D Self-Organizing Map");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

        Visual2DSomTest logic = new Visual2DSomTest(CLUSTERS, new ManhattanDistance());
        double[][] points = new double[POINTS][2];
        for (int i = 0; i < points.length ; i++) {
            points[i][0] = Math.random() * 2 - 1;
            points[i][1] = Math.random() * 2 - 1;
        }
        logic.setPoints(points);
        List<BufferedImage> images = logic.studyAndCollectImages(ERAS);

        ImageIcon icon = new ImageIcon(images.get(0));
        JLabel label = new JLabel(icon);
        frame.add(label);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, images.size() - 1, 0);
        slider.setMajorTickSpacing(points.length);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.addChangeListener((e) -> {
            int value = slider.getValue();
            icon.setImage(images.get(value));
            label.repaint();
        });
        frame.add(slider);
        frame.pack();
        frame.setVisible(true);
    }

    private static final Color[] colors = new Color[]{
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.PINK

    };
    private int imageSize = 500;
    private double[][] points;
    private SelfOrganizingMap som;

    public Visual2DSomTest(int clusters, DistanceFunction distanceFunction){
        som = new SelfOrganizingMapImpl(distanceFunction, 2, clusters);
    }

    public List<BufferedImage> studyAndCollectImages(int eras){
        List<BufferedImage> images = new ArrayList<>(points.length * eras + 1);
        images.add(drawCanvas());

        for (int i = 0; i < eras; i++) {
            for (double[] point : points) {
                som.study(point);
                images.add(drawCanvas());
            }
            som.nextStudyingEra();
        }

        return images;
    }
    private BufferedImage drawKohonen(){

        return null;
    }

    private BufferedImage drawCanvas(){
        BufferedImage canvas = new BufferedImage(imageSize, imageSize,  BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                int cluster = som.process(new double[]{
                        convertCanvasToSom(x),
                        convertCanvasToSom(y)
                });
                int[] color = convertColorToIntArray(colors[cluster % colors.length]);
                canvas.getRaster().setPixel(x, y, color);
            }
        }

        drawPoints(canvas, points, 5, Color.white);
        drawPoints(canvas, som.getWeights(), 2, Color.black);

        return canvas;
    }

    private void drawPoints(BufferedImage canvas, double[][] points, int pointRadius, Color pointColor){
        for (double[] point : points) {
            int x = convertSomToCanvas(point[0]);
            int y = convertSomToCanvas(point[1]);
            for (int i = x - pointRadius; i <= x + pointRadius; i++) {
                for (int j = y - pointRadius; j <= y + pointRadius; j++) {
                    if (i >= 0 && i < imageSize && j >= 0 && j < imageSize){
                        canvas.getRaster().setPixel(i, j, convertColorToIntArray(pointColor));
                    }
                }
            }
        }
    }

    public void setPoints(double[][] points) {
        this.points = points;
    }

    private double convertCanvasToSom(int x){
        return 2 * (double) x / imageSize - 1;
    }

    private int convertSomToCanvas(double x){
        return (int)((x + 1) * imageSize / 2);
    }

    private int[] convertColorToIntArray(Color color){
        return new int[]{ color.getRed(), color.getGreen(), color.getBlue() };
    }
}
