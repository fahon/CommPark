package willsong.cn.commpark.activity.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import willsong.cn.commpark.activity.ExChange;

/**
 * Created by Administrator on 2016/11/11 0011.
 */

public class ExChangeUtil {

    public double getMoneyChange(ExChange ec,Date EntreDate, Date ExDate) {
        if(ec.FreeWeekend){//周末免费
            return getBillDates(ec,EntreDate,ExDate);
        }else {
            return getDates(ec,EntreDate,ExDate);
        }
    }

    public double getChange(ExChange ec, int timeDifference) {
        double money = 0;
        int bill = timeDifference - ec.FreeTime;//减去免费时间
        if (bill > 0) {//是否免费
            if (ec.IncludeFreeTime) {//包含免费时段
                if (ec.FirstBillingCycle > 0) { //计算第一个周期
                    int FirstBill = timeDifference - ec.FirstBillingCycle;//包含免费时段减去第一个时间周期
                    if (FirstBill > 0) {//在一个周期内
                        money += getMoney(ec, FirstBill) + ec.FirstCycleRate;
                    } else {
                        money += ec.FirstCycleRate;
                    }
                } else {
                    money += getMoney(ec, timeDifference);
                }
            } else {//不包含免费时段
                if (ec.FirstBillingCycle > 0) { //计算第一个周期
                    int UnFirstBill = bill - ec.FirstBillingCycle;//不包含免费时段减去第一个时间周期
                    if (UnFirstBill > 0) {
                        money += getMoney(ec, UnFirstBill) + ec.FirstCycleRate;
                    } else {
                        money += ec.FirstCycleRate;
                    }
                } else {
                    money += getMoney(ec, bill);
                }
            }
        } else {
            return money;
        }
        return money;
    }

    //计算金额
    public double getMoney(ExChange ec, int timeDiff) {
        if (1 == ec.TimeNotEnough) {//补足
            return Math.ceil(1.0 * timeDiff / ec.BillingCycle) * ec.Rate;
        } else if (2 == ec.TimeNotEnough) {//舍弃
            return Math.floor(1.0 * timeDiff / ec.BillingCycle) * ec.Rate;
        } else if (3 == ec.TimeNotEnough) {//四舍五入
            return Math.round(1.0 * timeDiff / ec.BillingCycle) * ec.Rate;
        }
        return 0;
    }

    /**
     * 判断是否是同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public boolean isDay(Date date1, Date date2) {
        SimpleDateFormat sf = new SimpleDateFormat("YYYY/MM/DD");
        if (sf.format(date1).equals(sf.format(date2))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 需要计费的天数
     *
     * @param dBegin
     * @param dEnd
     * @return
     */
    public double getBillDates(ExChange ec,Date dBegin, Date dEnd) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd");
        double money = 0;//00 - 24小时制
        double ermoney = 0;
        List<Date> lDate = findDatess(simpleDateFormat.format(dBegin), simpleDateFormat.format(dEnd));
        int index = 0; //计费天数
        int time = 0;
        if(lDate.size() > 1){ //只有一个单日
            for (int i = 0;i < lDate.size();i++){
                if(0 == i){
                    if(!isWeekend(lDate.get(i))){
                        if(0 == ec.FeeCeilingPerDay || ec.ChargeByNatureDay == false){
                            int hours = dBegin.getHours()*60*60;
                            int month = dBegin.getMinutes()*60;
                            int seconds = dBegin.getSeconds();
                            money += getChange(ec,86400 - (hours + month + seconds));
                            time += 86400 - (hours + month + seconds);
                            continue;
                        }
                    }else {
                        continue;
                    }
                }
                if(i == lDate.size()-1){
                    if(isWeekend(lDate.get(i))){//是周末
                        money += 0;
                    }else { //不是周末最后一天的钱
                        int hours = dEnd.getHours()*60*60;
                        int month = dEnd.getMinutes()*60;
                        int seconds = dEnd.getSeconds();
                        if(0 != ec.FeeCeilingPerDay){ //24小时是否收费上线
                            double ss = getMoney(ec,(hours + month + seconds));
                            if(ss > ec.FeeCeilingPerDay){
                                money += ec.FeeCeilingPerDay;
                            }else {
                                money += getMoney(ec,(hours + month + seconds));
                            }
                        }else {
                            money += getMoney(ec,(hours + month + seconds));
                        }
                        //money += getMoney(ec,(hours + month + seconds));
                        time += (hours + month + seconds);
                        break;
                    }
                }
                if(isWeekend(lDate.get(i))){
                    money += 0;
                    time += 0;
                    continue;
                }
                index++;
                time += 24*60*60;
            }
            System.out.println(time);
            if(ec.ChargeByNatureDay){
                if(0 != ec.FeeCeilingPerDay){ //24小时是否收费上线
                    money += ec.FeeCeilingPerDay * index;
                }else {
                    money = getChange(ec,time);
                }
            }else{
                if(0 != ec.FeeCeilingPerDay){ //24小时是否收费上线
                    ermoney += time/86400 * ec.FeeCeilingPerDay;
                    if(getChange(ec,time%86400) > ec.FeeCeilingPerDay){
                        ermoney += ec.FeeCeilingPerDay;
                    }else{
                        ermoney += getChange(ec,time%86400);
                    }
                    return ermoney;
                }else {
                    money = getChange(ec,time);
                }
            }
        }else {
            if(isWeekend(dBegin)) {//判断是否是周末
                money += 0;
            }else{
                if(0 != ec.FeeCeilingPerDay && getChange(ec, datetime(dBegin, dEnd)) > ec.FeeCeilingPerDay){
                    return ec.FeeCeilingPerDay;
                }else{
                    return getChange(ec, datetime(dBegin, dEnd));
                }
            }
        }
        return money;
    }

    /**
     * 算出有多少个周末
     * @return
     */
    public int getDay(Date dBegin, Date dEnd){
        int daytime = 0;
        List<Date> day = new ArrayList<>();
        List<Date> lDate = findDates(dBegin, dEnd);
        for (int i = 0;i < lDate.size();i++){
            if(isWeekend(lDate.get(i))){
                day.add(lDate.get(i));
            }
        }
        return daytime;
    }

    /**
     * 得到起始时间
     *
     * @param dBegin
     * @param dEnd
     * @return
     */
    public List<Date> findDates(Date dBegin, Date dEnd) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String Begin = sf.format(dBegin);
        String End = sf.format(dEnd);
        Date Begininfo= null;
        Date Endinfo = null;
        try {
            Begininfo = sf.parse(Begin);
            Endinfo = sf.parse(End);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        List lDate = new ArrayList();
        lDate.add(Begininfo);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(Begininfo);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(Endinfo);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(calBegin.getTime());
        }
        return lDate;
    }

    /**
     * 得到起始时间
     *
     * @param dBegin
     * @param dEnd
     * @return
     */
    public static List<Date> findDatess(String dBegin, String dEnd) {
        SimpleDateFormat st = new SimpleDateFormat("yyyy-MM-dd");
        String startinfo = dBegin.substring(0, 10);
        String endinfo = dEnd.substring(0, 10);
        Date Begininfo= null;
        Date Endinfo = null;
        try {
            Begininfo = st.parse(startinfo);
            Endinfo = st.parse(endinfo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List lDate = new ArrayList();
        lDate.add(Begininfo);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(Begininfo);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(Endinfo);
        // 测试此日期是否在指定日期之后
        while (Endinfo.after(calBegin.getTime())) {
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(calBegin.getTime());
        }
        return lDate;
    }

    /**
     * 判断时候是周末
     *
     * @param day
     * @return
     */
    public boolean isWeekend(Date day) {
        if (day.getDay() == 0 || day.getDay() == 6) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 时差
     * @param enterTime
     * @param exitTime
     * @return
     */
    public int datetime(Date enterTime,Date exitTime){
        long l=exitTime.getTime()-enterTime.getTime();
        long day=l/(24*60*60*1000);
        long hour=(l/(60*60*1000)-day*24);
        long min=((l/(60*1000))-day*24*60-hour*60);
        long s=(l/1000-day*24*60*60-hour*60*60-min*60);
        System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
        System.out.println((int) l/1000 + "");
        return (int) l/1000;
    }

    /**
     * 需要计费的天数
     *
     * @param dBegin
     * @param dEnd
     * @return
     */
    public double getDates(ExChange ec,Date dBegin, Date dEnd) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd");
        double money = 0;
        double ermoney = 0;
        List<Date> lDate = findDatess(simpleDateFormat.format(dBegin), simpleDateFormat.format(dEnd));
        int index = 0; //计费天数
        int time = 0;
        if(lDate.size() > 1){ //只有一个单日
            for (int i = 0;i < lDate.size();i++){
                if(0 == i){
                    if(0 == ec.FeeCeilingPerDay || ec.ChargeByNatureDay == false){
                        int hours = dBegin.getHours()*60*60;
                        int month = dBegin.getMinutes()*60;
                        int seconds = dBegin.getSeconds();
                        money += getChange(ec,86400 - (hours + month + seconds));
                        time += 86400 - (hours + month + seconds);
                        continue;
                    }

                }
                if(i == lDate.size()-1){
                    int hours = dEnd.getHours()*60*60;
                    int month = dEnd.getMinutes()*60;
                    int seconds = dEnd.getSeconds();
                    if(0 != ec.FeeCeilingPerDay){ //24小时是否收费上线
                        double ss = getMoney(ec,(hours + month + seconds));
                        if(ss > ec.FeeCeilingPerDay){
                            money += ec.FeeCeilingPerDay;
                        }else {
                            money += getMoney(ec,(hours + month + seconds));
                        }
                    }else {
                        money += getMoney(ec,(hours + month + seconds));
                    }
                    //money += getMoney(ec,(hours + month + seconds));
                    time += (hours + month + seconds);
                    break;
                }
                index++;
                time += 24*60*60;
            }
            if(ec.ChargeByNatureDay){
                if(0 != ec.FeeCeilingPerDay){ //24小时是否收费上线
                    money += ec.FeeCeilingPerDay * index;
                }else {
                    money = getChange(ec,time);
                }
            }else{
                if(0 != ec.FeeCeilingPerDay){ //24小时是否收费上线
                    ermoney += time/86400 * ec.FeeCeilingPerDay;
                    if(getChange(ec,time%86400) > ec.FeeCeilingPerDay){
                        ermoney += ec.FeeCeilingPerDay;
                    }else{
                        ermoney += getChange(ec,time%86400);
                    }
                    return ermoney;
                }else {
                    money = getChange(ec,time);
                }
            }
        }else {
            if(0 != ec.FeeCeilingPerDay && getChange(ec, datetime(dBegin, dEnd)) > ec.FeeCeilingPerDay){
                return ec.FeeCeilingPerDay;
            }else{
                return getChange(ec, datetime(dBegin, dEnd));
            }
        }
        return money;
    }
}
