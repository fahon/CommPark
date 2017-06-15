package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class CommTradeRecord {
    //    停车点编号：parkingSpotId(string)
    public String parkingSpotId;
    //    平台编号：platformId(string)
    public String platformId;
    //    泊位编号：berthId(string)
    public String berthId;
    //    批次号：batchCode(string)
    public String batchCode;
    //    业务流水号：bizSn(int)
    public int bizSn;
    //    交易流水号：tradeCode(int)
    public int tradeCode;
    //    交易类型：tradeType(int)
    public int tradeType;
    //    支付类型：paymentType(int)
    public int paymentType;
    //    应付金额：dueMoney(int)
    public int dueMoney;
    //    实付金额：payMoney(int)
    public int payMoney;
    //    折扣金额：payDiscountMoney(int)
    public int payDiscountMoney;
    //    补缴金额：compensateMoney(int)
    public int compensateMoney;
    //    交易时间：tradeTime(DateTime)
    public String tradeTime;
    //    记录类型：recordType(int)
    public int recordType;
    //    城市代码：cardCityCode,N4
    public int cardCityCode;
    //    行业代码：cardBusinessCode,N2
    public int cardBusinessCode;
    //    卡号：cardPhysicsNumber,N10
    public int cardPhysicsNumber;
    //    卡表面号：cardSurfaceNumber,N11
    public int cardSurfaceNumber;
    //    卡交易次数：cardTradeCount,N6
    public int cardTradeCount;
    //    消费前金额：cardBeforeTradeMoney,N8
    public int cardBeforeTradeMoney;
    //    交易金额：cardTradeMoney,N8
    public int cardTradeMoney;
    //    交易日期：cardTradeDate,N8
    public int cardTradeDate;
    //    交易时间：cardTradeTime,N6
    public int cardTradeTime;
    //    TAC：cardTac,H8
    public String cardTac;
    //    卡类型：cardType,N2
    public int cardType;
    //    启用日期：cardStartDate,N8
    public int cardStartDate;
    //    加款日期cardRechangeDate,N8
    public int cardRechangeDate;
    //    CPU卡内号：cpuCardNo,H20
    public String cpuCardNo;
    //    芯片标志：icType,N1{0:mi卡;1:cpu卡}
    public int icType;
    //    消费类型：purType,H2{06:消费;09:复合消费}(M1卡用00)
    public String purType;
    //    终端机编号：termId,H12
    public String termId;
    //    终端机流水:termSeq,H8
    public String termSeq;
    //    清算交易类型：cchsTxn,N2{消费:88;锁卡99}
    public int cchsTxn;
    //    卡版本：cardVer,N2
    public int cardVer;

    public CommTradeRecord(String parkingSpotId, String platformId,
                           String berthId, String batchCode, int bizSn,
                           int tradeCode, int tradeType, int paymentType,
                           int dueMoney, int payMoney, int payDiscountMoney,
                           int compensateMoney, String tradeTime, int recordType,
                           int cardCityCode, int cardBusinessCode, int cardPhysicsNumber,
                           int cardSurfaceNumber, int cardTradeCount, int cardBeforeTradeMoney,
                           int cardTradeMoney, int cardTradeDate, int cardTradeTime, String cardTac,
                           int cardType, int cardStartDate, int cardRechangeDate, String cpuCardNo,
                           int icType, String purType, String termId, String termSeq, int cchsTxn,
                           int cardVer) {
        this.parkingSpotId = parkingSpotId;
        this.platformId = platformId;
        this.berthId = berthId;
        this.batchCode = batchCode;
        this.bizSn = bizSn;
        this.tradeCode = tradeCode;
        this.tradeType = tradeType;
        this.paymentType = paymentType;
        this.dueMoney = dueMoney;
        this.payMoney = payMoney;
        this.payDiscountMoney = payDiscountMoney;
        this.compensateMoney = compensateMoney;
        this.tradeTime = tradeTime;
        this.recordType = recordType;
        this.cardCityCode = cardCityCode;
        this.cardBusinessCode = cardBusinessCode;
        this.cardPhysicsNumber = cardPhysicsNumber;
        this.cardSurfaceNumber = cardSurfaceNumber;
        this.cardTradeCount = cardTradeCount;
        this.cardBeforeTradeMoney = cardBeforeTradeMoney;
        this.cardTradeMoney = cardTradeMoney;
        this.cardTradeDate = cardTradeDate;
        this.cardTradeTime = cardTradeTime;
        this.cardTac = cardTac;
        this.cardType = cardType;
        this.cardStartDate = cardStartDate;
        this.cardRechangeDate = cardRechangeDate;
        this.cpuCardNo = cpuCardNo;
        this.icType = icType;
        this.purType = purType;
        this.termId = termId;
        this.termSeq = termSeq;
        this.cchsTxn = cchsTxn;
        this.cardVer = cardVer;
    }
}
