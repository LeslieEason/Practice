package com.socket;
import java.io.*;
import java.net.Socket;
//用于接受数据的线程
public class ReceiveThread extends Thread
{
    private Socket s;

    public ReceiveThread(Socket s)
    {
        this.s = s;
    }

    public void run()
    {
        InputStream inputStream= null;
        try {
            inputStream = s.getInputStream();
            DataInputStream dataInputStream=new DataInputStream(inputStream);
            while(true)
            {
                String msg=dataInputStream.readUTF();
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
