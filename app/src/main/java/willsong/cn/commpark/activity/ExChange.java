package willsong.cn.commpark.activity;

/**
 * Created by Administrator on 2016/11/10 0010.
 * BillingCycle 计费周期
 * Rate    单位费用
 * TimeNotEnough  时间不足处理 1补 2舍 3四舍五入
 * FreeTime 免费时间
 * FirstBillingCycle   第一个周期
 * FirstCycleRate   第一个周期收费
 * IncludeFreeTime  计费是否包括免费时间段
 * FreeWeekend    周末是否免费
 * FeeCeilingPerDay  24小时收费上线
 * ChargeByNatureDay  24小时为一天
 * BillingOnceaDay   24小时是否只收费一次
 * CountFreeTimeAfterPref   优惠时间后是否计算免费时间
 */

public class ExChange {
    public int Parkid;
    public int BillingCycle;
    public double Rate;
    public int TimeNotEnough;
    public int FreeTime;
    public int FirstBillingCycle;
    public double FirstCycleRate;
    public boolean IncludeFreeTime;
    public boolean FreeWeekend;
    public double FeeCeilingPerDay;
    public boolean ChargeByNatureDay;
    public boolean BillingOnceaDay;
    public boolean CountFreeTimeAfterPref;
}
