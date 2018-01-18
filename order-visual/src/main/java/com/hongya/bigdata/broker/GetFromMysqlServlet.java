package com.hongya.bigdata.broker;

import java.io.IOException;

/**
 * Created by  on 11/01/2018.
 * ${Main}
 */
public class GetFromMysqlServlet extends javax.servlet.http.HttpServlet {

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        doGet(request,response);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        System.out.println("GetFromMysqlServlet " + request.getParameterNames());
/*
        JSONArray arr = new JSONArray();


        JSONObject o1 = new JSONObject();
        o1.put("date","2018-01-01 12:12:14");
        o1.put("count",1);

        JSONObject o2 = new JSONObject();
        o2.put("date","2018-01-01 12:12:18");
        o2.put("count",2);

        JSONObject o3 = new JSONObject();
        o3.put("date","2018-01-01 12:12:30");
        o3.put("count",4);

        arr.add(o1);
        arr.add(o2);
        arr.add(o3);

        String json = arr.toJSONString();
*/
        String rule = request.getParameter("rule");

        String sql = "select\n" +
                "    substring(date,1,16) as date,\n" +
                "    count(1) as amouont,\n" +
                "    sum(same) as same,\n" +
                "    sum(same)*100.0/count(1) as ratio\n" +
                "from\n" +
                "    (\n" +
                "    select \n" +
                "        a.userid,a.orderid,a.date,\n" +
                "        if (a.rule001=b.rule001,1,0) as same\n" +
                "    from \n" +
                "    \n" +
                "        (\n" +
                "        select \n" +
                "            userid,orderid,date,rule001, rule002, rule003,rule004 ,rule005 ,rule006 ,rule007 ,rule008 ,rule009 ,rule010\n" +
                "        from \n" +
                "            t_audit_result\n" +
                "        where code='人工'\n" +
                "        ) a\n" +
                "    left join\n" +
                "        (\n" +
                "        select \n" +
                "            userid,orderid,date,rule001, rule002, rule003,rule004 ,rule005 ,rule006 ,rule007 ,rule008 ,rule009 ,rule010\n" +
                "        from \n" +
                "            t_audit_result\n" +
                "        where code='机器'\n" +
                "        ) b\n" +
                "    on \n" +
                "        a.userid=b.userid\n" +
                "    and\n" +
                "        a.orderid=b.orderid\n" +
                "    ) t\n" +
                "group by substring(date,1,16)";

        if (rule != null && rule.length() != 0){
            sql = sql.replaceAll("rule001",rule);
        }
        String json = MysqlDao.executeQuery(sql);


        //将json数据返回给客户端
        response.setContentType("text/html; charset=utf-8");

        response.getWriter().write(json);
    }
}
