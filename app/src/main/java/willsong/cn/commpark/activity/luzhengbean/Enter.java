package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/20 0020.
 */

public class Enter extends BaseReqBean{
//    工号：uid(string)
    public String uid;
//    终端业务操作流水记录列表：businessLogList
    public BusinessLog[] businessLogList;

    public Enter(int seqno, String code, universal universal, String uid, BusinessLog[] businessLogList) {
        super(seqno, code, universal);
        this.uid = uid;
        this.businessLogList = businessLogList;
    }

    public Enter(String uid, BusinessLog[] businessLogList) {
        this.uid = uid;
        this.businessLogList = businessLogList;
    }
}
