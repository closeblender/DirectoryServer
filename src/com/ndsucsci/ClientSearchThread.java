package com.ndsucsci;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by closestudios on 10/10/15.
 */
public class ClientSearchThread extends Thread {

    Socket socket = null;
    String host;
    int port;
    String query;
    SearchCallback callback;

    public ClientSearchThread(String host, int port, String query, SearchCallback callback) {
        super("ClientSearchThread");
        this.host = host;
        this.port = port;
        this.query = query;
        this.callback = callback;
    }

    public interface SearchCallback {
        void searchResults(ArrayList<SearchResult> searchResults);
    }

    public void run() {

        // Print out what you are doing
        System.out.println("Search File");

        try {

            socket = new Socket(host, port);

            OutputStream outToServer = socket.getOutputStream();
            InputStream inFromServer = socket.getInputStream();

            // Send Register Message
            outToServer.write(SearchRequest.createMessage(query));
            outToServer.flush();

            // Get response
            SearchResponse searchResponse = new SearchResponse();
            byte[] data = new byte[1024];
            while(!searchResponse.receivedRequest()) {
                int length = inFromServer.read(data, 0, data.length);
                searchResponse.receivedBytes(data, length);
            }

            callback.searchResults(searchResponse.getSearchResults());

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
