package com.uycode.socket;

import com.uycode.entity.Student;
import com.uycode.utils.C3P0Tools;
import com.uycode.utils.JDBCTools;
import jdk.net.Sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author ahmatjan(UyCode)
 * @email Hyper-Hack@outlook.com
 * @since 4/14/2021 19:10
 */

public class Server {

    public static void runServer() {
        try {
            // 创建一个ServerSocket监听8080端口
            ServerSocket serverSocket = new ServerSocket(8080);

            // 等待请求
            Socket socket = serverSocket.accept();

            // 接收到请求后使用socket进行通信, 创建BufferedReader用于读取数据
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()
            ));

            String line = bufferedReader.readLine();

            System.out.println("received from client :" + line);

            // 从数据库获取client要的数据
           Student student = getStudentById(Integer.parseInt(line));

            // 创建printWriter用于发送数据
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println(student);
            printWriter.flush();

            // 关闭资源
            printWriter.close();
            bufferedReader.close();
            socket.close();
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Student getStudentById(Integer id) {

        Connection connection = C3P0Tools.getConnection();
        String sql = "select * from student where id = ?;";
        JDBCTools<Student> result = new JDBCTools<>();
        return result.getBean(connection, sql, Student.class,id);
    }
}
