package com.ndsucsci;

import com.ndsucsci.client.ClientRegisterThread;
import com.ndsucsci.objects.SearchResult;
import com.ndsucsci.objects.UpdateFile;
import com.ndsucsci.objects.User;
import com.ndsucsci.server.Directory;
import com.ndsucsci.server.ServerMainThread;
import com.ndsucsci.server.ServerPingThread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

public class DirectoryServer {

    static ArrayList<User> users;
    static Directory directory;

    public static void main(String[] args) {


        // Init Users
        users = new ArrayList<>();

        // Init Directory
        directory = new Directory();

        try {
            System.out.println("Starting Server On IP: " + InetAddress.getLocalHost().getHostAddress().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Create Search and Registration (Main Connection Thread)
        ServerMainThread serverMainThread = new ServerMainThread(9090);
        serverMainThread.start();

        // Create Ping (Hello Thread)
        ServerPingThread serverPingThread = new ServerPingThread(9091);
        serverPingThread.start();


    }

    public static boolean updateFiles(ArrayList<UpdateFile> files, String computerUUID) {
        // Check that it is a valid computer UUID
        if(!repeatUUID(computerUUID)) {
            return false;
        }

        // Update Files!
        for(int i=0;i<files.size(); i++) {
            directory.updateFile(files.get(i), computerUUID);
        }

        return true;
    }

    public static ArrayList<SearchResult> searchFiles(String query) {
        return directory.searchFiles(query, users);
    }

    public static String registerComputer(String ipAddress) {
        // Create UUID for new computer
        String uuid = UUID.randomUUID().toString();
        while(repeatUUID(uuid)) {
            uuid = UUID.randomUUID().toString();
        }

        // Create User
        users.add(new User(uuid, ipAddress));

        return uuid;
    }

    public static boolean pingUser(String uuid, String ipAddress) {
        for(int i=0;i<users.size();i++) {
            if(users.get(i).uuid.equals(uuid)) {
                users.get(i).pingUser(ipAddress);
                return true;
            }
        }
        return false;
    }

    public static boolean repeatUUID(String uuid) {
        for(int i=0;i<users.size();i++) {
            if(users.get(i).uuid.equals(uuid)) {
                return true;
            }
        }
        return false;
    }

}
