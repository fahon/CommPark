package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/21 0021.
 * 车辆出场
 */

public class OutBus extends BaseReqBean {
    public OutBus(int seqno, String code, universal universal, String uid, Business[] businessLogList) {
        super(seqno, code, universal);
        this.uid = uid;
        this.businessLogList = businessLogList;
    }

    public OutBus(String uid, Business[] businessLogList) {
        this.uid = uid;
        this.businessLogList = businessLogList;
    }

    //    工号：uid(string)
    public String uid;
//    终端业务操作流水记录列表：businessLogList
    public Business[] businessLogList;
}
