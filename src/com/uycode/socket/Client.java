package com.uycode.socket;

import java.io.*;
import java.net.Socket;

/**
 * @author ahmatjan(UyCode)
 * @email Hyper-Hack@outlook.com
 * @since 4/14/2021 19:21
 */

public class Client {

    public static void runClient() {

        Integer id = 1;

        try {
            // 创建Socket, 连接服务器(目前为本机)
            Socket socket = new Socket("127.0.0.1", 8080);

            // 使用Socket创建Print Writer和BufferedReader进行读写数据
            Writer out;
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());

            InputStream in;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            // 发送数据
            printWriter.println(id);
            printWriter.flush();

            // 接收数据
            String line = bufferedReader.readLine();
            System.out.println("Received from server: " + line);

            // 关闭资源
            printWriter.close();
            bufferedReader.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
