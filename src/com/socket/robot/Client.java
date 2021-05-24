package com.socket.robot;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    public Client() throws IOException
    {
        Socket s=new Socket("127.0.0.1",8888);
        InputStream inputStream=s.getInputStream();
        DataInputStream dataInputStream=new DataInputStream(inputStream);
        OutputStream outputStream=s.getOutputStream();
        DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
        while(true)
        {
            Scanner sc=new Scanner(System.in);
            String msg=sc.nextLine();
            //从键盘输入数据，并使用字节流将数据发给服务端
            dataOutputStream.writeUTF(msg);
            String str=dataInputStream.readUTF();
            //读取来自服务端的返回数据，并显示
            System.out.println(str);
        }
    }
}

