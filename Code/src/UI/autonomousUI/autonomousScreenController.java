package UI.autonomousUI;

import NeuralNetwork.TrafficSignDetectFX;
import arduinoControl.Car;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;

public class autonomousScreenController
{

    @FXML
    JFXToggleButton toggleButton;

    private static volatile boolean hasHazardDetection = false;

    public void initialize()
    {

    }

    private void beginHazardDetection()
    {

        new Thread(() -> {

            while(hasHazardDetection)
            {
               if(Car.distanceToSign < 150)
               {
                   Car.getInstance().setCarNeutral();
               }
               else
               {
                   Car.getInstance().moveCarForward();
               }
            }


        }).start();
    }

    private void stopHazardDetection()
    {
        hasHazardDetection = false;
    }

    public void toggleAutonomousMode()
    {

        if(toggleButton.isSelected())
        {
            beginHazardDetection();
        	Car.getInstance().setAutonomousMode(true);
            Car.getInstance().moveCarForward();
        }
        else
        {
            stopHazardDetection();
        	Car.getInstance().setAutonomousMode(false);
            Car.getInstance().setCarNeutral();
        }

    }
}
