package com.ndsucsci.clientservermessages;

/**
 * Created by Trevor on 10/23/15.
 */
public class DownloadFileResponse extends DataMessage {
    public DownloadFileResponse() {
        super();
    }

    public String getFile() {
        return new String(getDataBytes());
    }

    public static byte[] createMessage(String file) {
        return DataMessage.createMessage(file.getBytes());
    }
}
