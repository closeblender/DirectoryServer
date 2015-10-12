package com.ndsucsci;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by closestudios on 10/10/15.
 */
public class ClientRegisterThread extends Thread {

    Socket socket = null;
    String host;
    int port;
    RegisterCallback callback;

    public ClientRegisterThread(String host, int port, RegisterCallback callback) {
        super("ClientRegisterThread");
        this.host = host;
        this.port = port;
        this.callback = callback;
    }

    public interface RegisterCallback {
        void onRegistered(String computerUUID);
    }

    public void run() {

        // Print out what you are doing
        System.out.println("Register Client");

        try {

            socket = new Socket(host, port);

            OutputStream outToServer = socket.getOutputStream();
            InputStream inFromServer = socket.getInputStream();

            // Send Register Message
            outToServer.write(RegisterRequest.createMessage());
            outToServer.flush();

            // Get response
            RegisterResponse registerResponse = new RegisterResponse();
            byte[] data = new byte[1024];
            while(!registerResponse.receivedRequest()) {
                int length = inFromServer.read(data, 0, data.length);
                registerResponse.receivedBytes(data, length);
            }

            callback.onRegistered(registerResponse.getComputerUUID());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
