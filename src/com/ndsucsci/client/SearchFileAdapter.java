package com.ndsucsci.client;

import com.ndsucsci.objects.SearchResult;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;

/**
 * Created by closestudios on 10/25/15.
 */
public class SearchFileAdapter implements ListSelectionListener {

    JList fileList;
    JList peerList;
    ArrayList<SearchResult> results;

    public SearchFileAdapter(JList fileList, JList peerList, ArrayList<SearchResult> results) {
        this.fileList = fileList;
        this.peerList = peerList;
        this.results = results;
    }

    public void valueChanged(ListSelectionEvent e) {

        //System.out.println("Selection Changed!");

        DefaultListModel peersJlist = new DefaultListModel();

        if(fileList.getSelectedValue() != null) {
            String filename = fileList.getSelectedValue().toString();
            //System.out.println("Searching File: " + filename);
            for (SearchResult sr : results) {
                if(sr.filename.equals(filename)) {
                    //System.out.println("Found File!");
                    peersJlist.addElement(sr.ipAddress);
                }
            }
        }

        peerList.setModel(peersJlist);

    }


}
