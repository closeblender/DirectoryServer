package com.ndsucsci.server;

import com.ndsucsci.DirectoryServer;
import com.ndsucsci.clientservermessages.*;
import com.ndsucsci.objects.SearchResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

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
            serverRequest.getBytesFromInput(inFromClient);

            // Process Request
            byte[] response = new byte[0];
            switch (serverRequest.getRequestType()) {
                case Register:
                    RegisterRequest registerRequest = new RegisterRequest(serverRequest);

                    // Registers Computer
                    String newUUID = DirectoryServer.registerComputer(clientSocket.getInetAddress().getHostAddress());

                    // Respond with new UUID;
                    response = RegisterResponse.createMessage(newUUID);

                    System.out.println("Registered UUID: " + newUUID);

                    break;
                case Search:

                    // Create request
                    SearchRequest searchRequest = new SearchRequest(serverRequest);

                    // Create response
                    ArrayList<SearchResult> results = DirectoryServer.searchFiles(searchRequest.getQuery());
                    response = SearchResponse.createMessage(results);

                    System.out.println("Searched For \"" + searchRequest.getQuery() + "\" Received Results: " + results.size());

                    break;
                case UpdateList:
                    UpdateFilesRequest updateFilesRequest = new UpdateFilesRequest(serverRequest);

                    // Update Directory with files and create response message
                    response = UpdateFilesResponse.createMessage(DirectoryServer.updateFiles(updateFilesRequest.getUpdateFiles(), updateFilesRequest.getComputerUUID()));

                    System.out.println("Updated Files " + updateFilesRequest.getUpdateFiles().size() + " For UUID: " + updateFilesRequest.getComputerUUID());

                    break;
            }

            // Send response
            outToClient.write(response);
            outToClient.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
