
//立即执行
$(function () {
    loadRechargeRecodesData();
})

function loadRechargeRecodesData() {

    //充值记录带分页的    前台传回2个属性
    $.ajax({
        type:"post",
        url:ctx+"/account/queryRechargeRecodesByUserId",
        dataType:"json",
        success:function (data) {
            var paginator = data.paginator;//分页信息
            var list = data.list;  //数据
            if(list.length<1){
                //alert("无充值记录")
                //显示暂无数据
                $("#pages").html("<img style='margin-left: -70px;padding:40px;' " +
                    "src='/img/zanwushuju.png'>");
            }else {
                initDivsHtml(list);
            }
        }
    })
}


function initDivsHtml(list) {
    if(list.length>0){
        var divs = "";
        for (var i=0;i<list.length;i++){
            var tempData=list[i];
            divs=divs+"<div class='table-content-first'>";
            divs=divs+tempData.auditTime+"</div>";
            divs=divs+"<div class='table-content-center'>";
            divs=divs+tempData.actualAmount+"元"+"</div>";
            divs=divs+"<div class='table-content-first'>";
            var status=tempData.status;
            switch(status) {
                case 0:
                    divs=divs+"支付失败";
                    break;
                case 1:
                    divs=divs+"已支付"
                    break;
                case 2:
                    divs=divs+"待支付";
                    break;
            }
            divs=divs+"</div>";
        }
        $("#rechargeList").html(divs);
    }
}