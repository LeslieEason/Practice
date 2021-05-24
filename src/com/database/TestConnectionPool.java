package com.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestConnectionPool
{
    private static int threadNumber = 100;
    public static int insertTime = 1;
    public static void main(String[] args) throws SQLException, ClassNotFoundException
    {
//        method1();
//        connectionPoolWay();
        traditionalWay();

    }

    //测试获取3个数据库链接
    //然后创建100个线程，每个线程都会从连接池中借用连接，
    // 并且在借用之后，归还连接。 拿到连接之后，执行一个耗时1秒的SQL语句。
    private static void method1() throws SQLException, ClassNotFoundException
    {
        ConnectionPool cp=new ConnectionPool(3);
        for(int i=0;i<100;i++)
            new WorkingThread("thread"+i,cp).start();
    }


    private static void connectionPoolWay() throws SQLException, ClassNotFoundException
    {
        ConnectionPool cp = new ConnectionPool(10);

        System.out.println("开始连接池方式插入数据测试：");
        long start = System.currentTimeMillis();
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < threadNumber; i++) {
            Thread t =new ConnectionpoolWorkingThread(cp);
            t.start();
            ts.add(t);
        }
        //等待所有线程结束
        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        long end = System.currentTimeMillis();

        System.out.printf("使用连接池方式，启动%d条线程，每个线程插入%d条数据，一共耗时%d 毫秒%n",threadNumber,insertTime,end-start);

    }

    private static void traditionalWay() throws SQLException, ClassNotFoundException
    {
        System.out.println("开始传统方式插入数据测试：");
        long start = System.currentTimeMillis();
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < threadNumber; i++) {
            Thread t =new TraditionalWorkingThread();
            t.start();
            ts.add(t);
        }
        //等待所有线程结束
        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();

        System.out.printf("使用传统方式，启动%d条线程，每个线程插入%d条数据，一共耗时%d 毫秒%n",threadNumber,insertTime,end-start);

    }

}

class WorkingThread extends Thread
{
    private ConnectionPool cp;
    public WorkingThread(String name,ConnectionPool cp)
    {
        super(name);
        this.cp=cp;
    }
    public void run()
    {
        Connection c= null;
        try {
            c = cp.getConnection();
            System.out.println(this.getName()+"获取了一个数据库连接，开始工作");
            String sql="select * from test";
            PreparedStatement psmt=c.prepareStatement(sql);
            Thread.sleep(1000);
            psmt.executeQuery();
        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }
}

//不使用数据库池
class TraditionalWorkingThread extends Thread
{
    private String Sqlusername="root";
    private String Sqluserpassword="admin";
    private PreparedStatement pstmt;
    private Connection con = null;
    private Statement st1=null;
    String drivername = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://127.0.0.1:3306/flightsystem?useSLL=false/useUnicode=true&characterEncoding=utf8";
    public  TraditionalWorkingThread() throws SQLException,ClassNotFoundException
    {
        Class.forName(drivername);
        con= DriverManager.getConnection(url,Sqlusername,Sqluserpassword);
    }

    public void run()
    {
        try {
            for(int i=0;i<TestConnectionPool.insertTime;i++)
            {
                int n= (int)Math.pow(i,i);
                String sql="INSERT INTO test VALUES(null,?,?)";
                pstmt=con.prepareStatement(sql);
                pstmt.setString(1,"51234345"+i);
                pstmt.setInt(2,n);
                pstmt.execute();
            }
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

    }
}

//使用数据库池
class ConnectionpoolWorkingThread extends Thread
{
    private String Sqlusername="root";
    private String Sqluserpassword="admin";
    private PreparedStatement pstmt;
    private Connection con = null;
    private Statement st1=null;
    String drivername = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://127.0.0.1:3306/flightsystem?useSLL=false/useUnicode=true&characterEncoding=utf8";
    private ConnectionPool cp;
    public ConnectionpoolWorkingThread(ConnectionPool cp) throws ClassNotFoundException, SQLException {
        Class.forName(drivername);
        con= DriverManager.getConnection(url,Sqlusername,Sqluserpassword);
        this.cp = cp;
    }

    public void run()
    {
        Connection c = null;
        try {
            c = cp.getConnection();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int i=0;i<TestConnectionPool.insertTime;i++)
        {
            int n= (int)Math.pow(i,i+1);
            String sql="INSERT INTO test VALUES(null,?,?)";
            try {
                pstmt=con.prepareStatement(sql);
                pstmt.setString(1,"123123"+i);
                pstmt.setInt(2,n);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                pstmt.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        cp.returnConnection(c);
    }
}