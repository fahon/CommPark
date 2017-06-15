package willsong.cn.commpark.activity.util;


import willsong.cn.commpark.activity.Bean.FunctionCode;
import willsong.cn.commpark.activity.Bean.StreamDirection;

/**
 * Created by Administrator on 2016/10/19 0019.
 */

public class CStatusFrame extends CBaseFrame {
    public CStatusFrame()
    {

    }

    public CStatusFrame(byte work, short alarm)
    {
        CreateControlCode(StreamDirection.Upload, FunctionCode.State);
        m_WorkingStatus = work;
        m_AlarmStatus = alarm;
    }

    public static int Framelength()
    {
        return 2 + 1 + 2 + 4 + 4 + 1 + 2 + 1 + 1 + 2;
    }

    public byte m_WorkingStatus;
    /// <summary>
    /// 电子收费系统工作状态
    /// </summary>

    public short m_AlarmStatus;
    /// <summary>
    /// 电子收费系统报警状态
    /// </summary>

    protected void SetData()
    {
        setM_Data(Addition(new byte[] { m_WorkingStatus }, TrunTo2Bytes(ToBytes((short)m_AlarmStatus), null)
                ,null));
    }
    private static byte[] Addition(byte[] a, byte[] b, byte[] returnbyte)
    {
        returnbyte = new byte[a.length + b.length ];
        for (int i = 0; i < a.length; i++)
            returnbyte[i] = a[i];
        for (int i = 0; i < b.length; i++)
            returnbyte[a.length + i] = b[i];
        return returnbyte;
    }
}
