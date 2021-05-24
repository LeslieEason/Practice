package com.socket.robot;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server
{
    private static List<String> cannotUnderstand= new ArrayList<>();
    static
    {
        cannotUnderstand.add("听求不懂啊");
        cannotUnderstand.add("说人话");
        cannotUnderstand.add("再说一遍？");
        cannotUnderstand.add("大声点");
        cannotUnderstand.add("老子在忙，一边玩儿去");
    }

    public Server() throws IOException, SQLException, ClassNotFoundException
    {
        ServerSocket ss=new ServerSocket(8888);
        System.out.println("监听端口号：8888");
        Socket s=ss.accept();
        //使用一个字节流来接受从客户端上传的流数据
        InputStream inputStream=s.getInputStream();
        DataInputStream dataInputStream=new DataInputStream(inputStream);
        //使用字节流来返回数据给客户端
        OutputStream outputStream=s.getOutputStream();
        DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
        while(true)
        {
            //拿到客户端输入的数据，并输出
            String msg=dataInputStream.readUTF();
            System.out.println(msg);
            //到DAO list去查询，拿到一个response
            List<Dictionary> ds= new DictionaryDAO().query(msg);
            String response = null;
            //如果该list没有东西，则从cannotUnderstand中拿response
            if(ds.isEmpty())
            {
                Collections.shuffle(cannotUnderstand);
                response=cannotUnderstand.get(0);
            }
            //如果有东西，则从数据库任意拿一个response
            else
            {
                Collections.shuffle(ds);
                response=ds.get(0).response;
            }
            //使用字节流将数据写给Client
            dataOutputStream.writeUTF(response);
        }
    }
}
