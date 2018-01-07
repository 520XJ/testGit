package com.shsxt.xm.api.utils;



import com.shsxt.xm.api.constant.CycleUnit;
import com.shsxt.xm.api.po.BasItem;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 *   利息计算
 */
public class Calculator {
    public static BigDecimal ONE_PRECENT= BigDecimal.valueOf(0.01);
    public static List<RepayPlan> getPlan(BigDecimal account,BasItem from) {
        BasItem item = new BasItem();
        BeanUtils.copyProperties(from,item);
        item.setItemAccount(account);
        int repayMethod=item.getItemRepayMethod();
        BigDecimal rate=item.getItemRate();
        if(item.getItemAddRate()!=null&&item.getItemAddRate().compareTo(BigDecimal.ZERO)>0){
            rate=rate.add(item.getItemAddRate());
        }
        rate=rate.multiply(ONE_PRECENT);
        int cycle=item.getItemCycle();
        List<RepayPlan> planList = new ArrayList<RepayPlan>();
        //一次性还款
        if (repayMethod == 1) {
            RepayPlan p = new RepayPlan();
            p.setPeriod(1);
            p.setPrincipal(account);
            BigDecimal interest = account.multiply(rate)
                    .multiply(BigDecimal.valueOf(cycle))
                    .divide(BigDecimal.valueOf(12),2);
            Date startTime= DateUtils.setDayLast(new Date());
            Date repayTime=startTime;
            if(item.getItemCycleUnit()== CycleUnit.DAY) {
                interest = interest.divide(BigDecimal.valueOf(30),2);
            }
            repayTime= DateUtils.addTime(item.getItemCycleUnit(), startTime, cycle);
            p.setInterest(interest);
            p.setAccount(interest.add(account));
            p.setEndTime(repayTime);
            planList.add(p);
        } else if (repayMethod == 2) {
            //等额本息
            //每月还款额，本息
            BigDecimal repayPrincipal=account;
            rate = rate.divide(new BigDecimal(12),2);
            BigDecimal monthAccount=account.multiply(
                    rate.multiply(rate.add(BigDecimal.ONE).pow(cycle))
                            .divide((rate.add(BigDecimal.ONE)
                                    .pow(cycle).subtract(BigDecimal.ONE)),2));
            monthAccount=monthAccount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            BigDecimal totalInerest=monthAccount.multiply(BigDecimal.valueOf(cycle)).subtract(account);
            BigDecimal minInrest= BigDecimal.valueOf(0.01 * item.getItemCycle());
            System.out.println("totalAccount:"+monthAccount.multiply(BigDecimal.valueOf(cycle)));
            System.out.println("totalInerest:"+totalInerest);
            //如果总利息不够每期平均的话
            if(totalInerest.compareTo(minInrest)<1){
                for(int i=0;i<cycle;i++){
                    RepayPlan p = new RepayPlan();
                    p.setPeriod(i + 1);
                    Date startTime= DateUtils.setDayLast(new Date());
                    Date repayTime= DateUtils.addMonths(startTime, 1);
                    p.setEndTime(repayTime);
                    p.setAccount(monthAccount);
                    if(totalInerest.compareTo(BigDecimal.ZERO)>0){
                        p.setInterest(ONE_PRECENT);
                        p.setPrincipal(monthAccount.subtract(ONE_PRECENT));
                        totalInerest=totalInerest.subtract(ONE_PRECENT);
                    }else{
                        p.setInterest(BigDecimal.ZERO);
                        p.setPrincipal(monthAccount);
                    }
                    planList.add(p);
                }
            }else{
                // monthAccount
                BigDecimal acutalTotalPrincipal= BigDecimal.ZERO;
                BigDecimal acutalTotalIntrest= BigDecimal.ZERO;
                for(int i=0;i<cycle;i++){
                    BigDecimal interest=repayPrincipal.multiply(rate);
                    interest= interest.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal monthPrincipal=monthAccount.subtract(interest);
                    repayPrincipal=repayPrincipal.subtract(monthPrincipal);

                    RepayPlan p = new RepayPlan();
                    p.setPeriod(i+1);
                    Date startTime= DateUtils.setDayLast(new Date());
                    Date repayTime= DateUtils.addTime(item.getItemCycleUnit(), startTime, cycle);
                    p.setEndTime(repayTime);
                    p.setAccount(monthAccount);
                    //最后一期采用减法
                    if(i<cycle-1){
                        p.setPrincipal(monthPrincipal);
                        p.setInterest(interest);
                    }else{
                        BigDecimal lastPrincipal=item.getItemAccount().subtract(acutalTotalPrincipal);
                        BigDecimal lastInterest=p.getAccount().subtract(lastPrincipal);
                        p.setPrincipal(lastPrincipal);
                        p.setInterest(lastInterest);
                    }
                    planList.add(p);
                    acutalTotalPrincipal=acutalTotalPrincipal.add(p.getPrincipal());
                }
            }
        } else if (repayMethod == 3) {
            //每月还息到期还本
//            if(item.getItemCycleUnit()==1){//天
//                //天收益
//                BigDecimal interest = account.multiply(rate)
//                        .multiply(BigDecimal.valueOf(cycle))
//                        .divide(BigDecimal.valueOf(360),2);//360天
//                Date yesterdayDate=DateUtils.addDays(new Date(), -1);//昨天日期
//                Integer number=0;
//
//
//            }

            BigDecimal interest = account.multiply(rate)
                    .multiply(BigDecimal.valueOf(cycle))
                    .divide(BigDecimal.valueOf(12),2);
            BigDecimal monthlyInterest = interest.divide(BigDecimal.valueOf(cycle),2);
            for (int i = 0; i < cycle; i++) {
                RepayPlan p = new RepayPlan();
                p.setPeriod(i + 1);
                if (i == cycle - 1) {
                    p.setAccount(account.add(monthlyInterest));
                    p.setPrincipal(account);
                } else {
                    p.setAccount(BigDecimal.ZERO.add(monthlyInterest));
                    p.setPrincipal(BigDecimal.ZERO);
                }
                p.setInterest(monthlyInterest);
                planList.add(p);
                Date startTime= DateUtils.setDayLast(new Date());
                Date repayTime= DateUtils.addTime(item.getItemCycleUnit(), startTime, cycle);
                p.setEndTime(repayTime);
            }
        } else if (repayMethod == 4) {
            //先息后本
        }else{
            //不识别的还款方式
        }
        return  planList;
    }

    /**
     * 处理存在负数的情况
     * @param planList
     */
    private static void  handlerInterestNegate(List<RepayPlan> planList){
        RepayPlan lastPlan=planList.get(planList.size() - 1);
        //最后一起做减法，但是前几期都是通过公式计算出来的,有可能是负数
        if(lastPlan.getInterest().compareTo(BigDecimal.ZERO)<0){
            BigDecimal negate=lastPlan.getInterest().negate(); //获取负数的正值
            while(negate.compareTo(BigDecimal.ZERO)>0){
                for(int i=0;i<planList.size()-1;i++){ //对 前n-1位的数据进行调整
                    RepayPlan p=planList.get(i);
                    if(negate.compareTo(BigDecimal.ZERO)>0){
                        if(p.getInterest().compareTo(ONE_PRECENT)>=0){
                            p.setInterest(p.getInterest().subtract(ONE_PRECENT));
                            negate=negate.subtract(ONE_PRECENT);
                        }
                    }else{
                        break;
                    }
                }
            }
            lastPlan.setInterest(BigDecimal.ZERO);
            for(int i=0;i<planList.size();i++){
                RepayPlan plan=planList.get(i);
                plan.setPrincipal(plan.getAccount().subtract(plan.getInterest()));
            }
        }

    }


    public static BigDecimal getInterest(BigDecimal account,BasItem item){
        BigDecimal total= BigDecimal.ZERO;
        List<RepayPlan> planList= getPlanTime(account, item);
        for(RepayPlan plan:planList){
            total=total.add(plan.getInterest());
        }
        return  total;
    }

    public static BigDecimal addRateInterest(BigDecimal account,BasItem item){
        BigDecimal total=(item.getItemRate().add(
                item.getItemAddRate()).multiply(account).multiply(
                new BigDecimal(item.getItemCycle())).divide(new BigDecimal(36000), 2, BigDecimal.ROUND_HALF_UP));
        return  total;
    }



    public static List<RepayPlan> getPlanTime(BigDecimal account,BasItem from) {
        BasItem item = new BasItem();
        BeanUtils.copyProperties(from,item);
        item.setItemAccount(account);  //借款金额
        int repayMethod=item.getItemRepayMethod(); //还款方式 1-一次性还款 2-等额本息 3-先息后本 4-每日付息',
        BigDecimal rate=item.getItemRate();  //项目利率 单位为%，100即100%

        //item.getItemAddRate() 平台增加的年化收益
        //判断如果存在  平台增加的年化收益 将其添加到  rate  项目利率中
        if(item.getItemAddRate()!=null&&item.getItemAddRate().compareTo(BigDecimal.ZERO)>0){
            rate=rate.add(item.getItemAddRate());
        }
        //乘以0.01 变为小数
        rate=rate.multiply(ONE_PRECENT);
        int cycle=item.getItemCycle();  //结款周期
        List<RepayPlan> planList = new ArrayList<RepayPlan>();
        //判断还款方式
        if (repayMethod == 1) {  //一次性还款
            RepayPlan p = new RepayPlan();
            p.setPeriod(1);
            p.setPrincipal(account);
            //multiply（）  BigDecimal的乘法    divide   BigDecimal的除法
            //  投资金额account X 年利率rate X 结款周期cycle /12个月    后面的2 时精度 小数点后2位
            //divide(BigDecimal.valueOf(12),2);
            //计算个月的利息多少
            BigDecimal interest = account.multiply(rate)
                    .multiply(BigDecimal.valueOf(cycle))
                    .divide(BigDecimal.valueOf(12),2);
            // DateUtils.setDayLast(new Date()) 打印 Sat Dec 16 23:59:00 CST 2017
            Date startTime=DateUtils.setDayLast(new Date());
            Date repayTime=startTime; //还款时间

            //判断item.getItemCycleUnit() 周期单位 1-天、2-月、3-季、4-年 是否等于 1  也就是天
            /**
             * ROUND_HALF_EVEN    银行家舍入法

             向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则向相邻的偶数舍入。

             如果舍弃部分左边的数字为奇数，则舍入行为与 ROUND_HALF_UP 相同;

             如果为偶数，则舍入行为与 ROUND_HALF_DOWN 相同。

             注意，在重复进行一系列计算时，此舍入模式可以将累加错误减到最小。

             此舍入模式也称为“银行家舍入法”，主要在美国使用。四舍六入，五分两种情况。

             如果前一位为奇数，则入位，否则舍去。

             以下例子为保留小数点1位，那么这种舍入方式下的结果。

             1.15>1.2 1.25>1.2
             */
            //还款周期为天   计算每天利息多少
            if(item.getItemCycleUnit()== CycleUnit.DAY) {
                interest = interest.divide(BigDecimal.valueOf(30),2, BigDecimal.ROUND_HALF_EVEN);
            }
            repayTime= DateUtils.addTime(item.getItemCycleUnit(), startTime, cycle);
            p.setInterest(interest);
            p.setAccount(interest.add(account));
            p.setEndTime(repayTime);
            planList.add(p);
        } else if (repayMethod == 2) {  //等额本息
            //等额本息
            //每月还款额，本息
            cycle=cycle/30;
            //BigDecimal repayPrincipal=account;
            double monthRate = rate.doubleValue()/12;
            Date startTime=DateUtils.setDayLast(new Date());

            BigDecimal monthIncome = account.multiply(new BigDecimal(monthRate * Math.pow(1 + monthRate, cycle)))
                    .divide(new BigDecimal(Math.pow(1 + monthRate, cycle) - 1), 2, BigDecimal.ROUND_DOWN);
            BigDecimal reaminPrincipal = account;
            for (int i = 1; i < cycle + 1; i++) {
                if(i < cycle){
                    RepayPlan repayPlan  = new RepayPlan();
                    BigDecimal multiply = account.multiply(new BigDecimal(monthRate));
                    BigDecimal sub  = new BigDecimal(Math.pow(1 + monthRate, cycle)).subtract(new BigDecimal(Math.pow(1 + monthRate, i-1)));
                    BigDecimal monthInterest = multiply.multiply(sub).divide(new BigDecimal(Math.pow(1 + monthRate, cycle) - 1), 6, BigDecimal.ROUND_DOWN);
                    monthInterest = monthInterest.setScale(2, BigDecimal.ROUND_DOWN);
                    Date repayTime= DateUtils.addDays(startTime, i*30);
                    BigDecimal monthPrincipal = monthIncome.subtract(monthInterest);
                    repayPlan.setPeriod(i);
                    repayPlan.setPrincipal(monthPrincipal);
                    repayPlan.setAccount(monthIncome);
                    repayPlan.setInterest(monthInterest);
                    repayPlan.setEndTime(repayTime);
                    planList.add(repayPlan);
                    reaminPrincipal = reaminPrincipal.subtract(monthPrincipal);
                }else {
                    //最后一期采用减法
                    RepayPlan repayPlan  = new RepayPlan();
                    BigDecimal monthInterest = monthIncome.subtract(reaminPrincipal);
                    Date repayTime= DateUtils.addDays(startTime, i*30);
                    repayPlan.setPeriod(i);
                    repayPlan.setPrincipal(reaminPrincipal);
                    repayPlan.setAccount(monthIncome);
                    repayPlan.setInterest(monthInterest);
                    repayPlan.setEndTime(repayTime);
                    planList.add(repayPlan);
                }



            }
            /*rate = rate.divide(new BigDecimal(360),2).multiply(new BigDecimal(30));
            BigDecimal monthAccount=account.multiply(
                    rate.multiply(rate.add(BigDecimal.ONE).pow(cycle))
                            .divide((rate.add(BigDecimal.ONE)
                                    .pow(cycle).subtract(BigDecimal.ONE)),2));
            monthAccount=monthAccount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            BigDecimal totalInerest=monthAccount.multiply(BigDecimal.valueOf(cycle)).subtract(account);
            BigDecimal minInrest= BigDecimal.valueOf(0.01 * item.getItemCycle());
            System.out.println("totalAccount:"+monthAccount.multiply(BigDecimal.valueOf(cycle)));
            System.out.println("totalInerest:"+totalInerest);
            //如果总利息不够每期平均的话
            if(totalInerest.compareTo(minInrest)<1){
                for(int i=0;i<cycle;i++){
                    RepayPlan p = new RepayPlan();
                    p.setPeriod(i + 1);
                    Date startTime= DateUtils.setDayLast(new Date());
                    Date repayTime= DateUtils.addDays(startTime, 30);
                    p.setEndTime(repayTime);
                    p.setAccount(monthAccount);
                    if(totalInerest.compareTo(BigDecimal.ZERO)>0){
                        p.setInterest(ONE_PRECENT);
                        p.setPrincipal(monthAccount.subtract(ONE_PRECENT));
                        totalInerest=totalInerest.subtract(ONE_PRECENT);
                    }else{
                        p.setInterest(BigDecimal.ZERO);
                        p.setPrincipal(monthAccount);
                    }
                    planList.add(p);
                }
            }else{
                // monthAccount
                BigDecimal acutalTotalPrincipal= BigDecimal.ZERO;
                BigDecimal acutalTotalIntrest= BigDecimal.ZERO;
                for(int i=0;i<cycle;i++){
                    BigDecimal interest=repayPrincipal.multiply(rate);
                    interest= interest.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal monthPrincipal=monthAccount.subtract(interest);
                    repayPrincipal=repayPrincipal.subtract(monthPrincipal);

                    RepayPlan p = new RepayPlan();
                    p.setPeriod(i+1);
                    Date startTime= DateUtils.setDayLast(new Date());
                    Date repayTime= DateUtils.addDays(startTime, i*30);
                    p.setEndTime(repayTime);
                    p.setAccount(monthAccount);
                    //最后一期采用减法
                    if(i<cycle-1){
                        p.setPrincipal(monthPrincipal);
                        p.setInterest(interest);
                    }else{
                        BigDecimal lastPrincipal=item.getItemAccount().subtract(acutalTotalPrincipal);
                        BigDecimal lastInterest=p.getAccount().subtract(lastPrincipal);
                        p.setPrincipal(lastPrincipal);
                        p.setInterest(lastInterest);
                    }
                    planList.add(p);
                    acutalTotalPrincipal=acutalTotalPrincipal.add(p.getPrincipal());
                }
            }*/
        } else if (repayMethod == 3) {  //先息后本
            //每月还息到期还本
//            if(item.getItemCycleUnit()==1){//天
//                //天收益
//                BigDecimal interest = account.multiply(rate)
//                        .multiply(BigDecimal.valueOf(cycle))
//                        .divide(BigDecimal.valueOf(360),2);//360天
//                Date yesterdayDate=DateUtils.addDays(new Date(), -1);//昨天日期
//                Integer number=0;
//
//
//            }
            cycle=cycle/30;
            BigDecimal interest = account.multiply(rate)
                    .multiply(BigDecimal.valueOf(cycle)).multiply(new BigDecimal(30))
                    .divide(BigDecimal.valueOf(360), 2, BigDecimal.ROUND_HALF_EVEN);
            BigDecimal monthlyInterest = interest.divide(BigDecimal.valueOf(cycle),2);
            for (int i = 0; i < cycle; i++) {
                RepayPlan p = new RepayPlan();
                p.setPeriod(i + 1);
                if (i == cycle - 1) {
                    p.setAccount(account.add(monthlyInterest));
                    p.setPrincipal(account);
                } else {
                    p.setAccount(BigDecimal.ZERO.add(monthlyInterest));
                    p.setPrincipal(BigDecimal.ZERO);
                }
                p.setInterest(monthlyInterest);
                Date startTime= DateUtils.setDayLast(new Date());
                Date repayTime= DateUtils.addDays(startTime, (i+1)*30);
                p.setEndTime(repayTime);
                planList.add(p);
            }
        } else if (repayMethod == 4) {  //每日付息
            //先息后本
        }else{
            //不识别的还款方式
        }
        return  planList;
    }

    /**
     * 等额本息还款，也称定期付息，即借款人每月按相等的金额偿还贷款本息，其中每月贷款利息按月初剩余贷款本金计算并逐月结清。把按揭贷款的本金总额与利息总额相加，
     * 然后平均分摊到还款期限的每个月中。作为还款人，每个月还给银行固定金额，但每月还款额中的本金比重逐月递增、利息比重逐月递减。
     */



        /**
         * 等额本息计算获取还款方式为等额本息的每月偿还本金和利息
         *
         * 公式：每月偿还本息=〔贷款本金×月利率×(1＋月利率)＾还款月数〕÷〔(1＋月利率)＾还款月数-1〕
         *
         * @param invest
         *            总借款额（贷款本金）
         * @param yearRate
         *            年利率
         * @param totalmonth
         *            还款总月数
         * @return 每月偿还本金和利息,不四舍五入，直接截取小数点最后两位
         */
        public static double getPerMonthPrincipalInterest(double invest, double yearRate, int totalmonth) {
            double monthRate = yearRate / 12;
            BigDecimal monthIncome = new BigDecimal(invest)
                    .multiply(new BigDecimal(monthRate * Math.pow(1 + monthRate, totalmonth)))
                    .divide(new BigDecimal(Math.pow(1 + monthRate, totalmonth) - 1), 2, BigDecimal.ROUND_DOWN);
            return monthIncome.doubleValue();
        }

        /**
         * 等额本息计算获取还款方式为等额本息的每月偿还利息
         *
         * 公式：每月偿还利息=贷款本金×月利率×〔(1+月利率)^还款月数-(1+月利率)^(还款月序号-1)〕÷〔(1+月利率)^还款月数-1〕
         * @param invest
         *            总借款额（贷款本金）
         * @param yearRate
         *            年利率
         * @param totalmonth
         *            还款总月数
         * @return 每月偿还利息
         */
        public static Map<Integer, BigDecimal> getPerMonthInterest(double invest, double yearRate, int totalmonth) {
            Map<Integer, BigDecimal> map = new HashMap<Integer, BigDecimal>();
            double monthRate = yearRate/12;
            BigDecimal monthInterest;
            for (int i = 1; i < totalmonth + 1; i++) {
                BigDecimal multiply = new BigDecimal(invest).multiply(new BigDecimal(monthRate));
                BigDecimal sub  = new BigDecimal(Math.pow(1 + monthRate, totalmonth)).subtract(new BigDecimal(Math.pow(1 + monthRate, i-1)));
                monthInterest = multiply.multiply(sub).divide(new BigDecimal(Math.pow(1 + monthRate, totalmonth) - 1), 6, BigDecimal.ROUND_DOWN);
                monthInterest = monthInterest.setScale(2, BigDecimal.ROUND_DOWN);
                map.put(i, monthInterest);
            }
            return map;
        }

        /**
         * 等额本息计算获取还款方式为等额本息的每月偿还本金
         *
         * @param invest
         *            总借款额（贷款本金）
         * @param yearRate
         *            年利率
         * @param totalmonth
         *            还款总月数
         * @return 每月偿还本金
         */
        public static Map<Integer, BigDecimal> getPerMonthPrincipal(double invest, double yearRate, int totalmonth) {
            double monthRate = yearRate / 12;
            BigDecimal monthIncome = new BigDecimal(invest)
                    .multiply(new BigDecimal(monthRate * Math.pow(1 + monthRate, totalmonth)))
                    .divide(new BigDecimal(Math.pow(1 + monthRate, totalmonth) - 1), 2, BigDecimal.ROUND_DOWN);
            Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(invest, yearRate, totalmonth);
            Map<Integer, BigDecimal> mapPrincipal = new HashMap<Integer, BigDecimal>();

            for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
                mapPrincipal.put(entry.getKey(), monthIncome.subtract(entry.getValue()));
            }
            return mapPrincipal;
        }

        /**
         * 等额本息计算获取还款方式为等额本息的总利息
         *
         * @param invest
         *            总借款额（贷款本金）
         * @param yearRate
         *            年利率
         * @param totalmonth
         *            还款总月数
         * @return 总利息
         */
        public static double getInterestCount(double invest, double yearRate, int totalmonth) {
            BigDecimal count = new BigDecimal(0);
            Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(invest, yearRate, totalmonth);

            for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
                count = count.add(entry.getValue());
            }
            return count.doubleValue();
        }

        /**
         * 应还本金总和
         * @param invest
         *            总借款额（贷款本金）
         * @param yearRate
         *            年利率
         * @param totalmonth
         *            还款总月数
         * @return 应还本金总和
         */
        public static double getPrincipalInterestCount(double invest, double yearRate, int totalmonth) {
            double monthRate = yearRate / 12;
            BigDecimal perMonthInterest = new BigDecimal(invest)
                    .multiply(new BigDecimal(monthRate * Math.pow(1 + monthRate, totalmonth)))
                    .divide(new BigDecimal(Math.pow(1 + monthRate, totalmonth) - 1), 2, BigDecimal.ROUND_DOWN);
            BigDecimal count = perMonthInterest.multiply(new BigDecimal(totalmonth));
            count = count.setScale(2, BigDecimal.ROUND_DOWN);
            return count.doubleValue();
        }

        /**
         * @param args
         */
        public static void main(String[] args) {
            double invest = 10000; // 本金
            int month = 12;
            double yearRate = 0.1; // 年利率
            double perMonthPrincipalInterest = getPerMonthPrincipalInterest(invest, yearRate, month);
            System.out.println("等额本息---每月还款本息：" + perMonthPrincipalInterest);
            Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(invest, yearRate, month);
            System.out.println("等额本息---每月还款利息：" + mapInterest);
            Map<Integer, BigDecimal> mapPrincipal = getPerMonthPrincipal(invest, yearRate, month);
            System.out.println("等额本息---每月还款本金：" + mapPrincipal);
            double count = getInterestCount(invest, yearRate, month);
            System.out.println("等额本息---总利息：" + count);
            double principalInterestCount = getPrincipalInterestCount(invest, yearRate, month);
            System.out.println("等额本息---应还本息总和：" + principalInterestCount);

    }

}
