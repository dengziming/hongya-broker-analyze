
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<html>
<head>

    <meta charset="utf-8">
    <title>broker</title>
    <script src="js/echarts.js"></script>
    <script src="js/jquery-3.2.1.js"></script>

</head>
<body>

<h2>Hello World!</h2>

<div id="main" style="width:80%;height:400px;"></div>
<script type="text/javascript">

    var myChart = echarts.init(document.getElementById('main'));

    var data = [];
    var data1 = [];
    var data2 = [];

        function getData() {

            $.ajax({
                type : "get",
                async : true,            //异步请求（同步请求将会锁住浏览器，用户其他操作必须等待请求完成才可以执行）
                url : "GetFromMysqlServlet",    //请求发送到GetFromMysqlServlet处
                data : {},
                dataType : "json",        //返回数据形式为json
                success : function(result) {

                    //请求成功时执行该函数内容，result即为服务器返回的json对象
                    for(var i = 0; i < result.length; i ++) {

                        var row = {
                            name: result[i].date,
                            value: [
                                result[i].date,
                                result[i].count
                            ]
                        };

                        data.push(row);    //挨个取出类别并填入类别数组

                        var row1 = {
                            name: result[i].date,
                            value: [
                                result[i].date,
                                result[i].same
                            ]
                        };

                        data.push(row1);

                        var row2 = {
                            name: result[i].date,
                            value: [
                                result[i].date,
                                result[i].ratio
                            ]
                        };

                        data2.push(row2);    //挨个取出类别并填入类别数组
                    }

                },
                error : function(errorMsg) {
                    //请求失败时执行该函数
                    alert("图表请求数据失败!");
                    myChart.hideLoading();
                }
            });

            return [data,data1,data2];
        }


        option = {
            title: {
                text: '动态数据 + 时间坐标轴'
            },
            tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                    params = params[0];
                    var date = new Date(params.name);
                    return params.name + ' : ' + params.value[1];
                },
                axisPointer: {
                    animation: false
                }
            },
            xAxis: {
                type: 'time',
                splitLine: {
                    show: false
                }
            },
            yAxis: {
                type: 'value',
                boundaryGap: [0, '100%'],
                splitLine: {
                    show: false
                }
            },
            series: [{
                name: 'count',
                type: 'line',
                showSymbol: false,
                hoverAnimation: false,
                data: data
            },
                {
                    name: 'same',
                    type: 'line',
                    showSymbol: false,
                    hoverAnimation: false,
                    data: data2
                },
                {
                    name: 'ratio',
                    type: 'line',
                    showSymbol: false,
                    hoverAnimation: false,
                    data: data2
                }]
        };

        myChart.setOption(option)
        setInterval(function () {

            var x = getData();
            data = x[0];
            data1=x[1];
            data2=x[2];


            myChart.setOption({
                series: [{
                    data: data
                },{
                    data:data1
                },{
                    data:data2
                }]
            });
        }, 30000);
    </script>


</body>
</html>
