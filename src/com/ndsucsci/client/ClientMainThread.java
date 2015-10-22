package com.ndsucsci.client;

import java.net.ServerSocket;

/**
 * Created by Trevor on 10/21/15.
 */
import java.net.Socket;

public class ClientMainThread extends Thread {

    private int portNo;
    ServerSocket clientSocket = null;
    boolean keepRunning = true;

    public ClientMainThread(int portNo) {
        this.portNo = portNo;
    }

    public void run() {
        try {
            clientSocket = new ServerSocket(portNo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while(keepRunning) {
            try {
                //listen for incoming connections
                Socket acceptedSocket = clientSocket.accept();
                
                //send file requested

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
