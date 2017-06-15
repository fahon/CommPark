package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/20 0020.
 */

public class BusinessLog {

    //    批次号：batchCode(string)
    public String batchCode;
    //    业务流水号：bizSn(int)
    public int bizSn;
    //    停车点编号：parkingSpotId(string) 必须
    public String parkingSpotId;
    //    平台编号：platformId(string)
    public String platformId;
    //    泊位编号：berthId(string)，不超过16字符
    public String berthId;
    //    附加泊位：addBerth(string) ，不超过16字符
    public String addBerth;
    //    业务类型：businessType(int)
    public int businessType;
    //    操作类型：actType(int) 必须
    public int actType;
    //    操作时间：actTime(DateTime) 必须
    public String actTime;
    //    车牌号：carNumber(string) 必须，不超过16字符
    public String carNumber;
    //    包月证号：monthlyCertNumber(string)，
    public String monthlyCertNumber;
    //    不超过32字符车辆类型：carType(int)
    public int carType;
    //    总剩余车位：totRemainNum(int) 必须
    public int totRemainNum;
    //    月租剩余车位：monthlyRemainNum(int) 必须
    public int monthlyRemainNum;
    //    访客剩余车位：guestRemainNum(int) 必须
    public int guestRemainNum;
    //    停车凭证类型：voucherType(int)
    public int voucherType;
    //    停车凭证号：voucherNo(String)，不超过20字符
    public String voucherNo;

    public BusinessLog(String batchCode, int bizSn, String parkingSpotId,
                       String platformId, String berthId, String addBerth,
                       int businessType, int actType, String actTime,
                       String carNumber, String monthlyCertNumber, int carType,
                       int totRemainNum, int monthlyRemainNum, int guestRemainNum,
                       int voucherType, String voucherNo) {
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
        this.voucherType = voucherType;
        this.voucherNo = voucherNo;
    }
}
