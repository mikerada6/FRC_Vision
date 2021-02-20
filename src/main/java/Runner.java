import image.ProcessImage;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public
class Runner {

    public static
    void main(String[] args) {
        System.out.println("Hello world");
        OpenCV.loadLocally();
        String path = "src/main/resources/1.png";
        ProcessImage tempImg = new ProcessImage();
        Mat img = imread(path);
        long start = System.currentTimeMillis();
        tempImg.process(img);
        ArrayList<MatOfPoint> hulls = tempImg.getConvexHullsOutput();

        if (hulls.size() > 0) {

            Double minX = Double.MAX_VALUE;
            Double maxX = Double.MIN_VALUE;
            Double minY = Double.MAX_VALUE;
            Double maxY = Double.MIN_VALUE;

            for(Point p : hulls.get(0).toArray())
            {
                if(p.x < minX)
                    minX = p.x;
                if(p.x > maxX)
                    maxX = p.x;
                if(p.y < minY)
                    minY = p.y;
                if(p.y > maxY)
                    maxY = p.y;
            }

            double centerX = (maxX + minX) / 2;
            double centerY = (maxY + minY) / 2;


            Imgproc.rectangle(img,
//Matrix obj of the image
                              new Point(minX,
                                        minY),
//p1
                              new Point(maxX,
                                        maxY),
//p2
                              new Scalar(0,
                                         0,
                                         255),
//Scalar object for color
                              5
//Thickness of the line
            );

            Imgproc.circle(img,
                           //Matrix obj of the image
                           new Point(centerX,
                                     centerY),
                           //Center of the circle
                           8,
                           //Radius
                           new Scalar(0,
                                      255,
                                      0),
                           //Scalar object for color
                           4
                           //Thickness of the circle
            );
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start));
            BufferedImage temp = null;
            temp = Mat2BufferedImage(img);

            JFrame frame = new JFrame();
            frame.setTitle("image");
            frame.setSize(temp.getWidth(),
                          temp.getHeight());
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(temp));
            frame
                    .getContentPane()
                    .add(label,
                         BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);


            System.out.println("EXIT");
        }
    }

    public static
    BufferedImage Mat2BufferedImage(Mat m) {
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
}
