/**
 * Created by lp on 2017/12/11.
 */
$(function () {
    $('#rate').radialIndicator();
    var val=$("#rate").attr("data-val");
    var radialObj=$("#rate").data('radialIndicator');
    radialObj.option("barColor","orange");
    radialObj.option("percentage",true);
    radialObj.option("barWidth",10);
    radialObj.option("radius",40);
    radialObj.value(val);


    $('#tabs div').click(function () {
        $(this).addClass('tab_active');
        var show=$('#contents .tab_content').eq($(this).index());
        show.show();
        $('#tabs div').not($(this)).removeClass('tab_active');
        $('#contents .tab_content').not(show).hide();
        if($(this).index()==2){
            /**
             * 获取项目投资记录
             *   ajax 拼接tr
             *    追加tr 到 recordList
             */
           // alert("投资用户列表");
            //loadInvestRecodesList($("#itemId").val());
        }
    });


});


function toRecharge() {
    $.ajax({
        type:"post",
        url:ctx+"/user/userAuthCheck",
        dataType:"json",
        success:function(data){
            if(data.code==200){
                layer.msg("该用户已认证!");
                window.location.href=ctx+"/account/rechargePage";
            }else{
                layer.confirm(data.msg, {
                    btn: ['执行认证','稍后认证'] //按钮
                }, function(){
                        window.location.href=ctx+"/user/auth";
                });
            }
        }
    })
}
function doInvest() {
    var usableAmount = parseFloat($("#ye").attr("data-value")); //可用余额
    var amount = parseFloat($("#usableMoney").val());           //投资金额
    var itemId = parseInt($("#itemId").val());                  //项目id
    //单笔最少投资金额  起投金额
    var sinleMinInvestAmount = parseFloat($("#minInvestMoney").attr("data-value"));
    //单笔最多投资金额
    var sinleMaxInvestAmount = parseFloat($("#maxInvestMoney").attr("data-value"));

    if (usableAmount == 0) {
        layer.tips("可用余额不满足本次投资金额，请先进行充值操作!", "#tz");
        return;
    }

    if (usableAmount < amount) {
        layer.tips("可用余额不满足本次投资金额，请先进行充值操作!", "#tz");
        return;
    }

    if (amount==null||amount.length<=0) {
        layer.tips("请输入投资金额", "#usableMoney");
        return;
    }

    //判断投资金额是否小于单笔最少投资金额
    if (sinleMinInvestAmount > 0) {
        if (amount < sinleMinInvestAmount) {
            layer.tips("投资金额不能小于起投金额", "#usableMoney");
            return;
        }
    }
    //判断投资金额是否大于单笔最多投资金额
    if (sinleMaxInvestAmount > 0) {
        if (amount > sinleMaxInvestAmount) {
            layer.tips("投资金额不能大于单笔最大投标金额", "#usableMoney");
            return;
        }
    }

    //弹出输入交易密码框
    layer.prompt({title: '输入任何口令，并确认', formType: 1}, function (pass, index) {
        layer.close(index);
        var businessPassword = pass;
        //判断
        if (isEmpty(businessPassword)) {
            layer.msg("交易密码不能为空!");
            return;
        }

        $.ajax({
            type: "post",
            url: ctx + "/busItemInvest/userInvest",
            data: {
                itemId: itemId,
                amount: amount,
                businessPassword: businessPassword
            },
            dataType: "json",
            success: function (data) {
                if (data.code == 200) {
                    layer.msg("项目投标成功!");
                    window.location.href=ctx+"/basItem/list";//调到项目详情页
                } else {
                    layer.msg(data.msg);
                    alert(data.msg);
                }
            }
        })
    });
}