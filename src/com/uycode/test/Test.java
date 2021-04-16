package com.uycode.test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.uycode.entity.Account;
import com.uycode.entity.Student;
import com.uycode.utils.C3P0Tools;
import com.uycode.utils.JDBCTools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * @author ahmatjan(UyCode)
 * @email Hyper-Hack@outlook.com
 * @since 4/13/2021 20:48
 */

public class Test {
    public static void main(String[] args) throws Exception{
        /*Connection connection = C3P0Tools.getConnection();

        String sql = "select * from account where id = 1";

        PreparedStatement statement = connection.prepareStatement(sql);

        ResultSet resultSet = statement.executeQuery();

        if(resultSet.next()) {
            int id = resultSet.getInt(1);
            String name = resultSet.getString(2);

            Account account = new Account();
            account.setId(id);
            account.setName(name);

            System.out.println(account.toString());



        }

        C3P0Tools.release(connection,statement, resultSet);*/


        Connection connection = C3P0Tools.getConnection();

        String sql = "select * from student where id = ?;";

        JDBCTools<Student> result = new JDBCTools<>();

        Student student = result.getBean(connection, sql, Student.class,1);
        System.out.println(student);


    }
}
