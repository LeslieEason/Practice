package com.database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//定义一个数据库池
public class ConnectionPool {
    List <Connection> cs=new ArrayList<>();
    int size;
    private String Sqlusername="root";
    private String Sqluserpassword="admin";
    private Connection con = null;
    String drivername = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://127.0.0.1:3306/flightsystem?useSLL=false/useUnicode=true&characterEncoding=utf8";
    public ConnectionPool(int size) throws ClassNotFoundException, SQLException
    {
        this.size=size;
        init();
    }

    //初始化数据库池的数量
    private void init() throws ClassNotFoundException, SQLException
    {
        Class.forName(drivername);
        for (int i=0;i<size;i++)
        {
            con= DriverManager.getConnection(url,Sqlusername,Sqluserpassword);
            cs.add(con);
        }

    }

    //获取一个数据库链接
    public synchronized Connection getConnection() throws InterruptedException
    {
        while(cs.isEmpty())
        {
            this.wait();
        }

        Connection c=cs.remove(0);
        return c;
    }

    public synchronized void returnConnection(Connection c)
    {
        cs.add(c);
        this.notifyAll();
    }
}
