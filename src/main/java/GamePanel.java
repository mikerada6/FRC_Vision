import image.ProcessImage;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class GamePanel extends JPanel implements Runnable {
    public static final int FPS = 100;
    public static final int WIDTH = 640;
    public static final int HEIGHT = 640;
    public static int totalFrameCount;
    public boolean running;
    private BufferedImage image;
    private Graphics2D g;
    private double averageFPS;
    private Thread thread;

    private Mat imageArray;
    private VideoCapture videoDevice;
    private ProcessImage processImage;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        BufferedImage img = null;
        totalFrameCount = 0;

        OpenCV.loadLocally();

        imageArray = new Mat();
        // Video device acces
        videoDevice = new VideoCapture();
        // 0:Start default video device 1,2 etc video device id
        videoDevice.open(0);
        processImage = new ProcessImage();
    }

    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void run() {
        running = true;

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();

        long startTime;
        long URDTimeMillis;
        long waitTime;
        long totalTime = 0;

        int frameCount = 0;
        int maxFrameCount = FPS;

        long targetTime = 1000 / FPS;


        while (running) {

            startTime = System.nanoTime();
            g = (Graphics2D) image.getGraphics();
            gameUpdate();
            gameRender();
            gameDraw();
            totalFrameCount++;
            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - URDTimeMillis;

            try {
                Thread.sleep(waitTime);
            } catch (Exception e) {

            }
            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == maxFrameCount) {
                averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000.0);
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    public void gameUpdate() {
        if (videoDevice.isOpened()) {
            if (videoDevice.read(imageArray)) {
                processImage.process(imageArray);
                processImage.drawRectoangles(imageArray,true);
            }
        }

    }

    public void gameRender() {
        image = processImage.Mat2BufferedImage(imageArray);
        g = (Graphics2D) image.getGraphics();
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + averageFPS, 20, 80);
    }

    public void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

    }
}
