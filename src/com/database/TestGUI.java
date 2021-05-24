package com.database;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;
public class TestGUI {
    //使用事件调度线程
    private static void method1()
    {
        //窗体的初始化
        JFrame f = new JFrame("LoL");
        f.setSize(400, 300);
        f.setLocation(580, 200);
        f.setLayout(null);
        final JLabel l = new JLabel();
        ImageIcon i = new ImageIcon("e:/前端/图片/0.jpg");
        l.setIcon(i);
        l.setBounds(50, 50, i.getIconWidth(), i.getIconHeight());
        JButton b = new JButton("隐藏图片");
        b.setBounds(150, 200, 100, 30);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                l.setVisible(false);
                System.out.println("当前使用的是事件调度线程：" + SwingUtilities.isEventDispatchThread());
                //使用该方法查看是否是事件调度线程
            }
        });
        f.add(l);
        f.add(b);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    //对比使用事件调度线程与SwingWorker来执行耗时任务
    private static void method2()
    {
        JFrame f = new JFrame("LoL");
        f.setSize(300, 300);
        f.setLocation(200, 200);
        f.setLayout(new FlowLayout());

        JButton b1 = new JButton("在事件调度线程中执行长耗时任务");
        JButton b2 = new JButton("使用SwingWorker执行长耗时任务");
        JLabel l = new JLabel("任务执行结果");
        f.add(b1);
        f.add(b2);
        f.add(l);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        b1.addActionListener(new ActionListener() {
            @Override
            //默认使用事件调度线程去执行耗时任务
            public void actionPerformed(ActionEvent e)
            {
                l.setText("开始执行完毕");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                l.setText("任务执行完毕");
            }
        });
        b2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //定义一个SwingWorker，并重写其doInBackgroud方法，来解决问题
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        System.out.println("执行这个SwingWorder的线程是：" + Thread.currentThread().getName());
                        l.setText("开始执行完毕");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        l.setText("任务执行完毕");
                        return null;
                    }
                };
                worker.execute();
                //使用execute来执行任务

            }
        });
        f.setVisible(true);
    }

    //使用Swing来攥写一个查找文件内容的小软件
    private static void method3()
    {
        SwingUtilities.invokeLater(()->new SearchFrame().setVisible(true));
    }

    static class SearchFrame extends JFrame
    {
        JLabel lLocation = new JLabel("查询目录");
        JLabel lSearch = new JLabel("文件内容");

        JTextField tfLocation = new JTextField("E:\\JAVA\\Workspace");
        JTextField tfSearch = new JTextField("text");
        JButton bSubmit = new JButton("搜索");

        private static int foundCount = 0;
        //将所有控件都激活
        private void unfreeze()
        {
            bSubmit.setText("搜索");
            bSubmit.setEnabled(true);
            tfLocation.setEnabled(true);
            tfSearch.setEnabled(true);
        }

        //将所有控件都封锁
        private void freeze()
        {
            bSubmit.setText("正在搜索");
            bSubmit.setEnabled(false);
            tfLocation.setEnabled(false);
            tfSearch.setEnabled(false);
        }

        //面板设置初始化，布局
         SearchFrame()
        {
            int gap = 50;
            this.setLayout(null);
            JPanel pInput = new JPanel();
            JPanel pSubmit = new JPanel();
            pInput.setLayout(new GridLayout(2, 2, gap, gap));
            pInput.add(lLocation);
            pInput.add(tfLocation);
            pInput.add(lSearch);
            pInput.add(tfSearch);
            pSubmit.add(bSubmit);
            pInput.setBounds(50, 20, 200, 100);
            pSubmit.setBounds(0, 130, 300, 150);
            this.add(pInput);
            this.add(pSubmit);
            this.setSize(300, 200);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            bSubmit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent)
                {
                    foundCount=0;
                    String location=tfLocation.getText();
                    String search=tfSearch.getText();
                    if(location.length()==0)
                    {
                        JOptionPane.showMessageDialog(null,"搜索目录不能为空");
                        tfLocation.grabFocus();
                        return;
                    }

                    if(search.length()==0)
                    {
                        JOptionPane.showMessageDialog(null,"搜索目录不能为空");
                        tfLocation.grabFocus();
                        return;
                    }

                    File folder =new File(location);
                        if(!folder.exists())
                        {
                            JOptionPane.showMessageDialog(null,"搜索目录不存在");
                            tfLocation.grabFocus();
                            return;
                        }

                    if(!folder.isDirectory())
                    {
                        JOptionPane.showMessageDialog(null,"搜索目录不是一个文件夹");
                        tfLocation.grabFocus();
                        return;
                    }
                    freeze();
                    SwingWorker worker=new SwingWorker() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            search(folder,search);
                            JOptionPane.showMessageDialog(null, "总共找到满足条件的文件: " + foundCount + " 个");
                            unfreeze();
                            return null;
                        }
                    };
                    worker.execute();
                }
            });
        }

        public static void search(File file,String search) throws IOException
        {
            if(file.isFile())
            {
                if (file.getName().toLowerCase().endsWith(".txt"))
                {
                    try {
                        String fileContent=readFileContent(file);
                        if(fileContent.contains(search))
                        {
                            foundCount++;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(file.isDirectory())
            {
                File []fs=file.listFiles();
                for (File f:fs)
                {
                    search(f,search);
                }
            }
        }

        public static String readFileContent(File file) throws IOException
        {
            FileReader fileReader=new FileReader(file);
            char []all=new char[(int)file.length()];
            fileReader.read(all);
            return new String(all);
        }
    }

    public static void main(String[] args)
    {
//        method1();
//        method2();
        method3();
    }
}