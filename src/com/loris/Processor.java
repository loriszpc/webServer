package com.loris;

import java.io.*;
import java.net.Socket;

public class Processor extends Thread {

    private Socket socket;
    private InputStream in;
    private PrintStream out;

    public final static String WEB_ROOT = "D:\\D\\Project\\geect_project\\webServer\\htdocs";


    public Processor(Socket socket) {
        this.socket = socket;
        // 从请求中获取输入流
        try {
            in = socket.getInputStream();
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //启动线程首先调用run方法
    public void run() {
        String fileName = parse(in);
        sendFile(fileName);
    }


    //根据客户或浏览器发来的输入流进行解析获取要返回的资源
    public String parse(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String fileName = "";
        try {
            String httpMessage = br.readLine();
            String[] content = httpMessage.split(" ");

            if (content.length != 3) {
                this.sendErrorMessage(400, "client query error!");
                return null;
            }

            //一共包含三个部分，1，协议状态号 2，请求文件名称 3，请求版本
            for (String msg : content) {
                System.out.println(msg);
                System.out.println();
            }

            fileName = content[1];

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    //如果协议或者请求出错，返回错误信息
    public void sendErrorMessage(int errorCode, String errorMessage) {
        out.println("HTTP/1.0 " + errorCode + " " + errorMessage);
        out.println("content-type: text/html");
        out.println();
        out.println("<html>");
        out.println("<head>");
        out.println("<title> ERROR Message");
        out.println("</title>");
        out.println("</head>");

        out.println("<body>");
        out.println("<h1> ERRORCODE:" + errorCode + "/h1<br/>");
        out.println("<h1> ERRORMESSAGE:" + errorMessage + "/h1");
        out.println("</body>");
        out.println("</html>");

        out.flush();
        out.close();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendFile(String fileName) {
        File file = new File(Processor.WEB_ROOT + fileName);
        if (!file.exists()) {
            sendErrorMessage(404, "File not found!");
            return;
        }

        try {
            InputStream in = new FileInputStream(file);
            byte[] content = new byte[(int) file.length()];
            in.read(content);
            out.println("HTTP/1.0 200 queryfile");
            out.println("content-length:" + content.length);
            out.println();
            out.write(content);
            out.flush();
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
