/**
 * Created by lp on 2017/12/12.
 */

$(function () {
    $("#identityNext").click(function () {
        var realName=$("#realName").val();
        var idCard=$("#card").val();
        var businessPassword=$("#_ocx_password").val();
        var confirmPassword=$("#_ocx_password1").val();
        if(isEmpty(realName)){
            layer.tips("真实名称不能为空!","#realName");
            return;
        }
        if(isEmpty(idCard)){
            layer.tips("身份证号不能为空!","#card");
            return;
        }
        if(idCard.length!=18||idCard.length>18){
            layer.tips("身份证号不合法!","#card");
            return;
        }
        if(isEmpty(businessPassword)||isEmpty(confirmPassword)||(businessPassword!=confirmPassword)){
            layer.tips("密码非法!","#_ocx_password1");
            return;
        }

        if(businessPassword.length<6||confirmPassword.length<6){
            layer.tips("密码至少6位数字!","#_ocx_password1");
            return;
        }

        if(businessPassword!=confirmPassword){
            layer.tips("密码不一致!","#_ocx_password1");
            return;
        }

        $.ajax({
            type:"post",
            url:ctx+"/user/userAuth",
            data:{
                realName:realName,
                idCard:idCard,
                businessPassword:businessPassword,
                confirmPassword:confirmPassword
            },
            dataType:"json",
            success:function (data) {
                if(data.code==200){
                    //window.location.href=ctx+"/user/"
                    alert("认证通过");
                    layer.msg(data.msg);
                    window.location.href=ctx+"/user/index";
                }else{
                    layer.msg(data.msg);
                    return;
                }
            }
        })



    })
});
