package com.ndsucsci;

/**
 * Created by closestudios on 10/9/15.
 */
public class ServerRequest extends DataMessage {

    public enum ServerRequestType {
        Register(0), Search(1), UpdateList(2);

        private final int value;
        private ServerRequestType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public ServerRequest() {
        super();
    }

    public ServerRequestType getRequestType() {
        if(!receivedRequest()) {
            return null;
        }

        if(getData().size() == 0) {
            return null;
        }

        return ServerRequestType.values()[getData().get(0)];
    }

}
