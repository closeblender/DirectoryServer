package com.ndsucsci;

import com.ndsucsci.client.ClientRegisterThread;
import com.ndsucsci.objects.SearchResult;
import com.ndsucsci.objects.UpdateFile;
import com.ndsucsci.objects.User;
import com.ndsucsci.server.Directory;
import com.ndsucsci.server.ServerMainThread;
import com.ndsucsci.server.ServerPingThread;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DirectoryServer {

    static ArrayList<User> users;
    static Directory directory;
    static boolean userLock = false;
    static boolean directoryLock = false;

    public static void main(String[] args) {


        try {
            // Init Users
            loadUsers();

            // Init Directory
            loadDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


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

    public static boolean updateFiles(ArrayList<UpdateFile> files, String computerUUID) throws IOException, InterruptedException {
        // Check that it is a valid computer UUID
        if(!repeatUUID(computerUUID)) {
            return false;
        }

        // Update Files!
        directory.updateFiles(files, computerUUID);

        saveDirectory();

        return true;
    }

    public static ArrayList<SearchResult> searchFiles(String query) {
        return directory.searchFiles(query, users);
    }

    public static String registerComputer(String ipAddress) throws IOException, InterruptedException {
        // Create UUID for new computer
        String uuid = UUID.randomUUID().toString();
        while(repeatUUID(uuid)) {
            uuid = UUID.randomUUID().toString();
        }

        // Create User
        users.add(new User(uuid, ipAddress));
        saveUsers();

        return uuid;
    }

    public static boolean pingUser(String uuid, String ipAddress) throws IOException, InterruptedException {
        for(int i=0;i<users.size();i++) {
            if(users.get(i).uuid.equals(uuid)) {
                users.get(i).pingUser(ipAddress);
                saveUsers();
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

    public static void loadUsers() throws IOException, ClassNotFoundException {
        File file = new File("users.bytes");
        if(file.exists()) {
            System.out.println("Loaded Users!");
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            ArrayList<User> c = (ArrayList<User>) s.readObject();
            s.close();
            users = c;
        } else {
            System.out.println("New Users!");
            users = new ArrayList<>();
        }
    }

    public static void loadDirectory() throws IOException, ClassNotFoundException {
        File file = new File("directory.bytes");
        if(file.exists()) {
            System.out.println("Loaded Directory!");
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            Directory c = (Directory) s.readObject();
            s.close();
            directory = c;
        } else {
            System.out.println("New Directory!");
            directory = new Directory();
        }
    }




    public static boolean saveUsers() throws IOException, InterruptedException {
        while(userLock) {
            Thread.sleep(10);
        }
        userLock = true;
        File file = new File("users.bytes");
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(users);
        s.close();
        userLock = false;
        return true;
    }

    public static boolean saveDirectory() throws IOException, InterruptedException {
        while(directoryLock) {
            Thread.sleep(10);
        }
        directoryLock = true;
        File file = new File("directory.bytes");
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(directory);
        s.close();
        directoryLock = false;
        return true;
    }

}
