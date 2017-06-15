package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class BusCard {

//    流水号：seqno（int）
    public int seqno;
//    业务编号：code(string)
    public String code;
//    通用请求字段：commRequest
    public universal commRequest;
//    工号：uid(string)
    public String uid;
//    终端业务交易记录对象：commTradeRecord
    public CommTradeRecord[] commTradeRecord;

    public BusCard(int seqno, String code,
                   universal commRequest, String uid,
                   CommTradeRecord[] commTradeRecord) {
        this.seqno = seqno;
        this.code = code;
        this.commRequest = commRequest;
        this.uid = uid;
        this.commTradeRecord = commTradeRecord;
    }
}
