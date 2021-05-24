package com.socket;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//一些常用的Socket编程中的方法
public class TestSocket
{
    //获取本机的ip地址
    private static void method1() throws UnknownHostException
    {
        InetAddress inetAddress=Inet4Address.getLocalHost();
        String ip=new String(inetAddress.getHostAddress());
        System.out.println("本机的ip地址是"+ip);

    }

    //发送ping命令，并获取结果，输出
    private static void method2() throws IOException
    {
        Process p=Runtime.getRuntime().exec("ping "+"183.232.231.174");
        BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line=null;
        StringBuilder stringBuilder=new StringBuilder();
        while((line=br.readLine())!=null)
        {
            if(line.length()!=0)
                stringBuilder.append(line+"\r\n");
        }
        System.out.println("本次返回的消息是：");
        System.out.println(stringBuilder.toString());
    }

//    配合线程池来判断当前网段内有多少个可用的ip地址
    private static void method3() throws UnknownHostException, InterruptedException
    {
        InetAddress inetAddress=InetAddress.getLocalHost();
        String ip=inetAddress.getHostAddress();
        String ipRange=ip.substring(0,ip.lastIndexOf('.'));
        System.out.println("本机ip地址：" + ip);
        System.out.println("网段是: " + ipRange);

        //使用线程安全的list去存网段可用ip
        List <String> ips= Collections.synchronizedList(new ArrayList<>());
        ThreadPoolExecutor executor=new ThreadPoolExecutor(10,15,60, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>());
        AtomicInteger number=new AtomicInteger();
        //使用线程安全的原子类来进行计数操作，避免脏数据
        for(int i=0;i<255;i++)
        {
            String testIP=ipRange+"."+(i+1);
            executor.execute(new Runnable()
            {
                //线程池里每次要进行的操作
                @Override
                public void run() {
                    try {
                        boolean reachable=isReachable(testIP);
                        if(reachable)
                            ips.add(testIP);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    synchronized (number)
                    {
                        System.out.println("已经完成"+number.incrementAndGet()+" 个ip测试");
                    }
                }
            });
        }
        executor.shutdown();//关闭线程池
        if(executor.awaitTermination(1,TimeUnit.HOURS))
        {
            System.out.println("如下ip地址可以连接");
            for(String value:ips)
                System.out.println(value);
            System.out.println("总共有:"+ips.size()+"个ip地址可用");
        }
    }

    //ping 范围内的ip是否可用，方式是看其返回的串有无TLL
    private static boolean isReachable(String testIP) throws IOException
    {
        boolean reachable=false;
        Process p=Runtime.getRuntime().exec("ping -n+1 "+testIP);
        BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line=null;
        StringBuilder stringBuilder=new StringBuilder();
        while((line=br.readLine())!=null)
        {
            if(line.length()!=0)
                stringBuilder.append(line+"\r\n");
        }
        reachable=stringBuilder.toString().contains("TTL");
        br.close();
        return reachable;
    }

    //服务端与客户端的通信，需要打开两个工程
    //服务端
    public static void method4() throws IOException
    {
        Server s=new Server();
    }

    //客户端
    public static void method5() throws IOException
    {
        Client c=new Client();
    }

    public static void main(String[] args) throws IOException, InterruptedException
    {
        //method1();
        //method2();
        //method3();
        method4();
    }

}
class Server
{
    public Server() throws IOException
    {
        ServerSocket ss=new ServerSocket(8888);
        System.out.println("监听端口号：8888");
        Socket s=ss.accept();
        new SendThread(s).start();
        new ReceiveThread(s).start();
//        InputStream inputStream=s.getInputStream();
//        DataInputStream dataInputStream=new DataInputStream(inputStream);
//        OutputStream outputStream=s.getOutputStream();
//        DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
//        while(true)
//        {
//            String msg=dataInputStream.readUTF();
//            System.out.println("收到客户端消息： "+msg);
//            Scanner sc=new Scanner(System.in);
//            String str=sc.next();
//            dataOutputStream.writeUTF(str);
//        }
    }
}

class Client
{
    public Client() throws IOException
    {
        Socket s=new Socket("127.0.0.1",8888);
        new SendThread(s).start();
        new ReceiveThread(s).start();
//        InputStream inputStream=s.getInputStream();
//        DataInputStream dataInputStream=new DataInputStream(inputStream);
//        OutputStream outputStream=s.getOutputStream();
//        DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
//        while(true)
//        {
//            Scanner sc=new Scanner(System.in);
//            String msg=sc.next();
//            dataOutputStream.writeUTF(msg);
//            String str=dataInputStream.readUTF();
//            System.out.println("收到服务端消息： "+str);
//        }
    }
}
