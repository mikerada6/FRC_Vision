import image.ProcessImage;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;

public class Camera {
    public static void main(String[] args) {

        // Load Native Library
        OpenCV.loadLocally();

        // image container object
        Mat imageArray = new Mat();
        // Video device acces
        VideoCapture videoDevice = new VideoCapture();
        // 0:Start default video device 1,2 etc video device id
        videoDevice.open(0);
        // is contected

        JFrame frame = new JFrame();
        frame.setTitle("image");
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        ProcessImage processImage = new ProcessImage();


        int count = 0;
        if (videoDevice.isOpened()) {
            boolean cont = true;
            while (cont) {
                if (videoDevice.read(imageArray)) {
                    if (count++ % 100 == 0) {
                        System.out.println(".");
                    } else {
                        System.out.print(".");
                    }
                    processImage.process(imageArray);
                    processImage.drawRectoangles(imageArray,true);
                    BufferedImage image = Mat2BufferedImage(imageArray);
                    saveImage(image);
                    frame.setSize(image.getWidth(),
                            image.getHeight());

                    JLabel label = new JLabel();
                    label.setIcon(new ImageIcon(image));
                    frame.add(label);

                }
                else
                {
                    System.out.println("FUCK");
                }

            }
        } else {
            System.out.println("Error.");
        }
    }

    public static BufferedImage Mat2BufferedImage(Mat m) {
        //source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
        //Fastest code
        //The output can be assigned either to a BufferedImage or to an Image

        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0,
                0,
                b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),
                m.rows(),
                type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b,
                0,
                targetPixels,
                0,
                b.length);
        return image;
    }

    //Save an image
    public static void saveImage(BufferedImage img) {
        System.out.print("x");
        if(img!=null) {
            try {
                File outputfile = new File("new.png");
                ImageIO.write(img, "png", outputfile);
                System.out.print("|");
            } catch (Exception e) {
                System.out.println("error trying to save");
            }
        }
    }
}
