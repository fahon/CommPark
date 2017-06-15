package willsong.cn.commpark.activity.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/12 0012.
 */

public abstract class CBaseFrame {
    public static final int STX = 0xAAA5;

    public static int getSTX() {
        return STX;
    }

    private byte[] m_Data = new byte[0];

    private byte m_DataLength = 0;

    private short m_SenderAddressCode;

    private short m_SenderCompanyCode;

    private byte m_Ctl = 0;

    private byte m_SD;

    private byte m_Fun;

    private short m_CRC = 0;

    private byte[] m_SAddr = new byte[4];

    public short getM_CRC() {
        return m_CRC;
    }

    private int m_ReceiverAddressCode;

    public byte[] getM_Data() {
        return m_Data;
    }

    public void setM_Data(byte[] m_Data) {
        setM_DataLength((byte)(m_Data.length & 0xFF));
        this.m_Data = m_Data;
    }

    public byte getM_DataLength() {
        return m_DataLength;
    }


    public void setM_DataLength(byte m_DataLength) {
        this.m_DataLength = m_DataLength;
    }

    protected abstract void SetData();

    public short getM_Id() {
        return m_Id;
    }

    public void setM_Id(short m_Id) {
        this.m_Id = m_Id;
    }

    public short getM_SenderCompanyCode() {
        return m_SenderCompanyCode;
    }

    public void setM_SenderCompanyCode(short m_SenderCompanyCode) {
        this.m_SenderCompanyCode = m_SenderCompanyCode;
    }

    public short getM_SenderAddressCode() {
        return m_SenderAddressCode;
    }

    public void setM_SenderAddressCode(short m_SenderAddressCode) {
        this.m_SenderAddressCode = m_SenderAddressCode;
    }

    public void setM_SD(byte m_SD) {
        this.m_SD = m_SD;
    }

    public void setM_Fun(byte m_Fun) {
        this.m_Fun = m_Fun;
    }

    public byte getM_SD() {
        return m_SD;
    }

    public byte getM_Fun() {
        return m_Fun;
    }

    public static short getCrcSeed() {
        return CRC_SEED;
    }

    public static short getCrcPoly() {
        return CRC_POLY;
    }

    public byte[] getM_SAddr() {
        return m_SAddr;
    }

    public void setM_SAddr(byte[] m_SAddr) {
        this.m_SAddr = m_SAddr;
    }

    public byte getM_Ctl() {
        return m_Ctl;
    }

    public void setM_Ctl(byte m_Ctl) {
        this.m_Ctl = m_Ctl;
    }

    public void CreateControlCode(byte dir, byte fun)
    {
        this.m_SD = dir;
        this.m_Fun = fun;
        this.m_Ctl = (byte)(((byte)(dir << 7) & 0x80) | (byte)(fun  & 0x1F));
    }

    public void setM_CRC(short m_CRC) {
        this.m_CRC = m_CRC;
    }

    public static final short CRC_SEED = 0x0000;

    public static final short CRC_POLY = 0x1021;

    protected void CRC16Check(byte[] data)
    {
        m_CRC = CRC16Check(data, CRC_SEED, CRC_POLY);
    }

    public static short CRC16Check(byte[] data,short seed,short poly)
    {
        short reg_crc = seed, current;
        for (int i = 0; i < data.length; i++)
        {
            current = (short)(data[i] << 8);
            for (int j = 0; j < 8; j++)
            {
                if ((short)(reg_crc ^ current) < 0)
                    reg_crc = (short)((reg_crc << 1) ^ poly);
                else
                    reg_crc <<= 1;
                current <<= 1;
            }
        }
        return reg_crc;
    }

    public static final short ETX = 0xCD;

    public static int getETX() {
        return ETX;
    }

    private short m_Id = 0;

    private byte[] m_RAddr = new byte[4];

    public byte[] getM_RAddr() {
        return m_RAddr;
    }

    public void setM_RAddr(byte[] m_RAddr) {
        this.m_RAddr = m_RAddr;
    }

    public byte[] ToBytes()
    {
        byte[] bzSTX = TrunDESCbyte(ToBytes(getSTX()));
        SetData();
        byte[] bzDataLength = new byte[] { getM_DataLength() };
        byte[] bzId = TrunTo2Bytes(ToBytes(getM_Id()), null);
        byte[] bzCtl = new byte[] { getM_Ctl() };
        //CRC16Check(Addition(bzSTX, bzDataLength, bzId, SenderAddress, ReceiverAddress, bzCtl, Data,null));
        byte[] bzCrc = ToBytes(CRC16Check(Addition(bzSTX, bzDataLength, bzId, m_SAddr, m_RAddr, bzCtl, m_Data, null), CRC_SEED, CRC_POLY));
        //byte[] bzCrc = ToBytes(CRC);
        byte[] bzcrctwo = new byte[2];
        if(bzCrc.length > 2){
            bzcrctwo[0] = bzCrc[0];
            bzcrctwo[1] = bzCrc[1];
            byte[] bzETX = new byte[] { (byte) ETX };
            byte[] ret = Addition(bzSTX, bzDataLength, bzId, m_SAddr, m_RAddr, bzCtl, m_Data, bzcrctwo, bzETX,null);
            return ret;
        }
        if (bzCrc.length != 2)
            return null;
        byte[] bzETX = new byte[] { (byte) ETX };
        byte[] ret = Addition(bzSTX, bzDataLength, bzId, m_SAddr, m_RAddr, bzCtl, m_Data, bzCrc, bzETX,null);

        return ret;
    }

    /// <summary>
    /// 发送方地址
    /// </summary>
    /// <param name="companyCode">
    /// 厂商编码；
    /// 厂商编码用于标识电子收费系统制造厂商，取值范围1-65535，由交通管理部门统一编码。
    /// </param>
    /// <param name="addressCode">
    /// 地址编码；
    /// 地址编码标识电子收费系统的通信地址，取值范围1-65535，由厂商自行定义
    /// </param>
    public void CreateSenderAddress(short companyCode,short addressCode)
    {
        m_SenderCompanyCode = companyCode;
        m_SenderAddressCode = addressCode;
        m_SAddr = Addition(ToBytes(companyCode), ToBytes(addressCode), null);
    }
    private static byte[] Addition(byte[] a, byte[] b, byte[] returnbyte)
    {
        returnbyte = new byte[a.length + b.length];
        for (int i = 0; i < a.length; i++)
            returnbyte[i] = a[i];
        for (int i = 0; i < b.length; i++)
            returnbyte[a.length+i] = b[i];
        return returnbyte;
    }


    public int getM_ReceiverAddressCode() {
        return m_ReceiverAddressCode;
    }

    public void setM_ReceiverAddressCode(int m_ReceiverAddressCode) {
        this.m_ReceiverAddressCode = m_ReceiverAddressCode;
    }

    /// <summary>
    /// 接收方地址
    /// </summary>
    /// <param name="addressCode">
    /// 用于标识数据接收方的身份编码，由交通管理部门统一设置。
    /// </param>
    public void CreateReceiverAddress(int addressCode)
    {
        m_ReceiverAddressCode = addressCode;
        m_RAddr = TrunTo4Bytes(ToBytes((short)addressCode),null);
    }

    public byte[] TrunTo4Bytes(byte[] a, byte[] retuen_a)
    {
        if (a.length <4)
        {
            retuen_a = new byte[4];
            for (int i = 0; i < a.length; i++)
            {
                retuen_a[i] = a[i];
            }
            for (int i = a.length; i < retuen_a.length; i++)
                retuen_a[i] = 0;
            return retuen_a;
        }
        return a;
    }

    private static byte[] Addition(byte[] a, byte[] b, byte[] c, byte[] d, byte[] e, byte[] f, byte[] g, byte[] h, byte[] j, byte[] returnbyte)
    {
        returnbyte = new byte[a.length + b.length + c.length + d.length + e.length + f.length + g.length + h.length + j.length];
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
        for (int i = 0; i < g.length; i++)
            returnbyte[a.length + b.length + c.length + d.length + e.length + f.length + i] = g[i];
        for (int i = 0; i < h.length; i++)
            returnbyte[a.length + b.length + c.length + d.length + e.length + f.length + g.length + i] = h[i];
        for (int i = 0; i < j.length; i++)
            returnbyte[a.length + b.length + c.length + d.length + e.length + f.length + g.length + h.length + i] = j[i];
        return returnbyte;
    }
    private static byte[] Addition(byte[] a, byte[] b, byte[] c, byte[] d, byte[] e, byte[] f, byte[] g, byte[] returnbyte)
    {
        returnbyte = new byte[a.length + b.length + c.length + d.length + e.length + f.length + g.length];
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
        for (int i = 0; i < g.length; i++)
            returnbyte[a.length + b.length + c.length + d.length + e.length + f.length + i] = g[i];
        return returnbyte;
    }
    public byte[] TrunTo2Bytes(byte[] a,byte[] retuen_a)
    {
        if (a.length == 1)
        {
            retuen_a = new byte[2];
            retuen_a[0] = a[0];
            retuen_a[1] = 0;
            return retuen_a;
        }
        return a;
    }

    public static byte[] ToBytes(int _ushort)
    {
        return TrunDESCbyte(hexStringToBytes(a10To16(_ushort)));
    }

    private static byte[] TrunDESCbyte(byte[] bytes) {
        byte[] DESCbytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            DESCbytes[bytes.length - 1 - i] = bytes[i];
        }
        return DESCbytes;
    }


    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static byte[] GetHexToChs(String hex)
    {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            try {

                // 每两个字符是一个 byte。
//                String subHex = hex.substring(i * 2, 2);
//                toStringHex(subHex);
//                bytes = subHex.getBytes("gb2312");
                bytes[i] = (byte)(0xff & Integer.parseInt(hex.substring(i*2, i*2+2),16));
//                bytes[i] = Byte.parseByte(hex.substring(i * 2, 2),10);
 //               String tx = deUnicode(hex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }
    //转16进制
    public  static String a10To16(int s)
    {
        if (Integer.toHexString(s).toUpperCase().length() % 2 > 0)
            return "0" + Integer.toHexString(s).toUpperCase();
        else
            return Integer.toHexString(s).toUpperCase();
    }

    public static byte[] ConvertTimeToBytes(Date dt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        byte year = (byte)((calendar.get(Calendar.YEAR) - 2000) & 0xFF);
        byte month = (byte)(calendar.get(Calendar.MONTH) + 1 & 0xFF);
        byte date = (byte)(calendar.get(Calendar.DATE) & 0xFF);
        byte hour = (byte)(calendar.get(Calendar.HOUR_OF_DAY) & 0xFF);
        byte minute = (byte)(calendar.get(Calendar.MINUTE) & 0xFF);
        byte second = (byte)(calendar.get(Calendar.SECOND) & 0xFF);
        return new byte[]{ year, month, date,hour,minute,second };
    }


//    public static byte[] ConvertTimeToBytes(String dt){
//        try{
//            SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//            Date date =sdf.parse(dt);
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            return new byte[]{ (byte)(calendar.get(Calendar.YEAR) & 0xFF),
//                    (byte)(calendar.get(Calendar.MONTH) & 0xFF),
//                    (byte)(calendar.get(Calendar.DATE) & 0xFF),
//                    (byte)(calendar.get(Calendar.HOUR) & 0xFF),
//                    (byte)(calendar.get(Calendar.MINUTE) & 0xFF),
//                    (byte)(calendar.get(Calendar.SECOND) & 0xFF) };
//        }catch (Exception ex){
//            ex.printStackTrace();
//            return null;
//        }
//    }

    public static byte[] ConvertStringToBytes(String str, int len)
    {
        byte[] ret = new byte[len];
        for (int i = 0; i < len; i++) ret[i] = 0x20;
        for (int i = 0, pos = 0; i < str.length(); i++)
        {
            if (pos < len)
            {
                char item = str.charAt(i);
                byte[] bzItem;
                if (item > 0xFF) {
//               bzItem = System.Text.Encoding.GetEncoding("GBK").GetBytes(new char[] { item });
                    bzItem = getBytes(item,1);
                }
                else {
                    //bzItem = System.Text.Encoding.ASCII.GetBytes(new char[]{item});
                    bzItem = getBytes(item,2);
                }
                for (int j = 0; j < bzItem.length; j++, pos++)
                    if (pos < len) ret[pos] = bzItem[j];
            }
        }
        return ret;
    }
    //计算字符串的长度
    public  int getWordCount(String s)
    {
        //String s = new String(new char[]{'a'},"GBK");
        char[] a = {'a','b'};
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        int length = s.length();
        return length;
    }

    /**
     * 字符转成byte
     * @param chars
     * @param number
     * @return
     */
    private static byte[] getBytes (char chars,int number) {
        Charset cs;
        if(1 == number){
             cs= Charset.forName ("GBK");
        }else {
            cs = Charset.forName ("ASCII");
        }
        CharBuffer cb = CharBuffer.allocate(chars);
        cb.put (chars);
        cb.flip ();
        ByteBuffer bb = cs.encode (cb);
        return bb.array();
    }

    public static String toStringHex(String s)
    {
        byte[] baKeyword = new
                byte[s.length()/2];
        for(int i = 0; i < baKeyword.length; i++)
        {
            try
            {
                baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            s = new String(baKeyword, "utf-8");//UTF-16le:Not
        }
        catch (Exception e1)
        {

            e1.printStackTrace();
        }
        return s;
    }
}
