package com.ndsucsci.client;


import com.ndsucsci.objects.SearchResult;
import com.ndsucsci.objects.UpdateFile;

import javax.swing.*;
import java.io.File;
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

        String hostName = frame.hostTextField.getText();
        int portNo = Integer.parseInt(frame.portTextField.getText());

        //register client
        new ClientRegisterThread(hostName, portNo, new ClientRegisterThread.RegisterCallback() {
            @Override
            public void onRegistered(String computerUUID) {
                //need to cache uuid

                frame.logln("Register Computer UUID: " + computerUUID);
                uuid = computerUUID;

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
                pingComputer(computerUUID);
                new ClientMainThread(9092).start();
                clientAddFiles();
                clientSearch("*");
//                clientHasRegistered();
            }
        }).start();
    }

//    private static void clientHasRegistered() {
//        System.out.println("Type in help to view commands.");
//        while (true) {
//            //prompt user for command
//            Scanner userInput = new Scanner(System.in);
//            String request = userInput.nextLine().toLowerCase();
//
//            //respond to request
//            if (request.equals("help")) {
//                System.out.println("Display help menu - help\nSearch for file - search\nadd file - add\ndownload file - download\n");
//            } else if(request.equals("search")) {
//                //find file client wants to search for
//                clientSearch();
//            } else if(request.equals("add")) {
//                clientAddFiles();
//            } else if(request.equals("download")) {
//                clientDownloadFile();
//            }
//        }
//    }

    static void clientSearch(String fileName) {
        //ask for file name then call search thread
//        frame.log("Type file to search for: ");
//        Scanner userInput = new Scanner(System.in);
        new ClientSearchThread("127.0.0.1", 9090, fileName, new ClientSearchThread.SearchCallback() {
            @Override
            public void searchResults(ArrayList<SearchResult> searchResults) {
                frame.logln("Total Search Results: " + searchResults.size());
                frame.logln(searchResults.toString());
            }
        }).start();
    }

    public static void clientAddFiles() {
        boolean addMore = true;
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
            new ClientUpdateFileThread("127.0.0.1", 9090, files, "temp", new ClientUpdateFileThread.UpdateFilesCallback() {
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

    static void clientDownloadFile(String fileName) {
        //get ip address
//        frame.log("Type peer's IPAddress: ");
//        Scanner userInput = new Scanner(System.in);
//        String address = userInput.nextLine();

        //get filename
//        frame.logln("Type file name: ");
//        userInput = new Scanner(System.in);
//        String fileName = userInput.nextLine();
//
        //create download thread
        new ClientDownloadFileThread("127.0.0.1", 9092, fileName).start();
    }

    private static void pingComputer(String uuid) {
        new ClientPingThread("127.0.0.1", 9091, uuid).start();
    }

}

