package com.ndsucsci;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by closestudios on 10/9/15.
 */
public class ServerProcessRequestThread extends Thread {

    Socket clientSocket = null;

    public ServerProcessRequestThread(Socket socket) {
        super("ServerProcessRequestThread");
        clientSocket = socket;
    }


    public void run() {

        // Print out Client
        System.out.println("Request From Client: " + clientSocket.getInetAddress().getHostAddress());

        try {
            OutputStream outToClient = clientSocket.getOutputStream();
            InputStream inFromClient = clientSocket.getInputStream();

            // Get request
            ServerRequest serverRequest = new ServerRequest();

            byte[] data = new byte[1024];
            while(!serverRequest.receivedRequest()) {
                int length = inFromClient.read(data, 0, data.length);
                serverRequest.receivedBytes(data, length);
            }

            // Process Request
            byte[] response = new byte[0];
            switch (serverRequest.getRequestType()) {
                case Register:
                    RegisterRequest registerRequest = new RegisterRequest(serverRequest);

                    // Registers Computer
                    String newUUID = DirectoryServer.registerComputer(clientSocket.getInetAddress().getHostAddress());

                    // Respond with new UUID;
                    response = RegisterResponse.createMessage(newUUID);

                    break;
                case Search:

                    // Create request
                    SearchRequest searchRequest = new SearchRequest(serverRequest);

                    // Create response
                    response = SearchResponse.createMessage(DirectoryServer.searchFiles(searchRequest.getQuery()));

                    break;
                case UpdateList:
                    UpdateFilesRequest updateFilesRequest = new UpdateFilesRequest(serverRequest);

                    // Update Directory with files and create response message
                    response = UpdateFilesResponse.createMessage(DirectoryServer.updateFiles(updateFilesRequest.getUpdateFiles(), updateFilesRequest.getComputerUUID()));

                    break;
            }

            // Send response
            outToClient.write(response);
            outToClient.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
