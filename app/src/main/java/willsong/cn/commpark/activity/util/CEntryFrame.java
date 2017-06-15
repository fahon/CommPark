package willsong.cn.commpark.activity.util;

import java.text.SimpleDateFormat;

import willsong.cn.commpark.activity.Bean.FunctionCode;
import willsong.cn.commpark.activity.Bean.StreamDirection;

/**
 * Created by Administrator on 2016/10/19 0019.
 * 进场
 */

public class CEntryFrame extends CBaseFrame{
    public CEntryFrame() {
        
    }

    public CEntryFrame(String et, byte pt, short remainderTotal, short remainderByMonth, short remainderVister, String licPlate)
    {
        CreateControlCode(StreamDirection.Upload, FunctionCode.Entry);
        m_m_EntryTime = et;
        m_ParkType = pt;
        m_RemainderCount = remainderTotal;
        m_RemainderByMonth = remainderByMonth;
        m_RemainderVister = remainderVister;
        m_LicensePlate = licPlate;
    }

    public static int Framelength()
    {
        return 2 + 1 + 2 + 4 + 4 + 1 + 2 + 1 + 6 + 1 + 6 + 12; 
    }

    public String m_m_EntryTime;
    /// <summary>
    /// 进场时间
    /// </summary>

    public byte m_ParkType;
    /// <summary>
    /// 进场类别
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
   

    protected void SetData()
    {
//        Data = Addition(ConvertTimeToBytes(m_EntryTime),
//                new byte[] { (byte)ParkingType },
//                Addition(TrunTo2Bytes(ToBytes(RemainderCount), null),
//                TrunTo2Bytes(ToBytes(RemainderByMonth), null),
//                TrunTo2Bytes(ToBytes(RemainderVister), null),
//                ConvertStringToBytes(LicensePlate, 12),null),null);
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            setM_Data(Addition(ConvertTimeToBytes(sf.parse(m_m_EntryTime)),
                    new byte[] { m_ParkType },
                    Addition(TrunTo2Bytes(ToBytes(m_RemainderCount), null),
                            TrunTo2Bytes(ToBytes(m_RemainderByMonth), null),
                            TrunTo2Bytes(ToBytes(m_RemainderVister), null),
                            ConvertStringToBytes(m_LicensePlate, 12),null),null));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static byte[] Addition(byte[] a, byte[] b, byte[] c,  byte[] returnbyte)
    {
        returnbyte = new byte[a.length + b.length + c.length  ];
        for (int i = 0; i < a.length; i++)
            returnbyte[i] = a[i];
        for (int i = 0; i < b.length; i++)
            returnbyte[a.length + i] = b[i];
        for (int i = 0; i < c.length; i++)
            returnbyte[a.length + b.length + i] = c[i];
        return returnbyte;
    }
    private static byte[] Addition(byte[] a, byte[] b, byte[] c, byte[] d, byte[] returnbyte)
    {
        returnbyte = new byte[a.length + b.length + c.length + d.length];
        for (int i = 0; i < a.length; i++)
            returnbyte[i] = a[i];
        for (int i = 0; i < b.length; i++)
            returnbyte[a.length + i] = b[i];
        for (int i = 0; i < c.length; i++)
            returnbyte[a.length + b.length + i] = c[i];
        for (int i = 0; i < d.length; i++)
            returnbyte[a.length + b.length + c.length + i] = d[i];
        return returnbyte;
    }
}
