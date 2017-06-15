package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class EzStop extends BaseReqBean{

//    流水号：seqno（int）
//    业务编号：code(string)
//    通用请求字段：commRequest
//    工号：uid(string)
    public String uid;
//    终端业务记录对象：commParkingRecord
    public commParkingRecord[] commParkingRecord;

    public EzStop(int seqno, String code, universal universal, String uid, commParkingRecord[] commParkingRecord) {
        super(seqno, code, universal);
        this.uid = uid;
        this.commParkingRecord = commParkingRecord;
    }

    public EzStop(String uid, commParkingRecord[] commParkingRecord) {
        this.uid = uid;
        this.commParkingRecord = commParkingRecord;
    }
}
