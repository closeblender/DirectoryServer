package com.ndsucsci.clientservermessages;

/**
 * Created by Trevor on 10/23/15.
 */
public class DownloadFileResponse extends DataMessage {
    public DownloadFileResponse() {
        super();
    }

    public boolean foundFile() {
        return getDataBytes().length > 0;
    }

    public byte[] getFile() {
        return getDataBytes();
    }

    public static byte[] createMessage(byte[] data) {
        return DataMessage.createMessage(data);
    }
}
