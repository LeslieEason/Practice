package com.reflection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestReflection
{

    //三种获得类对象的方法
    private static void method1() throws ClassNotFoundException
    {
        String Classname="com.reflection.Hero";
        Class pClass1=Class.forName(Classname);
        Class pClass2=Hero.class;
        Class pClass3=new Hero().getClass();

    }

    //在静态方法中使用同步synchronized
    private static void method2() throws InterruptedException
    {
        synchronized (TestReflection.class)
        {
            System.out.println(Thread.currentThread().getName()+"进入了method2");
            System.out.println("运行了5s");
            Thread.sleep(5000);
        }
    }

    //使用synchronized修饰静态方法
    private synchronized static void method3() throws InterruptedException
    {

            System.out.println(Thread.currentThread().getName()+"进入了method3");
            System.out.println("运行了5s");
            Thread.sleep(5000);

    }


    private static void method4() throws InterruptedException
    {
        Thread t1= new Thread(){
            public void run(){
                //调用method1
                try {
                    TestReflection.method2();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.setName("第一个线程");
        t1.start();

        //保证第一个线程先调用method1
        Thread.sleep(1000);

        Thread t2= new Thread(){
            public void run(){
                //调用method2
                try {
                    TestReflection.method3();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t2.setName("第二个线程");
        t2.start();
    }

    //使用反射机制来创建对象,原理是通过构造器
    private static void method5() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        Class pClass1=Hero.class;
        Constructor c=pClass1.getConstructor();
        Hero h=(Hero)c.newInstance();
        h.name="gareen";
        System.out.println(h);
    }

    //使用配置文件来得到Hero对象，根据配置文件的不同，可以得到不同的对象，Spring中的IOC和DI的底层就是基于这样的机制实现的。
    private static void method6() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        File f= new File("E:\\JAVA\\Workspace\\Practice\\src\\com\\reflection\\Hero.config");
        FileReader fr=new FileReader(f);
        char []all=new char[(int)f.length()];
        fr.read(all);
        String classname=new String(all);
        Class clazz=Class.forName(classname);
        Constructor c=clazz.getConstructor();
        Hero h=(Hero)c.newInstance();
        System.out.println(h);
    }

    //使用反射机制来访问对象的属性值，有两种方式
    private static void method7() throws NoSuchFieldException, IllegalAccessException
    {
        Hero h=new Hero("garen",700,123,1);
        Field field=h.getClass().getField("name");
        field.set(h,"teemo");
//        Field field1=h.getClass().getField("id");
//        field1.set(h,2);
//        这种方式无法获取private字段的值
//        Field field1=h.getClass().getDeclaredField("id");
//        field1.set(h,2);
//        可以获取，但是无法修改
        Field field1=h.getClass().getDeclaredField("id");
        field1.setAccessible(true);
        field1.set(h,2);
        System.out.println(h);
    }

    //使用反射机制来访问方法
    private static void method8() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Hero h=new Hero();
        Method m = h.getClass().getMethod("setName", String.class);
        m.invoke(h,"盖伦");
        //        构造方法貌似不行
//        Method m=h.getClass().getMethod("Hero",String.class,Float.class,Integer.class,Integer.class);
//        m.invoke(h,"盖伦",710.0,100,1);
        System.out.println(h);
    }

    //同样使用配置文件得到两个ADHero、APHero对象，并设置名称，调用方法攻击第二个英雄
    private static void method9() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        File f= new File("E:\\JAVA\\Workspace\\Practice\\src\\com\\reflection\\Hero.config");
        FileReader fr=new FileReader(f);
        char []all=new char[(int)f.length()];
        fr.read(all);
        String FileContent=new String(all);
        String []cs=FileContent.split("\r\n");
        String hero1Classname=cs[0];
        String hero1Name=cs[1];
        String hero2Classname=cs[2];
        String hero2Name=cs[3];

        //使用反射机制创建hero1对象，并给属性赋值
        Class hero1Class=Class.forName(hero1Classname);
        Constructor hero1C=hero1Class.getConstructor();
        Object h1=hero1C.newInstance();
        Field hero1F=hero1Class.getDeclaredField("name");
        hero1F.set(h1,hero1Name);

        //使用反射机制创建hero2对象，并给属性赋值
        Class hero2Class=Class.forName(hero2Classname);
        Constructor hero2C=hero2Class.getConstructor();
        Object h2=hero2C.newInstance();
        Field hero2F=hero2Class.getDeclaredField("name");
        hero2F.set(h2,hero2Name);

        //使用反射机制调用方法
        Method method=hero1Class.getMethod("attackHero",Hero.class);
        method.invoke(h1,h2);
    }

    public static void main(String[] args) throws ClassNotFoundException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, NoSuchFieldException {
//        method1();
//        method4();
//        method5();
//        method6();
//        method7();
//        method8();
        method9();
    }
}
