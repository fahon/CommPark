package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/20 0020.
 */

public class ReqTop {


    public static byte[] intToByteArray(int i) {

        byte[] bytes = new byte[4];
        bytes[3] = (byte) ((i >> 24) & 0xFF);
        bytes[2] = (byte) ((i >> 16) & 0xFF);
        bytes[1] = (byte) ((i >> 8) & 0xFF);
        bytes[0] = (byte) (i & 0xFF);
        return bytes;
    }
}
