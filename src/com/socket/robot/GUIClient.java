package com.socket.robot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GUIClient
{
    public GUIClient() throws IOException {
        JFrame f = new JFrame();
        f.setTitle("client");
        f.setSize(400, 300);
        f.setLocation(600, 200);
        f.setLayout(null);
        JButton b = new JButton("send");
        b.setBounds(10, 10, 80, 30);
        f.add(b);
        final JTextField tf = new JTextField();
        tf.setBounds(10, 110, 80, 30);
        f.add(tf);
        final JTextArea ta = new JTextArea();
        ta.setBounds(110,10, 200, 300);
        f.add(ta);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        final Socket s = new Socket("127.0.0.1", 8888);
        new Thread()
        {
            public void run()
            {
                while(true)
                {
                    try {
                        DataInputStream dataInputStream=new DataInputStream(s.getInputStream());
                        String text=dataInputStream.readUTF();
                        ta.append(text+"\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.start();
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String text=tf.getText();
                ta.append(text+"\r\n");
                try {
                    DataOutputStream dataOutputStream=new DataOutputStream(s.getOutputStream());
                    dataOutputStream.writeUTF(text);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
