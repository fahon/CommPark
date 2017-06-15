package willsong.cn.commpark.activity.util;

import willsong.cn.commpark.activity.Bean.FunctionCode;
import willsong.cn.commpark.activity.Bean.StreamDirection;

/**
 * Created by Administrator on 2016/10/19 0019.
 * 车位
 */

public class CParkingFrame extends CBaseFrame {
    public CParkingFrame()
    {

    }

    public CParkingFrame(short tCnt, short tMonth, short tVister, short rCnt, short rMonth, short rVister)
    {
        CreateControlCode(StreamDirection.Upload, FunctionCode.Park);
        m_TotalCount = tCnt;
        m_TotalByMonth = tMonth;
        m_TotalVister = tVister;
        m_RemainderCount = rCnt;
        m_RemainderByMonth = rMonth;
        m_RemainderVister = rVister;
    }

    public static int Framelength()
    {
       return 2 + 1 + 2 + 4 + 4 + 1 + 2 + 1 + 2 + 2 + 2 + 2 + 2 + 2;
    }

    public short m_TotalCount;
    /// <summary>
    /// 总停车位
    /// </summary>

    public short m_TotalByMonth;
    /// <summary>
    /// 月租长包总车位
    /// </summary>


    public short m_TotalVister;
    /// <summary>
    /// 时租访客总车位
    /// </summary>

    public short m_RemainderCount;
    /// <summary>
    /// 总剩余车位
    /// </summary>

    public short m_RemainderByMonth;
    /// <summary>
    /// 月租长包剩余车位
    /// </summary>

    public short m_RemainderVister;
    /// <summary>
    /// 时租访客剩余车位
    /// </summary>

    protected void SetData()
    {
        setM_Data(Addition(TrunTo2Bytes(ToBytes(m_TotalCount), null),
                TrunTo2Bytes(ToBytes(m_TotalByMonth), null),
                TrunTo2Bytes(ToBytes(m_TotalVister),null),
                TrunTo2Bytes(ToBytes(m_RemainderCount), null),
                TrunTo2Bytes(ToBytes(m_RemainderByMonth), null),
                TrunTo2Bytes(ToBytes(m_RemainderVister),null),null));
    }
    private static byte[] Addition(byte[] a, byte[] b, byte[] c, byte[] d, byte[] e, byte[] f,   byte[] returnbyte)
    {
        returnbyte = new byte[a.length + b.length + c.length + d.length + e.length + f.length ];
        for (int i = 0; i < a.length; i++)
            returnbyte[i] = a[i];
        for (int i = 0; i < b.length; i++)
            returnbyte[a.length + i] = b[i];
        for (int i = 0; i < c.length; i++)
            returnbyte[a.length + b.length + i] = c[i];
        for (int i = 0; i < d.length; i++)
            returnbyte[a.length + b.length + c.length + i] = d[i];
        for (int i = 0; i < e.length; i++)
            returnbyte[a.length + b.length + c.length + d.length + i] = e[i];
        for (int i = 0; i < f.length; i++)
            returnbyte[a.length + b.length + c.length + d.length + e.length + i] = f[i];
        return returnbyte;
    }
}
