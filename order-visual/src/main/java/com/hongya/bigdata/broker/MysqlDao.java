package com.hongya.bigdata.broker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.*;
import java.util.Comparator;

/**
 * Created by dengziming on 12/01/2018.
 * ${Main}
 */
public class MysqlDao {




    public static String executeQuery(String sql){

        JSONArray array = new JSONArray();
        Connection con;
        //驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://localhost:3306/test";
        //MySQL配置时的用户名
        String user = "root";
        //MySQL配置时的密码
        String password = "123456";

        try {

            //加载驱动程序
            Class.forName(driver);
            //1.getConnection()方法，连接MySQL数据库！！
            con = DriverManager.getConnection(url,user,password);
            if(!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            //2.创建statement类对象，用来执行SQL语句！！
            Statement statement = con.createStatement();
            //要执行的SQL语句

            //3.ResultSet类，用来存放获取的结果集！！
            ResultSet rs = statement.executeQuery(sql);



            while(rs.next()){
                //获取stuname这列数据
                String date = rs.getString("date");
                //获取stuid这列数据
                int count = rs.getInt("amouont");
                int same = rs.getInt("same");
                double ratio = same * 1.0 / count;

                JSONObject json = new JSONObject();
                json.put("date",date);
                json.put("count",count);
                json.put("same",same);
                json.put("ratio",ratio);
                array.add(json);
            }
            rs.close();
            con.close();
        } catch(ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally{
            System.out.println("数据库数据成功获取！！");
        }

        array.sort(Comparator.comparing(o -> ((JSONObject) o).getString("date")));
        return array.toJSONString();
    }


}
