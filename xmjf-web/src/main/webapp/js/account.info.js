$(function () {
    loadAccountInfoData(); //资产
    loadInvestInfoData();  //投资
});

//圆形   资产详情   没有传参数  从session中取用户id userId
function  loadAccountInfoData() {
    $.ajax({
        type:"post",
        url:ctx+"/account/accountInfo",
        dataType:"json",
        success:function (data) {
            var data1=data.data1;
            var data2=data.data2;
            if(data1.length>0){
                $('#pie_chart').highcharts({
                    chart: {
                        plotBackgroundColor: null,
                        plotBorderWidth: null,
                        plotShadow: false,
                        spacing : [100, 0 , 40, 0]
                    },
                    title: {
                        floating:true,
                        text: '总资产:'+data2+"￥"
                    },
                    tooltip: {
                        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                    },
                    plotOptions: {
                        pie: {
                            allowPointSelect: true,
                            cursor: 'pointer',
                            dataLabels: {
                                enabled: true,
                                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                                style: {
                                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                                }
                            },
                        }
                    },
                    series: [{
                        type: 'pie',
                        innerSize: '80%',
                        name: '市场份额',
                        data: data1
                    }]
                }, function(c) {
                    // 环形图圆心
                    var centerY = c.series[0].center[1],
                        titleHeight = parseInt(c.title.styles.fontSize);
                    c.setTitle({
                        y:centerY + titleHeight/2
                    });
                    chart = c;
                });
            }
        }
    })
}

//折线    投资记录
function  loadInvestInfoData() {
    $.ajax({
        type:"post",
        url:ctx+"/busItemInvest/queryInvestInfoByUserId",
        dataType:"json",
        success:function(data){
            var data1=data.data1;
            var data2=data.data2;
            if(data1.length>0){
                $("#line_chart").highcharts({
                    chart: {
                        type: 'spline'
                    },
                    title: {
                        text: '用户投资收益折线图'
                    },
                    subtitle: {
                        text: '数据来源: SXT_P2P'
                    },
                    xAxis: {
                        categories: data1
                    },
                    yAxis: {
                        title: {
                            text: '总金额'
                        },
                        labels: {
                            formatter: function () {
                                return this.value + '°';
                            }
                        }
                    },
                    tooltip: {
                        crosshairs: true,
                        shared: true
                    },
                    plotOptions: {
                        spline: {
                            marker: {
                                radius: 4,
                                lineColor: '#666666',
                                lineWidth: 1
                            }
                        }
                    },
                    series: [{
                        name: '投资',
                        marker: {
                            symbol: 'square'
                        },
                        data: data2
                    }]
                })
            }
        }
    })
}




/*  折线图格式
var chart = Highcharts.chart('container', {
    chart: {
        type: 'spline'
    },
    title: {
        text: '两地月平均温度'
    },
    subtitle: {
        text: '数据来源: WorldClimate.com'
    },
    xAxis: {
        categories: ['一月', '二月', '三月', '四月', '五月', '六月',
            '七月', '八月', '九月', '十月', '十一月', '十二月']
    },
    yAxis: {
        title: {
            text: '金额'
        },
        labels: {
        }
    },
    series: [{
        name: '投资金额',
        data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2,
            26.5, 23.3, 18.3, 13.9, 9.6]
    }]
});*/
