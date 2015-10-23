package com.ndsucsci.objects;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by closestudios on 10/10/15.
 */
public class User  implements Serializable{
    public String uuid;
    public String ipAddress;
    long lastPing;
    public User() {

    }
    public User(String uuid, String ipAddress) {
        this.uuid = uuid;
        pingUser(ipAddress);
    }
    public void pingUser(String ipAddress) {
        this.ipAddress = ipAddress;
        lastPing = new Date().getTime();
    }
    public boolean isOnline() {
        return (new Date().getTime()) - lastPing < (200*1000);
    }
}
