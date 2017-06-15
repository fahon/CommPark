package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class Business {

    //    批次号：batchCode(string)
    public String batchCode;
    //    业务流水号：bizSn(String)
    public int bizSn;
    //    停车点编号：parkingSpotId(string)
    public String parkingSpotId;
    //    平台编号：platformId(string)
    public String platformId;
    //    泊位编号：berthId(string)
    public String berthId;
    //    附加泊位：addBerth(string)
    public String addBerth;
    //    业务类型：businessType(String)
    public int businessType;
    //    操作类型：actType(String)
    public int actType;
    //    操作时间：actTime(DateTime)
    public String actTime;
    //    车牌号：carNumber(string)
    public String carNumber;
    //    包月证号：monthlyCertNumber(string)
    public String monthlyCertNumber;
    // 车辆类型：carType(String)
    public int carType;
    //    总剩余车位：totRemainNum(String)
    public int totRemainNum;
    //    月租剩余车位：monthlyRemainNum(String)
    public int monthlyRemainNum;
    //    访客剩余车位：guestRemainNum(String)
    public int guestRemainNum;
    //    停车时长：parkingTimeLength(String)
    public int parkingTimeLength;
    //    收费金额：payMoney(String)
    public int payMoney;
    //    支付类型：paymentType(String)
    public int paymentType;
    //    停车凭证类型：voucherType(String)
    public int voucherType;
    //    停车凭证号：voucherNo(String)
    public String voucherNo;

    public Business(String batchCode, int bizSn, String parkingSpotId, String platformId, String berthId, String addBerth, int businessType, int actType, String actTime, String carNumber, String monthlyCertNumber, int carType, int totRemainNum, int monthlyRemainNum, int guestRemainNum, int parkingTimeLength, int payMoney, int paymentType, int voucherType, String voucherNo) {
        this.batchCode = batchCode;
        this.bizSn = bizSn;
        this.parkingSpotId = parkingSpotId;
        this.platformId = platformId;
        this.berthId = berthId;
        this.addBerth = addBerth;
        this.businessType = businessType;
        this.actType = actType;
        this.actTime = actTime;
        this.carNumber = carNumber;
        this.monthlyCertNumber = monthlyCertNumber;
        this.carType = carType;
        this.totRemainNum = totRemainNum;
        this.monthlyRemainNum = monthlyRemainNum;
        this.guestRemainNum = guestRemainNum;
        this.parkingTimeLength = parkingTimeLength;
        this.payMoney = payMoney;
        this.paymentType = paymentType;
        this.voucherType = voucherType;
        this.voucherNo = voucherNo;
    }
}
