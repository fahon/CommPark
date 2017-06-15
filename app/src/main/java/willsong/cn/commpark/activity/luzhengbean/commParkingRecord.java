package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class commParkingRecord {
    //    业务类型：parkingRecordType(String)
    public int parkingRecordType;
    //    停车点编号：parkingSpotId(string)
    public String parkingSpotId;
    //    平台编号：platformId(string)
    public String platformId;
    //    泊位编号：berthId(string)
    public String berthId;
    //    附加泊位：addBerth(string)
    public String addBerth;
    //    车牌号：carNumber(string)
    public String carNumber;
    //    车辆类型：carType(String)
    public int carType;
    //    停车操作类型：parkingActType(String)
    public int parkingActType;
    //    离开操作类型：leavingActType(String)
    public int leavingActType;
    //    到达时间：parkingTime(DateTime)
    public String parkingTime;
    //    离开时间：leavingTime(DateTime)
    public String leavingTime;
    //    停车时长：parkingTimeLength(String)，用秒数表示
    public int parkingTimeLength;
    //    到达批次号：parkingBatchCode(string)
    public String parkingBatchCode;
    //    到达业务流水号：parkingBizSn(String)
    public int parkingBizSn;
    //    离开批次号：leavingBatchCode(string)
    public String leavingBatchCode;
    //    离开业务流水号：leavingBizSn(String)
    public int leavingBizSn;
    //    计费金额：factMoney(String)
    public int factMoney;
    //    计费折扣：factDiscount(String)
    public int factDiscount;
    //    应缴金额：dueMoney(String)
    public int dueMoney;
    //    实缴金额：payMoney(String)
    public int payMoney;
    //    实缴折扣：payDiscount(String)
    public int payDiscount;
    //    剩余应付：dueBalance(String)
    public int dueBalance;
    //    预付费时长：prepayTimeLength(String)
    public int prepayTimeLength;
    //    预付费金额：prepayMoney(String)
    public int prepayMoney;
    //    预付费折扣：prepayDiscount(String)
    public int prepayDiscount;
    //    补缴金额：compensateMoney(String)
    public int compensateMoney;

    public commParkingRecord(int parkingRecordType, String parkingSpotId, String platformId,
                             String berthId, String addBerth, String carNumber, int carType,
                             int parkingActType, int leavingActType, String parkingTime, String leavingTime,
                             int parkingTimeLength, String parkingBatchCode, int parkingBizSn,
                             String leavingBatchCode, int leavingBizSn, int factMoney, int factDiscount,
                             int dueMoney, int payMoney, int payDiscount, int dueBalance, int prepayTimeLength,
                             int prepayMoney, int prepayDiscount, int compensateMoney) {
        this.parkingRecordType = parkingRecordType;
        this.parkingSpotId = parkingSpotId;
        this.platformId = platformId;
        this.berthId = berthId;
        this.addBerth = addBerth;
        this.carNumber = carNumber;
        this.carType = carType;
        this.parkingActType = parkingActType;
        this.leavingActType = leavingActType;
        this.parkingTime = parkingTime;
        this.leavingTime = leavingTime;
        this.parkingTimeLength = parkingTimeLength;
        this.parkingBatchCode = parkingBatchCode;
        this.parkingBizSn = parkingBizSn;
        this.leavingBatchCode = leavingBatchCode;
        this.leavingBizSn = leavingBizSn;
        this.factMoney = factMoney;
        this.factDiscount = factDiscount;
        this.dueMoney = dueMoney;
        this.payMoney = payMoney;
        this.payDiscount = payDiscount;
        this.dueBalance = dueBalance;
        this.prepayTimeLength = prepayTimeLength;
        this.prepayMoney = prepayMoney;
        this.prepayDiscount = prepayDiscount;
        this.compensateMoney = compensateMoney;
    }
}
