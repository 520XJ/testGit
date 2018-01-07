/**
 * Created by lp on 2017/12/9.
 */
$(function () {
    // 给图片标签绑定点击事件
    $(".validImg").click(function () {
        swithPicCode();
    });

    function  swithPicCode() {
        $(".validImg").attr("src",ctx+"/img/getPictureVerifyImage?time="+new Date());
    }

    /**
     * 获取短信验证码
     */
    $("#clickMes").click(function () {
        var phone=$("#phone").val();
        var code=$("#code").val();
        if(isEmpty(phone)){
            layer.tips("手机号不能为空!","#phone");
            return;
        }
        if(isEmpty(code)){
            layer.tips("验证码不能为空!",".validImg");
            return;
        }
        var  _this=$(this);
        /**
         * 发送ajax 请求手机短信发送接口
         */
        $.ajax({
            type:"post",
            url:ctx+"/sms/sendPhoneSms",
            data:{
                phone:phone,
                picCode:code,
                type:2
            },
            dataType:"json",
            success:function(data){
                if(data.code==200){
                    time(_this);
                }else{
                    layer.tips(data.msg,"#clickMes");
                }
            }
        })
    });


    var wait=6;
    function time(o) {
        if (wait == 0) {
            o.removeAttr("disabled");
            o.val('获取验证码');
            o.css("color", '#ffffff');
            o.css("background","#fcb22f");
            wait = 6;
        } else {
            o.attr("disabled", true);
            o.css("color", '#fff');
            o.css("background", '#ddd');
            o.val("重新发送(" + wait + "s)");
            wait--;
            setTimeout(function() {
                time(o)
            }, 1000)
        }
    }


    $("#login").click(function () {
        var phone=$("#phone").val();
        var picCode=$("#code").val();
        if(isEmpty(phone)){
            layer.tips("手机号不能为空!","#phone");
            return;
        }
        if(isEmpty(picCode)){
            layer.tips("验证码不能为空!",".validImg");
            swithPicCode();
            return;
        }
        var code=$("#verification").val();
        if(isEmpty(code)){
            layer.tips("手机验证码不能为空!","#verification");
            return;
        }

        var params={};
        params.phone=phone;
        params.picCode=picCode;
        params.code=code;
        $.ajax({
            type:"post",
            url:ctx+"/user/quickLogin",
            data:params,
            dataType:"json",
            success:function (data) {
                if(data.code==200){
                    window.location.href=ctx+"/index";
                }else{
                    layer.tips(data.msg,"#login");
                }
            }
        })


    })

})
