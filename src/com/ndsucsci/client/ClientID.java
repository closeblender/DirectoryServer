package com.ndsucsci.client;

import java.io.Serializable;

/**
 * Created by Trevor on 10/25/15.
 */
public class ClientID implements Serializable {

    public String uuid;

    public ClientID(String uuid) {
        this.uuid = uuid;
    }

}
