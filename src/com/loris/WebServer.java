package com.loris;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {


    public void serverStart(int port) {
        try {
            //1,监听80端口
            ServerSocket serverSocket = new ServerSocket(port);
            //2,获取页面信息
            while (true) {
                Socket socket = serverSocket.accept();
                //3,process是一个多线程的类，每个用户发过来的请求都会开启一个线程，并且获取一个processor实例
                new Processor(socket).start();

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("信息：重要错误，发生IOException！");

        }


    }


    //main方法通过命令行的方式启动
    public static void main(String[] args) {
        //1,首先判断是否有参数传递过来
        int port = 80;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        new WebServer().serverStart(port);
    }

}
