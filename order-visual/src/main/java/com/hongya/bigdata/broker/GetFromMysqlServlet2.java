package com.hongya.bigdata.broker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

/**
 * Created by  on 11/01/2018.
 * ${Main}
 */
public class GetFromMysqlServlet2 extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        doGet(request,response);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        JSONArray arr = new JSONArray();

        //这里把“类别名称”和“销量”作为两个属性封装在一个Product类里，每个Product类的对象都可以看作是一个类别（X轴坐标值）与销量（Y轴坐标值）的集合

        JSONObject o1 = new JSONObject();
        o1.put("date","2018-01-01");
        o1.put("count",1);

        JSONObject o2 = new JSONObject();
        o2.put("date","2018-01-01");
        o2.put("count",2);

        JSONObject o3 = new JSONObject();
        o3.put("date","2018-01-01");
        o3.put("count",4);

        arr.add(o1);
        arr.add(o2);
        arr.add(o3);

        String json = arr.toJSONString();    //将list中的对象转换为Json格式的数组

//System.out.println(json);

        //将json数据返回给客户端
        response.setContentType("text/html; charset=utf-8");
        response.getWriter().write(json);
    }
}
