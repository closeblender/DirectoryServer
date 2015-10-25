package com.ndsucsci.client;

import com.ndsucsci.objects.SearchResult;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ClientFrame extends JFrame {
    private JPanel contentPane;
    private JButton buttonConnect;
    private JButton buttonExit;
    public JList filesList;
    public JList peersList;
    private JProgressBar progressBar1;
    private JTextArea log;
    public JTextField hostTextField;
    public JTextField portTextField;

    public ClientFrame() {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonConnect);

        buttonConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onConnect();
            }
        });

        buttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });
    }

    private void onConnect() {
// add your code here
        buttonConnect.setEnabled(false);
        Client.connect();
        Client.clientAddFiles();
        //Client.
    }

    private void onExit() {
// add your code here if necessary
        dispose();
        System.exit(0);
    }

    public void logln(String s) {
        log.append(s + "\n");
        System.out.println(s);
    }

    public void log(String s) {
        log.append(s);
        System.out.print(s);
    }

    public void ConnectReset(){
        buttonConnect.setEnabled(true);
    }
}
