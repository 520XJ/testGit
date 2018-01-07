<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<form id='paysubmit' name='paysubmit'
      action='${pay.gatewayNew}'
      accept-charset='utf-8' method='POST'>
    <!--  订单描述 -->
    <input type="hidden" name='body' value='${pay.body}'/>
    <!--  服务器异步通知页面路径 -->
    <input type="hidden" name='notify_url' value='${pay.notifyUrl}'/>
    <!--  商户订单号 -->
    <input type="hidden" name='out_order_no' value='${pay.orderNo}'/>
    <!--  合作身份者PID，签约账号，由16位纯数字组成的字符串 -->
    <input type="hidden" name='partner' value='${pay.partner}'/>
    <!--  页面跳转同步通知页面路径 -->
    <input type="hidden" name='return_url' value='${pay.returnUrl}'/>
    <!--  订单名称 -->
    <input type="hidden" name='subject' value='${pay.subject}'/>
    <!--  付款金额 -->
    <input type="hidden" name='total_fee' value='${pay.totalFee}'/>
    <!--  商户号（6位数字） -->
    <input type="hidden" name='user_seller' value='${pay.userSeller}'/>
    <!--  签名值 -->
    <input type="hidden" name='sign' value='${pay.sign}'/>
    <input type='submit' value='支付进行中' style="display: none;" />
</form>

<script type="text/javascript">
    document.forms['paysubmit'].submit();
</script>

</body>
</html>