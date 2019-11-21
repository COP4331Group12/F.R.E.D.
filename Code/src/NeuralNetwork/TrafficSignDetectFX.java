package NeuralNetwork;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import arduinoControl.Car;
import javafx.scene.image.*;

/*
 * This class starts the camera and processes the frames seen by
 * capturing a frame, processing it with the classifier, and
 * creating a matrix of Rects and based on the size of the
 * array we can tell if a sign was found. Then the image is then
 * updated to be displayed.
 */

public class TrafficSignDetectFX {

    private static final long serialVersionUID = 1L;

    private DaemonThread myThread = null;
    private VideoCapture webSource = null;
    private Mat frame;
    private MatOfByte mem;
    private CascadeClassifier signDetect;
    private Image image;
    private ImageView display;
    private MatOfRect signDetections;
    public static volatile boolean signFound = false;

    public ImageView getDisplay() {
        return display;
    }

    public void setDisplay(ImageView display) {
        this.display = display;
    }


    public TrafficSignDetectFX() {
        webSource = new VideoCapture(
                "http://192.168.1.100:32805/video"); /*
         * Sets where to get footage from "0":front facing cam "1":secondary
         * "http://.../video": IP stream
         */
        myThread = new DaemonThread();
        Thread t = new Thread(myThread);
        t.setDaemon(true);
        myThread.runnable = true;
        frame = new Mat();
        mem = new MatOfByte();
        signDetect = new CascadeClassifier("C:/opencv/build/etc/haarcascades/stopsign_classifier.xml");// Loads
        // classifier
        signDetections = new MatOfRect();
        t.start(); // start thread
    }

    class DaemonThread implements Runnable {


        private void sendStopWithDelay(int time)
        {


            Car.getInstance().setCarNeutral();

            new Thread(() ->
            {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            Car.getInstance().moveCarForward();


        }

        protected volatile boolean runnable = false;

        public void run() {

            synchronized (this) {

                while (runnable) {
                    if (webSource.grab()) {
                        try {
                            webSource.retrieve(frame);
                            signDetect.detectMultiScale(frame, signDetections, 1.05, 4, 0, new Size(30, 30),
                                    new Size());

                            if (signDetections.toArray().length == 0) {// If no signs in view
                                signFound = false;
                            }

                            if (signDetections.toArray().length > 0) {// If sign was found in view
                                if (!signFound) {// If first time frame sign seen

                                    signFound = true;

                                    if (Car.getInstance().getAutonomousMode()) {

                                        sendStopWithDelay(3000);

                                    }

                                }
                                // Draws borders around the sign/signs
                                for (Rect rect : signDetections.toArray()) {
                                    Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0));
                                }

                            }
                            // Processes the image to be displayed through ImageView
                            Imgcodecs.imencode(".bmp", frame, mem);
                            image = new Image(new ByteArrayInputStream(mem.toArray()));
                            display.setImage(image);


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}