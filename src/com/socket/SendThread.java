package com.socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
//使用线程来发送数据
public class SendThread extends Thread
{
    private Socket s;

    public SendThread(Socket s)
    {
        this.s = s;
    }

    public void run()
    {
        OutputStream outputStream= null;
        try {
            outputStream = s.getOutputStream();
            DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
            while(true)
            {
                Scanner sc=new Scanner(System.in);
                String str=sc.next();
                dataOutputStream.writeUTF(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
