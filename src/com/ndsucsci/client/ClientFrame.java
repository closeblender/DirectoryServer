package com.ndsucsci.client;

import javax.swing.*;
import java.awt.event.*;

public class ClientFrame extends JFrame {
    private JPanel contentPane;
    private JButton buttonConnect;
    private JButton buttonExit;
    private JList files;
    private JList peers;
    private JProgressBar progressBar1;
    private JTextArea log;
    public JTextField hostTextField;
    public JTextField portTextField;

    public ClientFrame() {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonConnect);

        buttonConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
    }

    private void onOK() {
// add your code here
        buttonConnect.setEnabled(false);
        Client.connect();
    }

    private void onCancel() {
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
}
