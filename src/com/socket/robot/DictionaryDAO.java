package com.socket.robot;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
//封装人机对话的JDBC操作类
public class DictionaryDAO
{
    private String Sqlusername="root";
    private String Sqluserpassword="admin";
    private PreparedStatement pstmt;
    private Connection con = null;
    String drivername = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://127.0.0.1:3306/android?useSLL=false/useUnicode=true&characterEncoding=utf8";
    public DictionaryDAO() throws ClassNotFoundException, SQLException
    {
        Class.forName(drivername);
        con= DriverManager.getConnection(url,Sqlusername,Sqluserpassword);
    }

    public List<Dictionary> query(String receive) throws SQLException
    {
        List <Dictionary> ds=new ArrayList<>();
        String sql="select * from dictionary where receive = ?";
        pstmt=con.prepareStatement(sql);
        pstmt.setString(1,receive);
        ResultSet rs=pstmt.executeQuery();
        while (rs.next())
        {
            Dictionary d=new Dictionary(rs.getInt(1),rs.getString(2),rs.getString(3));
            ds.add(d);
        }
        return ds;
    }
}