package com.ndsucsci.client;


import com.ndsucsci.objects.SearchResult;
import com.ndsucsci.objects.UpdateFile;
import com.ndsucsci.server.Directory;

import javax.swing.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private static String uuid = null;
    private static File shareFolder = new File("share");
    public static ClientFrame frame = new ClientFrame();

    public static void main(String[] args) {
        frame.pack();
        frame.setVisible(true);
    }

    public static void connect(){
        try {
            checkForID();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hasConnected() {
        frame.logln("Register Computer UUID: " + uuid);
        uuid = uuid;

        //create share folder
        //get files from sharefolder
        if(!shareFolder.exists()) {
            try {
                shareFolder.mkdir();
                frame.logln("Created share folder");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //testing share folder
        if(shareFolder.exists()) {
            try {
                frame.logln("share folder exists");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //ping server and allow user to enter commands
        pingComputer(uuid);
        new ClientMainThread(9092).start();
        clientUpdateFiles();
    }

    static void clientSearch(String fileName) {
        new ClientSearchThread(getHost(), 9090, fileName, new ClientSearchThread.SearchCallback() {
            @Override
            public void searchResults(ArrayList<SearchResult> searchResults) {
                frame.tvFilesDirectory.setText("Search Results: ");
                //add search results to file and peer list
                DefaultListModel filesJlist = new DefaultListModel();
                for (SearchResult sr : searchResults) {
                    filesJlist.addElement(sr.filename);
                }
                frame.filesList.setModel(filesJlist);
                frame.filesList.addListSelectionListener(new SearchFileAdapter(frame.filesList, frame.peersList, searchResults));
                frame.logln("Total Search Results: " + searchResults.size());
                frame.logln(searchResults.toString());
            }
        }).start();
    }

    public static void clientUpdateFiles() {
        frame.tvFilesDirectory.setText("Update Files: ");
        DefaultListModel filesJlist = new DefaultListModel();
        ArrayList<UpdateFile> files = new ArrayList<>();
        frame.logln("Make sure all files being added are located in your share folder.");
        //get and add files from share folder
        if(shareFolder.exists()) {
            if(shareFolder.listFiles().length > 0) {
                for(File file : shareFolder.listFiles()) {
                    files.add(new UpdateFile(file.getName(), Long.toString(file.length()), true));
                    filesJlist.addElement(file.getName() + "  |  " + readableFileSize(file.length()));
                }
                //update immediately with local files
                frame.filesList.setModel(filesJlist);
            } else {
                frame.logln("Share folder is empty. You have nothing to add.");
            }

        } else {
            frame.logln("Share folder doesn't exist.");
        }

        if(files.size() > 0) {
            new ClientUpdateFileThread(getHost(), 9090, files, uuid, new ClientUpdateFileThread.UpdateFilesCallback() {
                public void onUpdate(boolean updated) {
                    frame.logln("Updated Files: " + updated);
                }
            }).start();
        }
    }

    //filesize helper function: http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + units[digitGroups];
    }

    static void clientDownloadFile(String address, String fileName) {
        new ClientDownloadFileThread(address, 9092, fileName).start();
    }

    private static void pingComputer(String uuid) {
        new ClientPingThread(getHost(), 9091, uuid).start();
    }

    public static void checkForID() throws IOException, ClassNotFoundException {
        File file = new File("uuid.bytes");
        if(file.exists()) {
            System.out.println("Loaded uuid!");
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            ClientID c = (ClientID) s.readObject();
            s.close();
            uuid = c.uuid;

            hasConnected();

        } else {
            System.out.println("New uuid!");

            int portNo = 9090;

            //register client
            new ClientRegisterThread(getHost(), portNo, new ClientRegisterThread.RegisterCallback() {
                @Override
                public void onRegistered(String computerUUID) {
                    //need to cache uuid
                    try {
                        ClientID clientID = new ClientID(computerUUID);
                        File file = new File("uuid.bytes");
                        FileOutputStream f = new FileOutputStream(file);
                        ObjectOutputStream s = new ObjectOutputStream(f);
                        s.writeObject(clientID);
                        s.close();
                        System.out.println("Save uuid.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    uuid = computerUUID;
                    hasConnected();
                }
            }).start();
        }
    }

    public static String getHost() {
        return frame.hostTextField.getText();
    }

}

