package UI;


import NeuralNetwork.TrafficSignDetectFX;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import logs.Logger;

import java.util.ArrayList;


public class mainScreenController
{
    @FXML
    TextArea Console;

    @FXML
    ImageView imageView;

    public void initialize()
    {
        initializeConsole();
        initializeAutoScroll();
        //startClassifier();
    }


    //This method will instantiate a classifier object that
    //begins a new thread in its constructor. Additionally,
    //we pass in the imageView so it can automatically update
    //the camera feed on the user interface
    private void startClassifier()
    {
        TrafficSignDetectFX classifier = new TrafficSignDetectFX();
        //classifier.setDisplay(imageView);

    }


    //When we create a new screen we want to bind the internal logger to the
    //text area on the main ui and then display successful binding
    private void initializeConsole()
    {

        Console.textProperty().bindBidirectional(Logger.getInstance().getObservableMasterLog());
        Logger.getInstance().logMessage("Setting up logger...");
        Logger.getInstance().logMessage("Logger initialized");

    }


    //This will listen for changes in the text area and make sure the text area
    //will automatically scroll to the most current log message
    private void initializeAutoScroll()
    {
        Logger.getInstance().getObservableMasterLog().addListener((observable, oldValue, newValue) -> {
            try
            {
                Console.selectPositionCaret(Console.getLength());
                Console.deselect();
            }catch (IndexOutOfBoundsException e) {}

        });

    }



}
