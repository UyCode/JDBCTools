package com.uycode.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ahmatjan(UyCode)
 * @email Hyper-Hack@outlook.com
 * @since 4/13/2021 21:18
 */

public class JDBCTools <T> {



    public List<T> getBeans(Connection connection, String sql, Class<T> clazz, Object... objects) {

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<T> list = new ArrayList<>();

        try {
            // 查询sql语句
            statement = connection.prepareStatement(sql);

            // 替换参数
            setResultSetParams(statement, objects);

            // 获取数据集
            resultSet = statement.executeQuery();

            // 解析结果集
            parseForList(resultSet, clazz, list);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            C3P0Tools.release(connection, statement, resultSet);
        }

        return list;
    }


    /**
     * 查询对象集合
     * @param connection
     * @param sql
     * @param clazz
     * @return
     */
    public List<T> getBeans(Connection connection, String sql, Class<T> clazz) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<T> list = new ArrayList<>();

        try {
            // 查询sql语句
            statement = connection.prepareStatement(sql);

            //获取结果集
            resultSet = statement.executeQuery();

            // 解析结果集
            parseForList(resultSet, clazz, list);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            C3P0Tools.release(connection, statement, resultSet);
        }

        return list;
    }


    /**
     * 获取复合语句查询的某个对象
     * @param connection
     * @param sql
     * @param clazz
     * @param objects
     * @return
     */
    public T getBean(Connection connection, String sql, Class<T> clazz, Object... objects) {

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // 查询sql语句
            statement = connection.prepareStatement(sql);

            // 替换字段
            setResultSetParams(statement, objects);

            // 获取数据集
            resultSet = statement.executeQuery();

            // 映射成JavaBean
            T object = clazz.getConstructor(null).newInstance(null);

            // 解析结果集
            parseForObject(resultSet, object,clazz);


            return (T) object;
        } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            C3P0Tools.release(connection, statement, resultSet);
        }

        return null;
    }

    /**
     * 通过SQL语句查询某个对象的数据
     * @param connection
     * @param sql
     * @param clazz
     * @return
     */
    public T getBean(Connection connection, String sql, Class<T> clazz) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // 查询sql语句
            statement = connection.prepareStatement(sql);

            // 获取结果集
            resultSet = statement.executeQuery();

            // 映射成JavaBean
            T object = clazz.getConstructor(null).newInstance(null);

            // 解析结果集
            parseForObject(resultSet, object,clazz);

            return (T) object;
        } catch (SQLException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            C3P0Tools.release(connection, statement, resultSet);
        }

        return null;
    }


    /**
     * 解析对象数据
     * @param resultSet
     * @param object
     */
    public void parseForObject(ResultSet resultSet, Object object, Class<T> clazz) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();

            while(resultSet.next()) {
                setParamType(resultSet, metaData, clazz,object);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析对象集合
     * @param resultSet
     * @param clazz
     * @param list
     */
    public void parseForList(ResultSet resultSet, Class<T> clazz, List<T> list) {

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while(resultSet.next()) {

                // 映射成JavaBean
                T object = clazz.getConstructor(null).newInstance(null);

                //赋值对象
                setParamType(resultSet, metaData, clazz, object);

                list.add(object);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * 替换字段
     * @param statement
     * @param objects
     */
    public void setResultSetParams(PreparedStatement statement, Object... objects) {
        try {
            for (int i = 0; i <objects.length; i++) {
                Object param = objects[i];
                String paramType = param.getClass().getTypeName();

                //System.out.println(paramType);

                switch (paramType) {
                    case "java.lang.Integer":
                        statement.setInt(i + 1, (Integer) param);
                        break;
                    case "java.lang.String":
                        statement.setString(i + 1, (String) param);
                        break;
                    default:
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取sql字段类型并赋值给对象
     * @param resultSet
     * @param metaData
     * @param clazz
     * @param object
     */
    public void setParamType(ResultSet resultSet, ResultSetMetaData metaData, Class<T> clazz, Object object) {
        try {

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                String columnTypeName = metaData.getColumnTypeName(i);
                Object value = null;

                switch (columnTypeName) {
                    case "INT":
                        value = resultSet.getInt(columnName);
                        break;
                    case "VARCHAR":
                        value = resultSet.getString(columnName);
                        break;
                    default:
                };
                //System.out.println(columnName + ": " + value);

                // 获取set方法
                String methodName = "set" + columnName.substring(0,1).toUpperCase() + columnName.substring(1);

                // 获取属性类型
                Field field = clazz.getDeclaredField(columnName);
                Method method = clazz.getMethod(methodName, field.getType());

                method.invoke(object, value);

                //System.out.println(method);
            }



        } catch (SQLException | NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
