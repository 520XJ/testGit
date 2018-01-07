/**
 *
 * tempData 是返回的list   中的每一条数据
 */


$(function () {
    /**
     * 页面加载完毕
     *    填充表格数据
     *    填充页码
     */
    loadInvestListData();


    /**
     * 期限查询切换控制
     */
    $(".tab").click(function () {
        /**
         * 1.当前节点选中
         * 2.其他选中节点选中状态取消
         */
        $(this).addClass("list_active");
        $(".tab").not($(this)).removeClass("list_active");
        // 获取点击的期限类别值
        var index=$(this).index();
        var isHistory=0;
        if(index==4){
            isHistory=1;
        }
        loadInvestListData(isHistory,null,index);
    })



});

/**
 * 1.是否为历史项目  isHistory
 * 2.项目类别  itemType
 * 3.项目期限   itemCycle
 * 4.翻页页码  pageNum
 * 5. 每页数据大小  pageSize
 */
function  loadInvestListData(isHistory,itemType,itemCycle,pageNum,pageSize) {
    var params={};
    params.isHistory=0;// 默认查询可投标项目
    if(!isEmpty(isHistory)){
        params.isHistory=isHistory;
    }
    if(!isEmpty(itemType)){
        params.itemType=itemType;
    }
    if(!isEmpty(itemCycle)&&itemCycle!=0){
        params.itemCycle=itemCycle;
    }
    if(!isEmpty(pageNum)){
        params.pageNum=pageNum;
    }
    if(!isEmpty(pageSize)){
        params.pageSize=pageSize;
    }

    /**
     * 发送ajax 请求后台数据
     */
    $.ajax({
        type:"post",
        url:ctx+"/basItem/queryBasItemsByParams",
        data:params,
        dataType:"json",
        success:function (data) {
            var paginator=data.paginator;// 分页信息
            var list=data.list;
            if(list.length>0){
                initTrHtml(list);
                initItemScale();  //初始化 项目投资进度值 圆形进度框
                initPageHtml(paginator);
                initDjs(); //待开放投资项目倒计时
            }else{
                //
                //alert("暂无数据!");
                //显示  暂无数据  图片
                $("#pages").html("<img style='margin-left: -70px;padding:40px;' " +
                    "src='/img/zanwushuju.png'>");
                $("#pcItemList").html("");
            }
        }
    })
}


/**
 * 填充表格行记录数据
 * @param list
 */
function  initTrHtml(list) {
    if(list.length>0){
        var trs="";
        for(var i=0;i<list.length;i++){
            var tempData=list[i];
            trs=trs+"<tr>";
            trs=trs+"<td><strong>"+tempData.itemRate+"</strong><span>%";
            if(!isEmpty(tempData.itemAddRate)){
                trs=trs+"+"+tempData.itemAddRate+"%"
            }
            trs=trs+"</span></td>";
            trs=trs+"<td>"+tempData.itemCycle+"|";
            if(tempData.itemCycleUnit==0||tempData.itemCycleUnit==1){
               trs=trs+"天";
            }
            if(tempData.itemCycleUnit==2){
                trs=trs+"月";
            }
            if(tempData.itemCycleUnit==3){
                trs=trs+"季";
            }
            if(tempData.itemCycleUnit==4){
                trs=trs+"年";
            }
           trs=trs+"</td>";


            trs=trs+"<td>";
            trs=trs+tempData.itemName;
            if(tempData.itemIsnew==1){
              trs=trs+"<strong class='colorful' new>NEW</strong>";
            }
            if(tempData.itemIsnew==0){
                if(tempData.moveVip==1){
                    trs=trs+"<strong class='colorful' app>APP</strong>";
                }
            }
            if(tempData.itemIsnew==0){
                if(tempData.moveVip==0){
                    if(tempData.itemIsrecommend==1){
                        trs=trs+"<strong class='colorful' hot>HOT</strong>";
                    }
                }
            }
            if(tempData.itemIsnew==0){
                if(tempData.moveVip==0){
                    if(tempData.itemIsrecommend==0){
                        if(!isEmpty(tempData.password)){
                            trs=trs+"<strong class='colorful' psw>LOCK</strong>";
                        }
                    }
                }
            }
            trs=trs+"</td>";

            trs=trs+"<td class='trust_range'>";
              if(tempData.total>90){
                 trs=trs+"A+";
              }
            if(tempData.total>85 && tempData.total<=90){
                trs=trs+"A";
            }
            if(tempData.total<=85 && tempData.total>75){
                trs=trs+"A-";
            }

            if(tempData.total<=75 && tempData.total>65){
                trs=trs+"B";
            }
            trs=trs+"</td>";

            trs=trs+"<td class='company'>";
             trs=trs+"<img src='../img/logo.png' >";
            trs=trs+"</td>";

            //判断是否为待开放投资项目
            if(tempData.itemStatus==1){
                trs=trs+"<td>";
                trs=trs+"<strong class='countdown time' data-item='"+tempData.id+"' data-time='"+tempData.syTime+"'>";
                trs=trs+" <time class='hour'></time>&nbsp;:";
                trs=trs+"<time class='min'></time>&nbsp;:";
                trs=trs+" <time class='sec'></time>";
                trs=trs+"</td>";

            }else{
                // 暂时显示进度值  后期优化
                trs=trs+"<td class='itemScale' data-val='"+tempData.itemScale+"'></td>";
            }

            trs=trs+"<td>";
            var status=tempData.itemStatus;

            var href = ctx="/basItem/itemDetailPage?itemId="+tempData.id;

           if(status==1){
                    trs=trs+"<p><a href='' ><input class=‘countdownButton’ valid type=‘button’ value='即将开标'></a></p>";
             }
             //立即投资
             if(status==10||status==13||status==18){
                  trs=trs+"<p class='left_money'>可投金额"+tempData.itemOngoingAccount+"元</p>" +
                      "<p><a href='"+href+"'><input valid type='button' value='立即投资'></a></p>";
             }
            if(status==20){
                trs=trs+ "<p><a href='"+href+"'><input not_valid type='button' value='已抢完'></a></p>";
            }
            if(status==30||status==31){
                trs=trs+ "<p><a href='"+href+"'><input not_valid type='button' value='还款中'></a></p>";
            }
            if(status==32){
                trs=trs+ "<p style=‘position: relative’><a href='"+href+"' class=‘yihuankuan’><input not_valid type='button' value='已还款'></a></p>";
            }
            if(status==23){
                trs=trs+ "<p><a href='"+href+"'><input not_valid type='button' value='已满标'></a></p>";
            }
            trs=trs+"</td>";
            trs=trs+"</tr>";
        }
        $("#pcItemList").html(trs);
    }
}


function  initPageHtml(paginator) {
    var navigatepageNums=paginator.navigatepageNums;
    if(navigatepageNums.length>0){
        var lis="";
        for(var j=0;j<navigatepageNums.length;j++){
            var page=navigatepageNums[j];

            var href="javascript:toPageData("+page+")";
            if(page==paginator.pageNum){
                lis=lis+"<li class='active'><a href='"+href+"' title='第"+page+"页' >"+page+"</a></li>";
            }else{
                lis=lis+"<li ><a href='"+href+"' title='第"+page+"页' >"+page+"</a></li>";
            }
        }
        $("#pages").html(lis);
    }
}



function initItemData(itemType) {
    /**
     * 获取类别
     */
    var itemType=itemType;

    var index=$(".list_active").index();
    var isHistory=0;
    if(index==4){
        isHistory=1;
    }
    loadInvestListData(isHistory,itemType,index);

}


function toPageData(page) {
    var pageNum=page;
    var itemType=$("#itemType").val();
    var index=$(".list_active").index();
    var isHistory=0;
    if(index==4){
        isHistory=1;
    }
    loadInvestListData(isHistory,itemType,index,pageNum);
}

//圆形进度框
function  initItemScale() {
    $('.itemScale').radialIndicator();
    var arrs=$('.itemScale');
    if(arrs.length>0){
        arrs.each(function(){
            var val=$(this).attr("data-val");
            var radialObj=$(this).data('radialIndicator');
            radialObj.option("barColor","orange");
            radialObj.option("percentage",true);
            radialObj.option("barWidth",10);
            radialObj.option("radius",40);
            radialObj.value(val);
        })
    }
}

/**
 * 渲染贷款项目投资进度
 */
function initInvestScale() {
    radialIndicator.defaults.radius=40;
    radialIndicator.defaults.barColor="orange";
    radialIndicator.defaults.barWidth=10;
    radialIndicator.defaults.roundCorner=true;
    radialIndicator.defaults.percentage=true;
    radialIndicator.defaults.fontSize=25;
    $(".itemScale").each(function () {
        var investScale= $(this).attr("data-value");//获取投资进度
        $(this).radialIndicator();
        $(this).data('radialIndicator').value(investScale);
    })
}

//  待开放投资项目   开放剩余时间
function initDjs() {
    $('.countdown').each(function () {
        var syTime = $(this).attr("data-time");//当前数据记录剩余时间
        var itemId = $(this).attr("data-item"); //当前数据记录id
        timer(syTime,$(this),itemId);
    })
}

/**
 * 倒计时  一秒执行一次   定时任务
 * @param intDiff
 * @param obj
 * @param itemId
 */
function timer(intDiff,obj,itemId){
    if( obj.timers){
        clearInterval( obj.timers);
    }

    obj.timers=setInterval(function(){
        var day=0,
            hour=0,
            minute=0,
            second=0;//时间默认值
        if(intDiff > 0){
            day = Math.floor(intDiff / (60 * 60 * 24));
            hour = Math.floor(intDiff / (60 * 60)) - (day * 24);
            minute = Math.floor(intDiff / 60) - (day * 24 * 60) - (hour * 60);
            second = Math.floor(intDiff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
        }

        if (minute <= 9) minute = '0' + minute;
        if (second <= 9) second = '0' + second;
        obj.find('.hour').html(hour);
        obj.find('.min').html(minute);
        obj.find('.sec').html(second);
        intDiff--;
        if(intDiff==-1){
            $.ajax({
                url : ctx+'/basItem/updateBasItemStatusToOpen',
                dataType : 'json',
                type : 'post',
                data:{
                    itemId:itemId
                },
                success : function(data) {
                    if(data.code==200){
                        window.location.reload()
                    }
                },
                error : function(textStatus, errorThrown) {

                }
            });
        }
    }, 1000);
}