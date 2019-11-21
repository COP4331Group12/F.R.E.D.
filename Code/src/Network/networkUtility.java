package Network;


import logs.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;


//The network interface uses a TCP connection with the arduino
//in order to send commands. Since we are using TCP we have create
//an input and output stream for receiving and sending commands
public class networkUtility
{
    private static final int port = 80;
    private static final String host = "192.168.1.100"; //router
    //private static final String host = "172.20.10.12"; //arduino 1
    //private static final String host = "172.20.10.8"; //arduino 2
    static Socket socket;
    static PrintWriter out;
    static BufferedReader in;

    private static networkUtility networkUtility;

    private networkUtility() {}

    public static networkUtility getInstance()
    {

        if(networkUtility == null)
        {
            networkUtility = new networkUtility();
        }

        return networkUtility;
    }

    public boolean isConnectedToServer()
    {
        return socket != null && socket.isConnected();
    }

    //We attempt to connect to the server for 5 seconds before
    //timing out and continuing
    public void connectToServer()
    {
        if(socket != null)
        {
            Logger.getInstance().logErrorMessage("Already connected to server");
            return;
        }

        socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(host, port), 5000);
        } catch (IOException e)
        {
            Logger.getInstance().logErrorMessage("Failed to connect to host");
            socket = null;
            return;
        }

        Logger.getInstance().logMessage("Connected to server\n");

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {

            Logger.getInstance().logErrorMessage("Failed to bind input and output streams");
        }

    }


    public String sendCommand(String message)
    {

        String response = "We did not get a request";

        if(socket == null)
        {
            return "Cannot send command until we connect to server";
        }

        out.println(message);

        try {

            response = in.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }


    public void closeServerConnection()
    {
        if(socket == null)
        {
            Logger.getInstance().logMessage("Socket is null: bypassing server connection termination");
            return;
        }

        out.close();
        out = null;

        Logger.getInstance().logMessage("\nClosed output stream");

        try {

            in.close();
            in = null;

        } catch (IOException e) {

            e.printStackTrace();
        }

        Logger.getInstance().logMessage("Closed input stream");


        try {
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.getInstance().logMessage("Closed socket");
    }

}
