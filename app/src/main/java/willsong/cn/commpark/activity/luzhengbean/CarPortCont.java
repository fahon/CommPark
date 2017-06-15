package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class CarPortCont {
//    流水号：seqno（String）
    public int seqno;
//    业务编号：code（string）
    public String code;
//    通用请求字段：commRequest
    public universal commRequest;
//    工号：uid（string）
    public String uid;
//    批次号：batchCode(string)
    public String batchCode;
//    业务流水号：bizSn(String)
    public int bizSn;
//    操作时间：actTime(DateTime)
    public String actTime;
//    停车点编号：parkingSpotId(string)
    public String parkingSpotId;
//    平台编号：platformId(string)
    public String platformId;
//    总车位：totBerthNum(String)
    public int totBerthNum;
//    月租车位：monthlyBerthNum(String)
    public int monthlyBerthNum;
//    访客车位：guesBerthNum(String)
    public int guesBerthNum;
//    总剩余车位：totRemainNum(String)
    public int totRemainNum;
//    月租剩余车位：monthlyRemainNum(String)
    public int monthlyRemainNum;
//    访客剩余车位：guestRemainNum(String)
    public int guestRemainNum;

    public CarPortCont(int seqno, String code, universal commRequest,
                       String uid, String batchCode, int bizSn, String actTime,
                       String parkingSpotId, String platformId,
                       int totBerthNum, int monthlyBerthNum, int guesBerthNum,
                       int totRemainNum, int monthlyRemainNum, int guestRemainNum) {
        this.seqno = seqno;
        this.code = code;
        this.commRequest = commRequest;
        this.uid = uid;
        this.batchCode = batchCode;
        this.bizSn = bizSn;
        this.actTime = actTime;
        this.parkingSpotId = parkingSpotId;
        this.platformId = platformId;
        this.totBerthNum = totBerthNum;
        this.monthlyBerthNum = monthlyBerthNum;
        this.guesBerthNum = guesBerthNum;
        this.totRemainNum = totRemainNum;
        this.monthlyRemainNum = monthlyRemainNum;
        this.guestRemainNum = guestRemainNum;
    }
}
