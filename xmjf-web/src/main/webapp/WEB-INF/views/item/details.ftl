<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>投资详情</title>
    <link rel="stylesheet" href="/css/reset.css">
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/details.css">
    <link rel="stylesheet" href="/css/page.css">
    <script type="text/javascript" src="${ctx}/js/assets/jquery-3.1.0.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/assets/radialIndicator.js"></script>
    <script type="text/javascript" src="${ctx}/js/common.js"></script>
    <script type="text/javascript" src="${ctx}/js/layer/layer.js"></script>
    <script type="text/javascript" src="${ctx}/js/details.js"></script>
    <script type="text/javascript">
        var ctx="${ctx}";
    </script>

</head>

<body>

<#include "../include/header.ftl">
<div class="invest_container">
    <div class="invest_item">
        <h2 class="item_name">${(item.itemName)!""}
            <#if item.itemIsnew==1>
                <span class="hot" new>NEW</span>
            <#elseif item.itemIsnew==0 && item.moveVip==1>
                   <span class="hot" app>APP</span>
            <#elseif item.itemIsnew==0 && item.moveVip==0 && item.itemIsrecommend ==1>
                <span class="hot" hot>HOT</span>
            <#elseif item.itemIsnew==0 && item.moveVip==0 && item.itemIsrecommend ==0 && item.password??>
                <span class="hot" lock>LOCK</span>
            </#if>
        </h2>
        <div class="invest_detail">
            <div>
                <p class="rate"><strong class="rate_int">${item.itemRate}</strong>
                    <span class="percent">%
                        <#if item.itemAddRate??>
                            +${item.itemAddRate}%
                        <#else>
                            无
                        </#if>

                	</span></p>
                <p class="details_text">预期年化收益率</p>
            </div>
            <div class="circle">
                <p class="cash_num"><span id="itemCycleForCoupon">${item.itemCycle}</span>
                   <#if item.itemCycleUnit == 1>
                     天
                   <#elseif item.itemCycleUnit == 2>
                     月
                   <#elseif item.itemCycleUnit == 3>
                       季
                   <#elseif item.itemCycleUnit == 4>
                       年
                   </#if>
                </p>
                <p class="details_text">项目期限</p>
            </div>
            <div class="total_money">
                <p class="cash_num">${item.itemAccount}元</p>
                <p class="details_text">项目总额</p>
            </div>
            <div class="radialIndicator" id='rate' data-val="${item.itemScale?string("#.##")}">
            </div>


        </div>
        <div class="invest_table">
            <table>
                <tr>
                    <td width="76">还款方式</td>
                    <td width="120">
                        <#if item.itemRepayMethod==1>
                            一次性还款
                        <#elseif  item.receiptMethod==2>
                              等额本息
                        <#elseif  item.receiptMethod==3>
                              先息后本
                        <#else >
                              每日付息
                        </#if>

                    </td>
                    <td width="76">起投金额</td>
                    <td width="154">
                        <em id="minInvestMoney" data-value="${item.itemSingleMinInvestment}">
                            <#if item.itemSingleMinInvestment gt 0 >
                                ${item.itemSingleMinInvestment}元
                            <#else >
                                 无限制
                            </#if>
                        </em>
                    </td>
                    <td width="76">最大投标</td>
                    <td width="77">
                        <em id="maxInvestMoney"  data-value="${item.itemSingleMaxInvestment?c}">
                        <#if item.itemSingleMaxInvestment gt 0 >
                            ${item.itemSingleMaxInvestment}元
                        <#else>
                               无限制
                        </#if>
                        </em>

                    </td>
                </tr>
                <tr>
                    <td>投标奖励</td>
                    <td>
                    	<#if item.itemAddRate??>
                            ${item.itemAddRate}%
                        <#else >
                             无
                        </#if>
                    </td>
                    <td>发布时间</td>
                    <td>
                        <#if item.releaseTime??>
                            ${item.releaseTime?string("yyyy-MM-dd")}
                        </#if>

                    </td>
                    <td>有效期</td>
                    <td>
                        <#if item.endTime??>
                            ${item.endTime?string("yyyy-MM-dd")}
                        </#if>

                    </td>
                </tr>
            </table>
        </div>
        <div class="invest_panel">
            <p class="text">剩余金额：${item.itemAccount-item.itemOngoingAccount}元</p>
             <#if userInfo??>
                 <p class="text left_account"><span id="ye" data-value=${busAccount.usable?c}>账户余额：${busAccount.usable}元</span>
                     <a class="charge" href="javascript:toRecharge()">充值</a>
                 </p>
             </#if>

            <p class="input_wrap left_account"><input type="number" id='usableMoney' placeholder="请输入投资金额"></p>

            <p class="input_wrap clear" style="margin-top:-12px;">
                <#if  userInfo??>
                    <#if item.itemStatus ==1>
                         <a href="javascript:void(0)"><input class='invest_button fl' style="background: #c9c9c9;cursor: default" type="button"  value="即将开放"></a>
                    <#elseif  item.itemStatus==10>
                            <a href="javascript:doInvest()"><input class='invest_button fl' type="button"  id="tz" value="立即投资"></a>
                    <#elseif item.itemStatus==20>
                         <a href="javascript:void(0)"><input class='invest_button fl' style="background: #c9c9c9;cursor: default" type="button"  value="已抢完"></a>
                    <#elseif  item.itemStatus==30 || item.itemStatus==31 >
                         <a href="javascript:void(0)"><input class='invest_button fl' style="background: #c9c9c9;cursor: default" type="button"  value="还款中"></a>
                    <#elseif  item.itemStatus==23 >
                         <a href="javascript:void(0)"><input class='invest_button fl' style="background: #c9c9c9;cursor: default" type="button"  value="已满标"></a>
                    <#else>
                         <a href="javascript:void(0)">
                             <input class='invest_button fl' style="background: #c9c9c9;cursor: default" type="button"  value="已还款">
                         </a>

                    </#if>
                <#else >
                     <a href="/login"><input class='invest_button fl' type="button" value="请登录"></a>
                </#if>

            <#--<span class="caculator fl"></span>-->
            </p>
            <p class="no_account">没有账号？<a href="/register">立即注册</a></p>
        </div>
    </div>
    <div class="item_introduce">
        <div class="tab" id="tabs">
            <div  class="tab_active">借款详情</div>
            <div>风险措施</div>
            <div >投标记录</div>
        </div>
        <div id="contents">
            <div class="tab_content" >
                <h3 class="title">关于车贷宝</h3>
                <div class="type_box">
                    <img src="/img/cheshangbao.png" alt="">
                </div>

                <h3 class="title">基本信息</h3>
                <div class="table_box">
                    <table class="table_base">
                        <thead>
                        <tr><th colspan="2">借款人信息</th><th colspan="2">车辆信息</th></tr>
                        </thead>

                        <tbody>
                        <tr><td>姓&nbsp;&nbsp;&nbsp;&nbsp;名</td><td>${(loanUser.realname)!}</td>
                            <td>车型</td><td>${(busItemLoan.carType)!}</td></tr>
                        <tr>
                            <td>身份证号</td><td>${(loanUser.identifyCard)!}</td><td>上牌时间</td><td>
                      <#if busItemLoan??>
                          <#if busItemLoan.licensingTime??>
                              ${busItemLoan.licensingTime?string("yyyy-MM-dd")}
                          </#if>
                      </#if>
                        </td></tr>
                        <tr><td></td><td></td><td>公里数</td><td>${(busItemLoan.kilometers)!"0"}万公里</td></tr>
                        <tr><td>首付金额</td><td>${(busItemLoan.firstPayAmount)!""}元</td><td>评估价</td><td>${(busItemLoan.assessPrice)!}元</td>
                        </tr>

                        </tbody>
                    <#--
                       type==2
                    <thead>
                    <tr><th colspan="2">借款人信息</th><th colspan="2">保障措施</th></tr>
                    </thead>
                    <tbody>
                    <tr><td>借&nbsp;款&nbsp;方</td><td>${security.realname}</td><td>1</td><td>汽车经销商/汽车零配件加工企业收入</td></tr>
                    <tr><td>注册资金</td><td>${carMall.registerCapital}</td><td>2</td><td>逾期抵（质）押物变现</td></tr>
                    <tr><td>注册地址</td><td>${carMall.address}</td><td>3</td><td>第三方机构担保逾期全额赔付 </td></tr>
                    <tr><td>法人</td><td>${carMall.juridicalPersonName}</td><td>4</td><td>第三方机构贷中监管</td></tr>
                    <tr><td>身份证号</td><td>${carMall.juridicalPersonCard}</td><td>5</td><td>专业团队催收</td></tr>
                    <tr><td>资金用途</td><td>${item.itemLoanUse}</td><td>6</td><td>第三方催收公司逾期债权收购</td></tr>
                    </tbody>-->

                    <#--
                      type==1
                    <thead>
                    <tr><th colspan="2">借款人信息</th><th colspan="2">保障措施</th></tr>
                    </thead>
                    <tbody>
                    <tr><td>姓&nbsp;&nbsp;&nbsp;&nbsp;名</td><td>${trainingInformation.name}</td><td>1</td><td>第三方合作机构担保</td></tr>
                    <tr><td>学&nbsp;&nbsp;&nbsp;&nbsp;校</td><td>${trainingInformation.school}</td><td>2</td><td>借款信息真实、透明、公开</td></tr>
                    <tr><td>年&nbsp;&nbsp;&nbsp;&nbsp;级</td><td>${trainingInformation.grade}</td><td>3</td><td>驾校承担监管责任</td></tr>
                    <tr><td>借款金额</td><td>${item.itemAccount}</td><td>4</td><td>专款专用避免挪用</td></tr>
                    <tr><td>培训驾校</td><td>${trainingInformation.drivingSchool}</td><td>5</td><td>专业团队催收</td></tr>
                    <tr><td>资金用途</td><td>大学生驾校培训费</td><td>6</td><td>担保机构无条件垫付代偿</td></tr>
                    </tbody>-->
                    <#--
                      type==5
                    <thead>
                     <tr><th colspan="2">借款人信息</th><th colspan="2">车辆信息</th></tr>
                     </thead>

                             <tbody>
                             <tr><td>姓&nbsp;&nbsp;&nbsp;&nbsp;名</td><td>${security.realname!''}</td><td>车型</td><td>${itemLoan.carType!''}</td></tr>
                             <tr><td>身份证号</td><td>${security.identifyCard!''}</td><td>上牌时间</td><td><#if itemLoan.licensingTime??>${itemLoan.licensingTime?string("yyyy-MM")}</#if></td></tr>
                             <tr><td>居&nbsp;住&nbsp;地</td><td>${info.currentAddress!''}</td><td>公里数</td><td>${itemLoan.kilometers!'0'}万公里</td></tr>
                             <tr><td>首付金额</td><td><#if itemLoan.firstPayAmount??>${itemLoan.firstPayAmount!''}元</#if></td><td>评估价</td><td><#if itemLoan.assessPrice??>${itemLoan.assessPrice?number}元</#if></td></tr>

                         </tbody>-->
                    <#--
                      其他情况
                    <thead>
                    <tr><th colspan="2">借款人信息</th><th colspan="2">车辆信息</th></tr>
                    </thead>
                  <#if itemLoan??>
                       <#if itemLoan.isNewCar=='0'>
                        <tbody>
                        <tr><td>姓&nbsp;&nbsp;&nbsp;&nbsp;名</td><td>${security.realname!''}</td><td>车型</td><td>${itemLoan.carType!''}</td></tr>
                        <tr><td>身份证号</td><td>${security.identifyCard!''}</td><td>上牌时间</td><td><#if itemLoan.licensingTime??>${itemLoan.licensingTime?string("yyyy-MM")}</#if></td></tr>
                        <tr><td>出&nbsp;生&nbsp;地</td><td>${info.birthAddress!''}</td><td>公里数</td><td>${itemLoan.kilometers!'0'}万公里</td></tr>
                        <tr><td>居&nbsp;住&nbsp;地</td><td>${info.currentAddress!''}</td><td>评估价</td><td><#if itemLoan.assessPrice??>${itemLoan.assessPrice?number}元</#if></td></tr>
                        </tbody>
                        <#else>
                            <tbody>
                            <tr><td>姓&nbsp;&nbsp;&nbsp;&nbsp;名</td><td>${security.realname!''}</td><td>品牌</td><td>${itemLoan.carBrand!''}</td></tr>
                            <tr><td>身份证号</td><td>${security.identifyCard!''}</td><td>车型</td><td>${itemLoan.carType!''}</td></tr>
                            <tr><td>出&nbsp;生&nbsp;地</td><td>${info.birthAddress!''}</td><td>购买价格</td><td>${itemLoan.buyPrice!''}</td></tr>
                            </tbody>
                        </#if>-->
                    </table>
                </div>
                <h3 class="title" id="anquanshenheType">安全审核</h3>
                <ul class="security_check clear">
                    <if pics??>
                  <#list pics as pic>
                        <#if pic.itemPictureType==1>
                            <li style="background-image: url(/img/shenfenzheng.png)">身份证</li>
                        <#elseif pic.itemPictureType==2>
                              <li style="background-image: url(/img/xue.png)">学生证</li>
                        </#if>
                  </#list>

                    </if>

                <#--  <li style="background-image: url(/img/cheng.png)">车城外观</li>
                  <li style="background-image: url(/img/cheng.png)">车城外观</li>-->


                </ul>

                <h3 class="title">相关文件</h3>
                <div class="lunbo_wrap">
                    <button class="click_button pre" id="pre" onclick="play()" >
                        <
                    </button>
                    <button class="click_button next" id="next"  onclick="play()" >
                        >
                    </button>
                    <div class="image_large" id="imgLarge">
                        <div class="left"></div>
                        <div class="right"></div>
                        <div class="close"></div>
                    </div>
                    <div class="over_hidden">
                        <ul class="lunbo" id="slider">
                        <#if pics??>
                            <#list pics as pic>
                                <#--<li style="background-image: url(${pic.picturePath})" onclick="picTab()" data-url="${pic.picturePath}" ></li>-->
                            </#list>
                        </#if>

                        </ul>
                    </div>
                </div>
            </div>
            <div class="tab_content" style="display: none">
                <div class="all_security_img">
                    <img src="/img/fengxianbaozhang.png" alt="">
                </div>
                <div class="security_bottom">
                    <ul class="clear">
                        <li style="background-image: url(/img/gongsi.png)">项目公司
                            本息担保</li>
                        <li style="background-image: url(/img/disanfang.png)">三方保证
                            无限连带</li>
                        <li style="background-image: url(/img/zichang.png)">安保资产
                            清收保障</li>
                        <li style="background-image: url(/img/gongkaitouming.png)">实地信审
                            公开透明</li>
                    </ul>
                </div>
                <div class="special">
                    <p class="shenming">特别申明：</p>
                    <p class="text">1、汇诚金服及其合作机构将始终秉持客观公正的原则，严控风险，最大程度的尽力确保借款人信息的真实性。同时由与第三方征信机构合作动态数据对接，筛查借款人信息。</p>
                    <p class="text">2、借款人若逾期，其个人信息将被公布。</p>
                </div>
            </div>
            <div class="tab_content" style="display: none">
                <table class="record">
                    <thead>
                    <tr><th width="400">投资用户</th><th>投资金额（元）</th><th width="400">投资时间</th></tr>
                    </thead>
                    <tbody id="recordList">
                    <tr>
                        <td>213213</td>
                        <td>21321</td>
                        <td>123</td>
                    </tr>
                    <tr>
                        <td>213213</td>
                        <td>21321</td>
                        <td>123</td>
                    </tr>
                    <tr>
                        <td>213213</td>
                        <td>21321</td>
                        <td>123</td>
                    </tr>
                    </tbody>

                </table>
                <div class="pages">
                    <nav>
                        <ul id="pages" style="margin:84px auto" class="pagination">
                            <li class="active"><a>1</a></li>
                            <li ><a>2</a></li>
                            <li ><a>3</a></li>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
    </div>
</div>





<input type="hidden" value="${item.id?c}" id="itemId"/>
</body>
</html>