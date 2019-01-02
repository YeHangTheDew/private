/*var dom = $("#container");
var myChart = echarts.init(dom);
var app = {};
option = null;
app.title = '极坐标系下的堆叠柱状图';*/
var a=null;
var b=null;
var c=null

$.ajax({
    type: 'POST',
    //url: "http://localhost:8080/auth/login_in",
    //url: "http://localhost:8080/auth/getData",getDataArry
    //url: "http://localhost:8080/auth/getDataArry",
    url: "http://localhost:8080/auth/getDataMap",
    dataType:"json",
    success: function(rep){

            //alert(rep.message);
            console.log(rep.message);
            //传入list，list下是数组
/*        console.log(rep.message);
        a = rep[0];
        b=rep[1];
        c=rep[2];
        console.log(rep);
        alert(a);
        alert(b);
        alert(c);*/
            console.log(rep);
//                    loadPage(0,baseUrl);
    },
    error:function(rep){
        consol.log(rep);
        alert("获取信息失败!");
    }
});

option = {
    angleAxis: {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
        z: 10
    },
    radiusAxis: {
    },
    polar: {
    },
    series: [{
        type: 'bar',
        data: a,
        coordinateSystem: 'polar',
        name: 'A',
        stack: 'a'
    }, {
        type: 'bar',
        data: b,
        coordinateSystem: 'polar',
        name: 'B',
        stack: 'a'
    }, {
        type: 'bar',
        data: c,
        coordinateSystem: 'polar',
        name: 'C',
        stack: 'a'
    }],
    legend: {
        show: true,
        data: ['A', 'B', 'C']
    }
};
;
if (option && typeof option === "object") {
    myChart.setOption(option, true);
}