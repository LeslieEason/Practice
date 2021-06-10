package com.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestDataBase
{
    private String Sqlusername="root";
    private String Sqluserpassword="admin";
    private PreparedStatement pstmt;
    private Connection con = null;
    private Statement st1=null;
    String drivername = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://127.0.0.1:3306/flightsystem?useSLL=false/useUnicode=true&characterEncoding=utf8";
    ResultSet rs;
    public TestDataBase() throws ClassNotFoundException, SQLException
    {
        Class.forName(drivername);
        con= DriverManager.getConnection(url,Sqlusername,Sqluserpassword);
        DatabaseMetaData metaData= con.getMetaData();
        rs=metaData.getCatalogs();
        // 获取数据库服务器产品名称
        System.out.println("数据库产品名称:\t"+metaData.getDatabaseProductName());
        // 获取数据库服务器产品版本号
        System.out.println("数据库产品版本:\t"+metaData.getDatabaseProductVersion());
        // 获取数据库服务器用作类别和表名之间的分隔符 如test.user
        System.out.println("数据库和表分隔符:\t"+metaData.getCatalogSeparator());
        // 获取驱动版本
        System.out.println("驱动版本:\t"+metaData.getDriverVersion());

        System.out.println("可用的数据库列表：");
        // 获取数据库名称
        while(rs.next())
        {
            System.out.println("数据库名称:\t"+rs.getString(1));
        }
    }

    //使用prestatement的方式插入数据到test表，并返回递增id
    private void inserttest(String name,int number) throws SQLException
    {
        String sql="INSERT INTO test VALUES(null,?,?)";
        pstmt=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1,name);
        pstmt.setInt(2,number);
        pstmt.execute();
        rs=pstmt.getGeneratedKeys();
        if (rs.next())
        {
            int id=rs.getInt(1);
            System.out.println(id);
        }
    }

    //生成10个数据，插入到Test表，并输出自递增id
    private static void method1() throws SQLException, ClassNotFoundException
    {
        TestDataBase dataBase=new TestDataBase();
        for (int i=0;i<10;i++)
            dataBase.inserttest("teemo"+i,12345+i);
    }


//    当插入一条数据之后，通过获取自增长id，得到这条数据的id，比如说是55.
//        删除这条数据的前一条，54.
//    如果54不存在，则删除53，以此类推直到删除上一条数据。
    private void insertfordelete(String name,int number) throws SQLException {
        String sql="INSERT INTO test VALUES(null,?,?)";
        pstmt=con.prepareStatement(sql);
        st1=con.createStatement();
        pstmt.setString(1,name);
        pstmt.setInt(2,number);
        pstmt.execute();
        rs=pstmt.getGeneratedKeys();
        int id=-1;
        if (rs.next())
        {
            id=rs.getInt(1);
        }
        System.out.println("刚插入的数据的id是"+id);
        for(int i=id-1;i>0;i--)
        {
            int targetid=i;
            ResultSet rs2= st1.executeQuery("select id from test where id ="+targetid);
            if(rs2.next())
            {
                System.out.println("id="+targetid+"的数据存在，删除该数据");
                String deleteSql="delete from test where id="+targetid;
                st1.execute(deleteSql);
                break;
            }
        }
    }

    private static void method2() throws SQLException, ClassNotFoundException {
        TestDataBase dataBase=new TestDataBase();
            dataBase.insertfordelete("teemoabcd",12345);
    }

    //通过事务，删除表中的前10条数据，通过控制台询问是否删除，只有回答是、否才完成操作
    private void deletebyaffairs() throws SQLException {
        Statement st4Query = con.createStatement();
        Statement st4Delete = con.createStatement();
        Scanner s = new Scanner(System.in);
        {
            //把自动提交关闭
            con.setAutoCommit(false);
            //查出前10条
            ResultSet rs =st4Query.executeQuery("select id from test order by id asc limit 0,10 ");
            while(rs.next())
            {
                int id = rs.getInt(1);
                System.out.println("试图删除id="+id+" 的数据");
                st4Delete.execute("delete from test where id = " +id);
            }
        }
        //是否删除这10条
        while(true){
            System.out.println("是否要删除数据(Y/N)");
            String str = s.next();
            if ("Y".equals(str)) {
                //如果输入的是Y，则提交删除操作
                con.commit();
                System.out.println("提交删除");
                break;
            } else if ("N".equals(str)) {
                System.out.println("放弃删除");
                break;
            }
        }

    }

    private static void method3() throws SQLException, ClassNotFoundException
    {
        TestDataBase dataBase=new TestDataBase();
        dataBase.deletebyaffairs();
    }

    //根据ORM思想，为test表设计常用的方法
    //使用DAO来封装SQL操作，使得所有的JDBC都在内部
    static class HumanDAO implements DAO
    {
        private String Sqlusername="root";
        private String Sqluserpassword="admin";
        private PreparedStatement pstmt;
        private Connection con = null;
        private Statement st1=null;
        String drivername = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3306/flightsystem?useSLL=false/useUnicode=true&characterEncoding=utf8";
        ResultSet rs;
        public HumanDAO() throws ClassNotFoundException, SQLException
        {
            Class.forName(drivername);
            con= DriverManager.getConnection(url,Sqlusername,Sqluserpassword);
        }

        //获取数据库表数据来生成一个对象
        public Human get(int id) throws SQLException
        {
            Human human=null;
            String sql="select * from test where id ="+id;
            pstmt=con.prepareStatement(sql);
            ResultSet rs=pstmt.getResultSet();
            if(rs.next())
            {
                human=new Human(rs.getInt(1),rs.getString(2),rs.getInt(3));
            }
            return human;
        }

        //将一个对象增加到数据库表
        //因为是自增列，所以需要指定id
        public void add(Human h) throws SQLException
        {
            String sql="insert into test values(?,?,?)";
            pstmt=con.prepareStatement(sql);
            pstmt.setInt(1,h.id);
            pstmt.setString(2,h.name);
            pstmt.setInt(3,h.number);
            pstmt.execute();
        }

        //删除对象对应数据库表中的记录
        public void delete(Human h) throws SQLException
        {
            String sql="delete from test where id=?";
            pstmt=con.prepareStatement(sql);
            pstmt.setInt(1,h.id);
            pstmt.execute();
        }

        //更新对象对应数据库表中的记录
        public void update(Human h) throws SQLException
        {
            String sql="update test set name=?,number=? where id =?";
            pstmt=con.prepareStatement(sql);
            pstmt.setString(1,h.name);
            pstmt.setInt(2,h.number);
            pstmt.setInt(3,h.id);
            pstmt.execute();
        }

        //查询所有数据，并放到一个集合
        public List<Human> list() throws SQLException
        {
            List <Human> humanList=new ArrayList<>();
            String sql="select * from test";
            pstmt=con.prepareStatement(sql);
            pstmt.executeQuery();
            ResultSet rs=pstmt.getResultSet();
            while (rs.next())
            {
                Human human=new Human(rs.getInt(1),rs.getString(2),rs.getInt(3));
                humanList.add(human);
            }
            return humanList;
        }
    }

    private static void method4() throws SQLException, ClassNotFoundException
    {
        HumanDAO humanDAO=new HumanDAO();
        //List<Human> humanList=humanDAO.list();
        //System.out.println(humanList);
        Human h1=new Human(33,"jj",44569);
        Human h2=new Human(32,"mf",44569);
        humanDAO.add(h1);
        humanDAO.delete(h1);
        humanDAO.update(h2);
    }

    //对比使用数据库池和不使用两种方式插入数据的性能
    private static void method5()
    {



    }


    public static void main(String[] args) throws SQLException, ClassNotFoundException
    {
//        method1();
//        method2();
//        method3();
//        method4();

    }

}