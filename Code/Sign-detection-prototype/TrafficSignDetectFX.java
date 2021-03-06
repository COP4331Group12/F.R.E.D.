package NeuralNetwork;


import java.io.ByteArrayInputStream;



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

import javafx.scene.image.*;

public class TrafficSignDetectFX
{

	private static final long serialVersionUID = 1L;

	private DaemonThread myThread = null;
	private VideoCapture webSource = null;
	private Mat frame;
	private MatOfByte mem;
	private CascadeClassifier signDetect;
	private Image image;
	private ImageView display;
	private MatOfRect signDetections;
	boolean signFound = false;
	




	public void setDisplay(ImageView display) {
		this.display = display;
	}

	public TrafficSignDetectFX() {
		webSource = new VideoCapture(0);
		myThread = new DaemonThread(); // create object of thread class
		Thread t = new Thread(myThread);
		t.setDaemon(true);
		myThread.runnable = true;
		frame = new Mat();
		mem = new MatOfByte();
		signDetect = new CascadeClassifier("NeuralNetwork/stopsign_classifier.xml");
		signDetections = new MatOfRect();
		t.start(); // start thread
	}

	class DaemonThread implements Runnable
    {
		protected volatile boolean runnable = false;

		public void run() {
			synchronized (this) {

				while (runnable) {
					if (webSource.grab()) {
						try {
							webSource.retrieve(frame);
							signDetect.detectMultiScale(frame, signDetections, 1.1, 3, 0, new Size(20, 20), new Size());

							if (signDetections.toArray().length == 0) {// If no signs in view
								signFound = false;
							}

							if (signDetections.toArray().length > 0) {// If sign was found in view
								if (!signFound) {// If sign found after not previous being seen
									////// Signal car to stop///////////////////////////
									System.out.println("STOP IT");
									signFound = true;
								}
								for (Rect rect : signDetections.toArray()) {
									Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0));
								}
							}
							
							Imgcodecs.imencode(".bmp", frame, mem);
							image = new Image(new ByteArrayInputStream(mem.toArray()));
							display.setImage(image);
							
						} catch (Exception ex) {
							System.out.println("Error");
						}
					}
				}
			}
		}

	}

}