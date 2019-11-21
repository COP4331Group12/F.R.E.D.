package UI.manualUI;

import arduinoControl.Car;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import logs.Logger;

public class manualScreenController
{

    @FXML
    AnchorPane anchorPane;

    @FXML
    Button upButton, backButton;

    @FXML
    JFXSlider turnSlider;




    public void initialize()
    {
        Car.getInstance().setAutonomousMode(false);
        Platform.runLater(this::createListener);
    }


    //Whenever we drag the steering slider to a non 0 position we
    //want it to snap back to 0 after the user releases the mouse.
    //This method is bound to a onMouseReleased listener in manualScreen.fxml
    void resetSlider()
    {
        if(turnSlider.getValue() != 0.0)
        {
            turnSlider.setValue(0.0);
        }
    }


    //When ever a change is detected by the steering slider we
    //call this method and use the UI data to create a server command
    //and send the turn command.
    public void turn()
    {
        int raw = (int)turnSlider.getValue();
        String direction = raw > 0 ? "1" : "0";
        String angle = String.valueOf(Math.abs(raw));

        Car.getInstance().turnCar(direction,angle);
    }


    //This is called when the forward button is pressed down, as well as when the
    //'W' key is pressed.
    public void goForward()
    {
        //Here we need to check the state of the car to prevent redundant commands
        if(Car.getInstance().getCarState().compareTo(Car.forwardCode) != 0)
        {
            Car.getInstance().moveCarForward();
        }

    }

    //This is called when the backward button is pressed down, as well as when the
    //'S' key is pressed.
    public void goBackward()
    {
        //Here we need to check the state of the car to prevent redundant commands
        if(Car.getInstance().getCarState().compareTo(Car.backwardCode) != 0)
        {
            Car.getInstance().moveCarBackward();
        }

    }

    //This is called whenever we release the forward or backward button or key
    //Whenever we need to stop we can call this method
    public void setNeutral()
    {
        if(Car.getInstance().getCarState().compareTo(Car.neutralCode) != 0)
        {
            Car.getInstance().setCarNeutral();
        }

    }

    public void shutDown()
    {
        Car.getInstance().shutDownCar();
    }



    //These three methods are specifically bound to the keyboard
    //To turn constant amounts every time we press the corresponding key
    public void turnRight()
    {
        Car.getInstance().turnCar("1","90");
    }

    public void turnLeft()
    {
        Car.getInstance().turnCar("0","90");
    }

    public void resetTurn()
    {
        Car.getInstance().turnCar("0","0");
    }


    //Initialize key listeners and bind keys to proper control methods.
    //Since we want to only control the car while a button are key is being held down
    //we send different commands depending on if we are pressing a key down or releasing
    //the key
    private void createListener()
    {
        Scene scene = anchorPane.getScene();

        if(scene == null)
        {
            Logger.getInstance().logErrorMessage("Failed to bind keys: null scene");
            return;
        }

        //Key pressed listener
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W: goForward(); break; //Forward
                case A: turnLeft(); break;   //Left
                case S: goBackward(); break;   //Backward
                case D: turnRight(); break;   //Right
                case R: resetTurn(); break;   //Center steering
                case SPACE: shutDown(); break;
                case E:  break;   //Right-Forward
                case Q:  break;   //Left-Forward
                case Z:  break;   //Left-Backward
                case C:  break;   //Right-Backward
            }
        });


        //Key released listener
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W: setNeutral(); break; //Forward
                case A: break;   //Left
                case S: setNeutral(); break;   //Backward
                case D: break;   //Right
                case E: setNeutral(); break;   //Right-Forward
                case Q: setNeutral(); break;   //Left-Forward
                case Z: setNeutral(); break;   //Left-Backward
                case C: setNeutral(); break;   //Right-Backward
            }
        });
    }
}
