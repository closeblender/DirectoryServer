package com.ndsucsci.client;

import com.ndsucsci.clientservermessages.DownloadFileRequest;
import com.ndsucsci.clientservermessages.DownloadFileResponse;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Trevor on 10/23/15.
 */
public class ClientDownloadFileThread extends Thread {

    String host = "";
    int port = 0;
    String filename = "";

    public ClientDownloadFileThread(String host, int port, String filename) {
        this.host = host;
        this.port = port;
        this.filename = filename;
    }

    public void run() {
        Socket downloadSocket = null;
        //connect to other client
        try {
            downloadSocket = new Socket("127.0.0.1", 9092);

            //send filename
            OutputStream os = downloadSocket.getOutputStream();
            DownloadFileRequest request = new DownloadFileRequest();
            request.createMessage(filename);

            //get file response
            DownloadFileResponse response = new DownloadFileResponse();
            InputStream is = downloadSocket.getInputStream();
            response.getBytesFromInput(is);
            OutputStream fileOutput = new FileOutputStream("share/" + filename);
            fileOutput.write(response.getDataBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
