package com.ndsucsci.client;

/**
 * Created by Trevor on 10/22/15.
 */
import com.ndsucsci.clientservermessages.DownloadFileRequest;
import com.ndsucsci.clientservermessages.DownloadFileResponse;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.File;

public class ClientSendFileThread extends Thread {
    Socket peerSocket = null;
    public ClientSendFileThread(Socket peerSocket) {
        this.peerSocket = peerSocket;
    }

    public void run() {
        try {
            InputStream is = peerSocket.getInputStream();
            OutputStream os = peerSocket.getOutputStream();

            //read for filename
            DownloadFileRequest dfr = new DownloadFileRequest();
            dfr.getBytesFromInput(is);

            File sendFile = new File("share/" + dfr.getFileName());
            if(sendFile.exists()) {
                InputStream fileStream = new FileInputStream(sendFile);
                DownloadFileResponse response = new DownloadFileResponse();
                response.getBytesFromInput(fileStream);
                os.write(response.getDataBytes(), 0, response.getDataBytes().length);
                System.out.println("File sent.");
            }

            peerSocket.close();

        } catch (Exception e) {

        }

    }
}
