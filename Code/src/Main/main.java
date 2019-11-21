package Main;


import Network.networkUtility;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logs.Logger;
import org.opencv.core.Core;

import java.io.IOException;

//import org.opencv.core.Core;

public class main extends Application
{

    private void closeDownOperations()
    {

       // Car.getInstance().stopDistanceTracking();
        //Car.getInstance().shutDownCar();
        networkUtility.getInstance().closeServerConnection();
        Logger.getInstance().closeMasterLogFile();
    }

    private void startUpOperations()
    {
        networkUtility.getInstance().connectToServer();
        //Car.getInstance().startDistanceTracking();
    }

    @Override
    public void start(Stage primaryStage) throws IOException
    {
        primaryStage.setTitle("F.R.E.D");

        startUpOperations();

        Parent root = FXMLLoader.load(getClass().getResource("/UI/mainScreen.fxml"));
        Scene scene = new Scene(root);


        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            closeDownOperations();
            primaryStage.close();
        });
    }

    public static void main(String[] args)
    {
    	//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
