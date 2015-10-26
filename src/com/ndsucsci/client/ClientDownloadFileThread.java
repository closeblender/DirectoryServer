package com.ndsucsci.client;

import com.ndsucsci.clientservermessages.DownloadFileRequest;
import com.ndsucsci.clientservermessages.DownloadFileResponse;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.File;

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
            downloadSocket = new Socket(host, port);

            Client.frame.logln("Downloading " + filename);

            //send filename
            OutputStream os = downloadSocket.getOutputStream();
            os.write(DownloadFileRequest.createMessage(filename));

            //get file response
            DownloadFileResponse response = new DownloadFileResponse();
            InputStream is = downloadSocket.getInputStream();
            response.getBytesFromInput(is);

            if(response.foundFile()) {
                Client.frame.logln("Peer Sent File: " + filename);


                OutputStream fileOutput = new FileOutputStream("share/" + filename);
                Client.frame.logln("first");
                byte[] fileBytes = response.getFile();
                Client.frame.logln("second");
                fileOutput.write(fileBytes);
                Client.frame.logln("ljdfs: " + filename);


                //check if file exists
                if((new File("share/" + filename)).exists()) {
                    Client.frame.logln("Received File");
                    Client.clientUpdateFiles();
                }

            } else {
                Client.frame.logln("Peer Did Not Send File: " + filename);

            }


            downloadSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
