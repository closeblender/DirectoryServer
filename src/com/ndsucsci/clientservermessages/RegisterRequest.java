package com.ndsucsci.clientservermessages;

/**
 * Created by closestudios on 10/10/15.
 */
public class RegisterRequest {

    ServerRequest serverRequest;

    public RegisterRequest(ServerRequest request) {
        serverRequest = request;
    }

    public static byte[] createMessage() {
        return DataMessage.createMessage("Register".getBytes());
    }

}
