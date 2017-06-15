package willsong.cn.commpark.activity.database;

import android.app.Activity;

//异常车辆  最近记录的列表实体类
public class AbnormalCarEntity extends Activity {
    public int _id;
    public String plateNumber;//车牌号：无牌车或车牌号
    public String outTime;//出场时间
    public String busCardMoney;//公交支付金额(实付款)
    public String fieldCode;//场内码：无牌车没有车牌号，通过场内码查询提交数据
    public String seqNo;//POS机流水号
    public String posId;//POSID
    public String cityCode;//城市id
    public String cardPhysicalNumber;//卡物理值
    public String card;//卡表面号
    public String cardCount;//卡计数器
    public String cardMoney;//交易前余额
    public String money;//交易金额
    public String cpuCar;//cpu卡号
    public String transportCardType;//交通卡卡类型
    public String cardTradeTac;//交易认证码
    public String icType;//ic芯片类型
    public String cardVer;//卡版本号
    public String corpId;//营运单位代码
    public String couponStr;//优惠券码
    public String payMoney;//应付金额
    public String CardTradeTime;//公交卡交易时间
    public String terminalNo;//公交终端交易流水号
    public String payType;//支付方式：1微信 2 支付宝 4现金 5公交卡
    public String couponNameStr;//优惠券名称集


    public AbnormalCarEntity(String plateNumber, String outTime, String busCardMoney, String fieldCode, String seqNo,
                             String posId, String cityCode, String cardPhysicalNumber, String card, String cardCount,
                             String cardMoney, String money, String cpuCar, String transportCardType, String cardTradeTac,
                             String icType, String cardVer, String corpId, String couponStr, String payMoney,
                             String CardTradeTime, String terminalNo,String payType,String couponNameStr) {
        super();
        this.plateNumber = plateNumber;
        this.outTime = outTime;
        this.busCardMoney = busCardMoney;
        this.fieldCode = fieldCode;
        this.seqNo = seqNo;
        this.posId = posId;
        this.cityCode = cityCode;
        this.cardPhysicalNumber = cardPhysicalNumber;
        this.card = card;
        this.cardCount = cardCount;
        this.cardMoney = cardMoney;
        this.money = money;
        this.cpuCar = cpuCar;
        this.transportCardType = transportCardType;
        this.cardTradeTac = cardTradeTac;
        this.icType = icType;
        this.cardVer = cardVer;
        this.corpId = corpId;
        this.couponStr = couponStr;
        this.payMoney = payMoney;
        this.CardTradeTime = CardTradeTime;
        this.terminalNo = terminalNo;
        this.payType = payType;
        this.couponNameStr = couponNameStr;
    }

    public AbnormalCarEntity(int _id, String plateNumber, String outTime, String busCardMoney, String fieldCode, String seqNo,
                             String posId, String cityCode, String cardPhysicalNumber, String card, String cardCount,
                             String cardMoney, String money, String cpuCar, String transportCardType, String cardTradeTac,
                             String icType, String cardVer, String corpId, String couponStr, String payMoney,
                             String CardTradeTime, String terminalNo,String payType,String couponNameStr) {
        super();
        this._id = _id;
        this.plateNumber = plateNumber;
        this.outTime = outTime;
        this.busCardMoney = busCardMoney;
        this.fieldCode = fieldCode;
        this.seqNo = seqNo;
        this.posId = posId;
        this.cityCode = cityCode;
        this.cardPhysicalNumber = cardPhysicalNumber;
        this.card = card;
        this.cardCount = cardCount;
        this.cardMoney = cardMoney;
        this.money = money;
        this.cpuCar = cpuCar;
        this.transportCardType = transportCardType;
        this.cardTradeTac = cardTradeTac;
        this.icType = icType;
        this.cardVer = cardVer;
        this.corpId = corpId;
        this.couponStr = couponStr;
        this.payMoney = payMoney;
        this.CardTradeTime = CardTradeTime;
        this.terminalNo = terminalNo;
        this.payType = payType;
        this.couponNameStr = couponNameStr;
    }
    public AbnormalCarEntity() {
        super();
    }
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getBusCardMoney() {
        return busCardMoney;
    }

    public void setBusCardMoney(String busCardMoney) {
        this.busCardMoney = busCardMoney;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCardPhysicalNumber() {
        return cardPhysicalNumber;
    }

    public void setCardPhysicalNumber(String cardPhysicalNumber) {
        this.cardPhysicalNumber = cardPhysicalNumber;
    }

    public String getPasm() {
        return posId;
    }

    public void setPasm(String posId) {
        this.posId = posId;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getCardMoney() {
        return cardMoney;
    }

    public void setCardMoney(String cardMoney) {
        this.cardMoney = cardMoney;
    }

    public String getCardCount() {
        return cardCount;
    }

    public void setCardCount(String cardCount) {
        this.cardCount = cardCount;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCpuCar() {
        return cpuCar;
    }

    public void setCpuCar(String cpuCar) {
        this.cpuCar = cpuCar;
    }

    public String getTransportCardType() {
        return transportCardType;
    }

    public void setTransportCardType(String transportCardType) {
        this.transportCardType = transportCardType;
    }

    public String getCardTradeTac() {
        return cardTradeTac;
    }

    public void setCardTradeTac(String cardTradeTac) {
        this.cardTradeTac = cardTradeTac;
    }

    public String getIcType() {
        return icType;
    }

    public void setIcType(String icType) {
        this.icType = icType;
    }

    public String getCardVer() {
        return cardVer;
    }

    public void setCardVer(String cardVer) {
        this.cardVer = cardVer;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getCouponStr() {
        return couponStr;
    }

    public void setCouponStr(String couponStr) {
        this.couponStr = couponStr;
    }

    public String getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(String payMoney) {
        this.payMoney = payMoney;
    }

    public String getCardTradeTime() {
        return CardTradeTime;
    }

    public void setCardTradeTime(String cardTradeTime) {
        CardTradeTime = cardTradeTime;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

}
