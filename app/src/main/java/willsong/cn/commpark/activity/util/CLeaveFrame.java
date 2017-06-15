package willsong.cn.commpark.activity.util;

import java.text.SimpleDateFormat;

import willsong.cn.commpark.activity.Bean.FunctionCode;
import willsong.cn.commpark.activity.Bean.StreamDirection;

/**
 * Created by Administrator on 2016/10/19 0019.
 */

public class CLeaveFrame extends CBaseFrame {

    public CLeaveFrame()
    {
        CreateControlCode(StreamDirection.Upload, FunctionCode.Leave);
    }

    public CLeaveFrame(String lt, byte pt, short remainderTotal, short remainderByMonth, short remainderVister, String licPlate,
                       int duration, int fee, byte pay)
    {
        CreateControlCode(StreamDirection.Upload, FunctionCode.Leave);
        m_LeaveTime = lt;
        m_ParkType = pt;
        m_RemainderCount = remainderTotal;
        m_RemainderByMonth = remainderByMonth;
        m_RemainderVister = remainderVister;
        m_LicensePlate = licPlate;
        m_Duration = duration;
        m_Fee = fee;
        m_PaymentType = pay;
    }

    public static int Framelength()
    {
        return 2 + 1 + 2 + 4 + 4 + 1 + 2 + 1 + 6 + 1 + 6 + 12 + 9;
    }

    public String m_LeaveTime;
    /// <summary>
    /// 离场时间
    /// </summary>

    public byte m_ParkType;
    /// <summary>
    /// 离场类别
    /// </summary>

    public short m_RemainderCount;
    /// <summary>
    /// 总剩余空位
    /// </summary>

    public short m_RemainderByMonth;
    /// <summary>
    /// 月租长包剩余车位
    /// </summary>

    public short m_RemainderVister;
    /// <summary>
    /// 时租访客剩余车位
    /// </summary>

    public String m_LicensePlate;
    /// <summary>
    /// 车辆号牌
    /// </summary>

    public int m_Duration;
    /// <summary>
    /// 停车时长
    /// </summary>

    public int m_Fee;
    /// <summary>
    /// 收费金额
    /// </summary>


    public byte m_PaymentType;
    /// <summary>
    /// 支付类型
    /// </summary>


    protected void SetData()
    {
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            setM_Data(Addition(ConvertTimeToBytes(sf.parse(m_LeaveTime)),
                    new byte[] { m_ParkType },
                    Addition(TrunTo2Bytes(ToBytes(m_RemainderCount), null),
                            TrunTo2Bytes(ToBytes(m_RemainderByMonth), null),
                            TrunTo2Bytes(ToBytes(m_RemainderVister), null),
                            ConvertStringToBytes(m_LicensePlate, 12),
                            Addition(UnTrunTo4Bytes(CLeaveToBytes(m_Duration), null),
                                    UnTrunTo4Bytes(ToBytes((short)m_Fee), null), new byte[] { (byte)m_PaymentType }, null)
                            ,null),null));

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
    private static byte[] Addition(byte[] a, byte[] b, byte[] c, byte[] returnbyte)
    {
        returnbyte = new byte[a.length + b.length + c.length];
        for (int i = 0; i < a.length; i++)
            returnbyte[i] = a[i];
        for (int i = 0; i < b.length; i++)
            returnbyte[a.length + i] = b[i];
        for (int i = 0; i < c.length; i++)
            returnbyte[a.length + b.length + i] = c[i];
        return returnbyte;
    }
    private static byte[] Addition(byte[] a, byte[] b, byte[] c, byte[] d, byte[] e,   byte[] returnbyte)
    {
        returnbyte = new byte[a.length + b.length + c.length + d.length + e.length  ];
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
        return returnbyte;
    }
    private byte[] UnTrunTo4Bytes(byte[] a, byte[] retuen_a)
    {
        if (a.length < 4)
        {
            retuen_a = new byte[4];
            for (int i = 0; i < a.length; i++)
            {
                retuen_a[4 - a.length+i] = a[i];
            }
            for (int i = 0; i < 4 - a.length; i++)
                retuen_a[i] = 0;
            return retuen_a;
        }
        return a;
    }
    private static byte[] CLeaveToBytes(int _short)
    {
        return GetHexToChs(a10To16(_short), null);
    }
    private static byte[] TrunDESCbyte(byte[] bytes, byte[] DESCbytes)
    {
        DESCbytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++)
        {
            DESCbytes[bytes.length - 1 - i] = bytes[i];
        }
        return DESCbytes;
    }
    private static byte[] GetHexToChs(String hex, byte[] bytes)
    {
        bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++)
        {
            try
            {
                // 每两个字符是一个 byte。 
                bytes[i] = bytes[i] = (byte)(0xff & Integer.parseInt(hex.substring(i*2, i*2+2),16));
            }
            catch(Exception ex) { }
        };
        return bytes;
    }
    public  static String a10To16(int s)
    {
        if (Integer.toHexString(s).toUpperCase().length() % 2 > 0)
            return "0" + Integer.toHexString(s).toUpperCase();
        else
            return Integer.toHexString(s).toUpperCase();
    }
}
