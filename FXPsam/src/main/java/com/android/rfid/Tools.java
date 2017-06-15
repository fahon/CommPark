package com.android.rfid;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {

    //byte 转十六进制
    public static String Bytes2HexString(byte[] b, int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    //十六进制转byte
    public static byte[] HexString2Bytes(String src) {
        int len = src.length() / 2;
        byte[] ret = new byte[len];
        byte[] tmp = src.getBytes();

        for (int i = 0; i < len; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }


    /**
     * 检验接收的数据长度
     *
     * @param dataLen 接收数据的长度
     * @param data    数据
     * @return
     */
    public static boolean checkData(String dataLen, String data) {
        int length = Integer.parseInt(dataLen, 16);
        return length == (data.length() / 2 - 5);
    }


    public static void main(String[] args) {
        String aa = "02000a00000000000000";
        String len = aa.substring(2, 6);
        System.out.println(checkData(len, aa));
    }


    /**
     * 字节数组转int,适合转高位在前低位在后的byte[]
     *
     * @param bytes
     * @return
     */
    public static long byteArrayToLong(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        long result = 0;
        try {
            int len = dis.available();
            if (len == 1) {
                result = dis.readByte();
            } else if (len == 2) {
                result = dis.readShort();
            } else if (len == 4) {
                result = dis.readInt();
            } else if (len == 8) {
                result = dis.readLong();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dis.close();
                bais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * int转byte[]，高位在前低位在后
     *
     * @param value
     * @return
     */
    public static byte[] varIntToByteArray(long value) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        Long l = new Long(value);
        try {
            if (l == l.byteValue()) {
                oos.writeByte(l.byteValue());
            } else if (l == l.shortValue()) {
                oos.writeShort(l.shortValue());
            } else if (l == l.intValue()) {
                oos.writeInt(l.intValue());
            } else if (l == l.longValue()) {
                oos.writeLong(l.longValue());
            } else if (l == l.floatValue()) {
                oos.writeFloat(l.floatValue());
            } else if (l == l.doubleValue()) {
                oos.writeDouble(l.doubleValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }

    /**
     * byte数组转换成16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    /**
     * 比较输入的日期是否在有效期内
     *
     * @param date1
     * @return
     */
    public static int getCompareData(String date1){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date d1 = null;
        try {
            d1 = sdf.parse(date1);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Date d2 = new Date();
        String date2 = sdf.format(d2);//当前年月日
        return d1.compareTo(d2);
    }

    /**
     * 单字节转成String ascii
     * @param suf
     * @return
     */
    public static String getSuf(byte[] suf){
        String nRcvString;
        StringBuffer  tStringBuf=new StringBuffer ();
        char[] tChars=new char[suf.length];

        for(int i=0;i<suf.length;i++) {
            tChars[i] = (char) suf[i];
        }
        tStringBuf.append(tChars);

        nRcvString=tStringBuf.toString();          //nRcvString从tBytes转成了String类型的"123"
        return nRcvString;
    }
}
