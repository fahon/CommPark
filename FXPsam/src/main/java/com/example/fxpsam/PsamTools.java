package com.example.fxpsam;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.rfid.Tools;
import com.fxpsam.nativeJni.RfidNative;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.android.rfid.Tools.Bytes2HexString;
import static com.android.rfid.Tools.bytesToHexString;
import static com.android.rfid.Tools.getCompareData;

/**
 * Created by WillSong on 16/9/25.
 */

public class PsamTools {
    private static String Tag = "PsamTools";
    /**
     * 公交卡支付
     *
     * @param transAmount 扣款金额 分
     * @return card_info  debit
     */
    public static Map<String, byte[]> charge(int transAmount, Context mContext, int TransSerialNo) {
        Map<String, byte[]> resultMap = new HashMap<>();
            /*
            //初始化
			rfid = new RfidNative() ;
			ret = rfid.open(port, baudrate) ;
			if(ret >= 0){
				isOpen = true ;
				addTips("open RFID success!! \n") ;
				//未打开设备时，功能按钮不可用
				setButtonClickable(btnCloseRfid, true ) ;
				setButtonClickable(btnPsamReset, true ) ;
				setButtonClickable(btnPsamClose, true ) ;
				setButtonClickable(bntPsamApdu08, true ) ;
				setButtonClickable(btnReadReg, true ) ;
				setButtonClickable(btnWriteReg, true ) ;
				setButtonClickable(btnOpenRfid, false ) ;
			}else{
				addTips("open RFID fail!! \n") ;
			}*/
        //
        int ret2;

        int i;
        byte[] inbuf = new byte[256];
        byte[] outbuf = new byte[256];
//        int TransAmount;
        int TransSerialNos = 1;
        long CardLastRemain;
        int CardCnt;
        String mCardSurfaceNo;
        String cardPhysicalNo;
        byte CardType;
        byte CardKind;
        byte[] CardSnr = new byte[5];  //序列号
        byte[] TransTime = new byte[7];
        byte[] CityCode = new byte[2]; //城市ID
        byte[] POSID = new byte[6];    //psam号

        //
        RfidNative rfidNative = new RfidNative();
        //Open Port
        int ret0 = rfidNative.open(14, 115200);
        if (ret0 < 0) {
            LogE(Tag, "Err:rfidNative.open\n");
        } else {
            LogE(Tag, "OK:rfidNative.open\n");
        }
        //RFidPowerOn
        ret0 = rfidNative.rfidpoweron(); //打开射频电源
        if (ret0 != 0) {
            LogE(Tag, "Err:rfidNative.rfidpoweron\n");
        } else {
            LogE(Tag, "OK:rfidNative.rfidpoweron\n");
        }
        //get Version
        byte[] Version = new byte[64];
        Arrays.fill(Version, (byte) 0);
        rfidNative.sptcreaderapigetver(Version); //获取动态库版本号
        LogE(Tag, "Get Version: " + Version.toString() + "\n");

        //Psam Init
        byte[] PSamNo = new byte[8];
        Arrays.fill(PSamNo, (byte) 0);
        int ret = rfidNative.sptcreaderapipsaminit(2, PSamNo); //PSAM卡初始化
        if (ret == 0) {
            //memcpy(POSID,PSamNo,6);
            for (i = 0; i < 6; i++) POSID[i] = PSamNo[i];
        }
        String psam = Tools.bytesToHexString(POSID);
        resultMap.put("pasm", POSID);//pasm号
        //
        for (int j = 0; j < 3; j++) {
            //Get Card Info
            byte[] outPut = new byte[64];
            Arrays.fill(outPut, (byte) 0);
            ret = rfidNative.sptcreaderapigetcardinfo(outPut);//读取上海公交卡信息
            if (ret != 0) {
                //=====================
                getErrorMsg(mContext, ret);
                LogE(Tag, "Err:sptc_reader_api_get_card_info: " + ret + "\n");
                ret = rfidNative.sptcreaderapigetdebugstep(); //取内部调试记录标志
                LogE(Tag, "Ok:sptc_reader_api_get_debug_step :" + ret + "\n");
                continue;
            } else {
                LogE(Tag, "OK:sptc_reader_api_get_card_info\n");
                LogE(Tag, "CardInfo = 0x" + Bytes2HexString(outPut, 48) + "\n");
                resultMap.put("card_info", outPut);//读取到的公交卡后的输出参数
                //for(i=0;i<24;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
                CardType = outPut[0];     //卡类型
                CardSnr[0] = outPut[1];   //序列号
                CardSnr[1] = outPut[2];
                CardSnr[2] = outPut[3];
                CardSnr[3] = outPut[4];
                CardCnt = outPut[16] * 256 + outPut[17];  //卡计数器
                CityCode[0] = outPut[5];
                CityCode[1] = outPut[6];                  //城市ID
                CardKind = outPut[7];                    //交通卡类型
                long k = (outPut[12] & 0xFF) * 256 * 256;
                long f = (outPut[13] & 0xFF) * 256;
                long d = outPut[14] & 0xFF;
                CardLastRemain = (k + f + d); //交易前余额
//                Long money = Tools.byteArrayToLong(test);
                Log.i("ccm", CardLastRemain + "======money:");

                byte[] cardSurfaceNum = new byte[11];  //卡表面号
                System.arraycopy(outPut, 24, cardSurfaceNum, 0, 11);
                mCardSurfaceNo = Tools.getSuf(cardSurfaceNum);//卡面号
            }

            if (transAmount > CardLastRemain) {
                Toast.makeText(mContext, "余额不足", Toast.LENGTH_SHORT).show();
                break;
            }
            //=============================================
            byte[] EXPDate = new byte[4]; //有效期, YYYYMMDD, BCD 码
            System.arraycopy(outPut, 8, EXPDate, 0, 4);
            String mEXPDate = bytesToHexString(EXPDate);//卡物理值
            if (getCompareData(mEXPDate) == -1) {
                Toast.makeText(mContext, "该卡已过有效期", Toast.LENGTH_SHORT).show();
                break;
            }
            if ((CardLastRemain / 100) > 1000) {
//                Toast.makeText(mContext, "余额超过1000元,为可疑卡", Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "无效卡", Toast.LENGTH_SHORT).show();
                break;
            }
            //=============================================
            //read last record
            Arrays.fill(outbuf, (byte) 0);
            ret = rfidNative.sptcreaderapigetlastrecord(outbuf);//读取最后一笔交易记录
            if (ret != 0) {
                LogE(Tag, "Err:sptc_reader_api_get_last_record,ret=" + ret + "\n");
                continue;
            } else {
                LogE(Tag, "OK:sptc_reader_api_get_last_record:");
                //for(i=0;i<19;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
                LogE(Tag, "Record = 0x" + Bytes2HexString(outbuf, 19) + "\n");
            }
            //debit
            Calendar ca = Calendar.getInstance();
            int st_wYear = ca.get(Calendar.YEAR);//获取年份
            int st_wMonth = ca.get(Calendar.MONTH) + 1;//获取月份
            int st_wDay = ca.get(Calendar.DATE);//获取日
            int st_wHour = ca.get(Calendar.HOUR_OF_DAY);//小时:Calendar.HOUR_OF_DAY(24小时制)Calendar.HOUR（12小时制）
            int st_wMinute = ca.get(Calendar.MINUTE);//分
            int st_wSecond = ca.get(Calendar.SECOND);//秒
            //sprintf(inbuf,"%04d%02d%02d%02d%02d%02d",st_wYear,st_wMonth,st_wDay,st_wHour,st_wMinute,st_wSecond);
            //ascstr2bcdstr((UBYTE *)inbuf,(UBYTE *)inbuf,14);
            //memcpy(TransTime,inbuf,7);
            byte[] bytTemp = new byte[7];
            bytTemp[0] = (byte) (st_wYear / 100);
            bytTemp[1] = (byte) (st_wYear % 100);
            bytTemp[2] = (byte) (st_wMonth);
            bytTemp[3] = (byte) (st_wDay);
            bytTemp[4] = (byte) (st_wHour);
            bytTemp[5] = (byte) (st_wMinute);
            bytTemp[6] = (byte) (st_wSecond);
            byte high, low;
            for (i = 0; i < 7; i++) {
                high = (byte) (bytTemp[i] / 10);
                low = (byte) (bytTemp[i] % 10);
                inbuf[i] = (byte) (high * 16 + low);
                TransTime[i] = inbuf[i];
            }
//            TransSerialNo++;
//            udword_to_buf3(TransSerialNo,&inbuf[7]);
            inbuf[7] = (byte) ((TransSerialNo >> 16) & 0xff);
            inbuf[8] = (byte) ((TransSerialNo >> 8) & 0xff);
            inbuf[9] = (byte) (TransSerialNo & 0xff);//交易流水号
            //udword_to_buf3(TransAmount,&inbuf[10]);
            inbuf[10] = (byte) ((transAmount >> 16) & 0xff);
            inbuf[11] = (byte) ((transAmount >> 8) & 0xff);
            inbuf[12] = (byte) (transAmount & 0xff); //交易金额

            //memset(outbuf,0,sizeof(outbuf));
            Arrays.fill(outbuf, (byte) 0);
            byte[] outBufCar = new byte[24];
            ret = rfidNative.sptcreaderapidebit(inbuf, outBufCar); //开始交易
            if (ret != 0) {
//                LogE(Tag, "Err:sptc_read_api_debit,ret = " + ret + "\n");
//                //=====================
//                getErrorMsg(mContext,ret);
                LogE(Tag, "Err:sptc_read_api_debit,ret = " + ret + "\n");
                ret2 = rfidNative.sptcreaderapigetdebugstep();
                LogE(Tag, "Ok:sptc_reader_api_get_debug_step :" + ret2 + "\n");
                //若交易不成功，则需要重新读卡，判断是否已扣款若已扣款，则发取CPU卡交易认证码或者计算M1卡交易认证码指令
                if (ret == 0x17) {
                    byte[] mOutPut = new byte[64];//新读取公交卡数据保存参数
                    Arrays.fill(mOutPut, (byte) 0);
                    int mret = rfidNative.sptcreaderapigetcardinfo(mOutPut);//重新读取上海公交卡信息
                    if (mret != 0) {
                        //=====================
                        getErrorMsg(mContext, mret);
                        continue;
                    } else {
                        LogE(Tag, "OK:sptc_reader_api_get_card_info2\n");
                        LogE(Tag, "CardInfo2 = 0x" + Bytes2HexString(mOutPut, 48) + "\n");

                        int cardCount = mOutPut[16] * 256 + mOutPut[17];//卡计数器
                        int cardMoney = mOutPut[12] * 256 * 256 + mOutPut[13] * 256 + mOutPut[14];//卡上金额
                        byte[] cardRemainMoney = new byte[3];//卡上金额,byte类型
                        cardRemainMoney[0] = mOutPut[12];
                        cardRemainMoney[1] = mOutPut[13];
                        cardRemainMoney[2] = mOutPut[14];
                        byte[] cardSurfaceNumber = new byte[11];  //卡表面号
                        System.arraycopy(mOutPut, 24, cardSurfaceNumber, 0, 11);
                        String cardSurfaceNo = Tools.getSuf(cardSurfaceNumber);//卡面号
                        if (cardCount > CardCnt && cardMoney < CardLastRemain && cardSurfaceNo.equals(mCardSurfaceNo)) {//确认是否扣款成功:卡号相同+金额已扣+卡计数器已加1
                            if (CardType == 0x01) {//CPU卡
                                //CPU,get transaction prove
                                Arrays.fill(inbuf, (byte) 0);
                                //udword_to_buf3(TransSerialNo,inbuf);
                                inbuf[0] = (byte) ((TransSerialNo >> 16) & 0xff);
                                inbuf[1] = (byte) ((TransSerialNo >> 8) & 0xff);
                                inbuf[2] = (byte) (TransSerialNo & 0xff);//交易流水号;
                                //memcpy(&inbuf[3],CardSnr,4);
                                inbuf[3] = CardSnr[0];
                                inbuf[4] = CardSnr[1];
                                inbuf[5] = CardSnr[2];
                                inbuf[6] = CardSnr[3];
                                //uword_to_buf(CardCnt,&inbuf[7]);
                                inbuf[7] = (byte) (CardCnt / 256);
                                inbuf[8] = (byte) (CardCnt % 256);

                                //memset(outbuf,0,sizeof(outbuf));
                                Arrays.fill(outbuf, (byte) 0);
                                ret = rfidNative.sptcreaderapicpugettransactionprove(inbuf, outbuf);
                                if (ret != 0) {
                                    LogE(Tag, "Err:sptc_reader_api_cpu_get_transaction_prove,ret = " + ret + "\n");
                                    ret = rfidNative.sptcreaderapigetdebugstep();
                                    LogE(Tag, "Ok:sptc_reader_api_get_debug_step :" + ret + "\n");
                                    continue;
                                } else {
                                    LogE(Tag, "OK:sptc_reader_api_cpu_get_transaction_prove:");
                                    //for(i=0;i<4;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
                                    LogE(Tag, "TransProve = 0x" + Tools.Bytes2HexString(outbuf, 4) + "\n");
                                    resultMap.put("inbuf", inbuf);//扣款时的输入参数
                                    resultMap.put("TransTime", TransTime);//交易时间
                                    resultMap.put("TAC", outbuf);//匹配交易的交易认证码
                                    resultMap.put("CardRemainMoney", cardRemainMoney);//交易后钱包余额，分

                                    break;
                                }
                            } else {//M1卡
                                //M1,Get Card Info again
//                                Arrays.fill(outPut,(byte)0);
//                                ret = rfidNative.sptcreaderapigetcardinfo(outPut);
//                                if(ret != 0) {
//                                    LogE(Tag, "Err:sptc_reader_api_get_card_info: "+ret+"\n");
//                                    ret = rfidNative.sptcreaderapigetdebugstep();
//                                    LogE(Tag, "Ok:sptc_reader_api_get_debug_step :"+ret+"\n");
//                                    continue;
//                                }else {
//                                    LogE(Tag, "OK:sptc_reader_api_get_card_info\n");
//                                    LogE(Tag, "CardInfo = 0x" + Tools.Bytes2HexString(outPut, 35) + "\n")  ;
                                //for(i=0;i<35;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
                                Arrays.fill(inbuf, (byte) 0);
                                //udword_to_buf3(TransSerialNo,inbuf);
                                inbuf[0] = (byte) ((TransSerialNo >> 16) & 0xff);
                                inbuf[1] = (byte) ((TransSerialNo >> 8) & 0xff);
                                inbuf[2] = (byte) (TransSerialNo & 0xff);//交易流水号;
                                //memcpy(&inbuf[3],CityCode,2);
                                inbuf[3] = CityCode[0];
                                inbuf[4] = CityCode[1];
                                //memcpy(&inbuf[5],CardSnr,4);
                                for (i = 0; i < 4; i++) inbuf[5 + i] = CardSnr[i];
                                inbuf[9] = CardKind;
                                //udword_to_buf(CardLastRemain,&inbuf[10]);
                                int tp = (int) CardLastRemain;
                                inbuf[10] = (byte) (tp / (256 * 256 * 256));
                                tp = tp % (256 * 256 * 256);
                                inbuf[11] = (byte) (tp / (256 * 256));
                                tp = tp % (256 * 256);
                                inbuf[12] = (byte) (tp / (256));
                                tp = tp % (256);
                                inbuf[13] = (byte) tp;
                                //udword_to_buf(TransAmount,&inbuf[14]);
                                tp = transAmount;
                                inbuf[14] = (byte) (tp / (256 * 256 * 256));
                                tp = tp % (256 * 256 * 256);
                                inbuf[15] = (byte) (tp / (256 * 256));
                                tp = tp % (256 * 256);
                                inbuf[16] = (byte) (tp / (256));
                                tp = tp % (256);
                                inbuf[17] = (byte) tp;
                                //memcpy(&inbuf[18],TransTime,7);
                                for (i = 0; i < 7; i++) inbuf[18 + i] = TransTime[i];
                                //uword_to_buf(CardCnt,&inbuf[25]);
                                inbuf[25] = (byte) (CardCnt / 256);
                                inbuf[26] = (byte) (CardCnt % 256);
                                //memcpy(&inbuf[27],&POSID[2],4);
                                for (i = 0; i < 4; i++) inbuf[27 + i] = POSID[2 + i];

                                //memset(outbuf,0,sizeof(outbuf));
                                Arrays.fill(outbuf, (byte) 0);
                                ret = rfidNative.sptcreaderapim1calctac(inbuf, outbuf); //计算M1卡交易认证码
                                if (ret != 0) {
                                    LogE(Tag, "Err:sptc_reader_api_m1_calc_tac,ret = " + ret + "\n");
                                    continue;
                                } else {
                                    LogE(Tag, "OK:sptc_reader_api_m1_calc_tac:");
                                    //for(i=0;i<4;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
                                    LogE(Tag, "CalTAC = 0x" + Bytes2HexString(outbuf, 4) + "\n");
                                    resultMap.put("inbuf", inbuf);//扣款时的输入参数
                                    resultMap.put("TransTime", TransTime);//交易时间
                                    resultMap.put("TAC", outbuf);//匹配交易的交易认证码
                                    resultMap.put("CardRemainMoney", cardRemainMoney);//交易后钱包余额，分

                                    break;
                                }
                            }
                        } else {
                            continue;
                        }
                    }
                } else {
                    //=====================
                    getErrorMsg(mContext, ret);
                    continue;
                }
            } else {
                LogE(Tag, "OK:sptc_read_api_debit:");
                //for(i=0;i<9;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
                LogE(Tag, "Debit = 0x" + Bytes2HexString(outbuf, 9) + "\n");
//                resultMap.put("debit",outPut); //卡信息（读取公交卡后输出参数）
                resultMap.put("car_buf", outBufCar);//扣款后输出参数【取交易认证码+交易计数器+交易后卡余额】
                LogE(Tag, "Debit = car_buf" + Bytes2HexString(outBufCar, 9) + "\n");
                resultMap.put("inbuf", inbuf);//扣款时的输入参数
                resultMap.put("TransTime", TransTime);//交易时间
            }
//            //若交易不成功，则需要重新读卡，判断是否已扣款若已扣款，则发取CPU卡交易认证码或者计算M1卡交易认证码指令
//            if (CardType == 0x01) {
//                Arrays.fill(inbuf, (byte) 0);
//                //udword_to_buf3(TransSerialNo,inbuf);
//                inbuf[0] = (byte)((TransSerialNo >> 16) & 0xff);
//                inbuf[1] = (byte)((TransSerialNo >> 8) & 0xff);
//                inbuf[2] = (byte) (TransSerialNo & 0xff);//交易流水号;
//                //memcpy(&inbuf[3],CardSnr,4);
//                inbuf[3] = CardSnr[0];
//                inbuf[4] = CardSnr[1];
//                inbuf[5] = CardSnr[2];
//                inbuf[6] = CardSnr[3];
//                //uword_to_buf(CardCnt,&inbuf[7]);
//                inbuf[7] = (byte) (CardCnt / 256);
//                inbuf[8] = (byte) (CardCnt % 256);
//
//                //memset(outbuf,0,sizeof(outbuf));
//                Arrays.fill(outbuf, (byte) 0);
//                ret = rfidNative.sptcreaderapicpugettransactionprove(inbuf, outbuf);//取CPU卡交易认证码
//                if (ret != 0) {
//                    LogE(Tag, "Err:sptc_reader_api_cpu_get_transaction_prove,ret = " + ret + "\n");
//                    continue;
//                } else {
//                    LogE(Tag, "OK:sptc_reader_api_cpu_get_transaction_prove:");
//                    //for(i=0;i<4;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
//                    LogE(Tag, "TransProve = 0x" + Bytes2HexString(outbuf, 4) + "\n");
//
//                }
//            } else {
//                Arrays.fill(inbuf, (byte) 0);
//                //udword_to_buf3(TransSerialNo,inbuf);
//                inbuf[0] = (byte)((TransSerialNo >> 16) & 0xff);
//                inbuf[1] = (byte)((TransSerialNo >> 8) & 0xff);
//                inbuf[2] = (byte) (TransSerialNo & 0xff);//交易流水号;
//                //memcpy(&inbuf[3],CityCode,2);
//                inbuf[3] = CityCode[0];
//                inbuf[4] = CityCode[1];
//                //memcpy(&inbuf[5],CardSnr,4);
//                for (i = 0; i < 4; i++) inbuf[5 + i] = CardSnr[i];
//                inbuf[9] = CardKind;
//                //udword_to_buf(CardLastRemain,&inbuf[10]);
//                int tp = (int) CardLastRemain;
//                inbuf[10] = (byte) (tp / (256 * 256 * 256));
//                tp = tp % (256 * 256 * 256);
//                inbuf[11] = (byte) (tp / (256 * 256));
//                tp = tp % (256 * 256);
//                inbuf[12] = (byte) (tp / (256));
//                tp = tp % (256);
//                inbuf[13] = (byte) tp;
//                //udword_to_buf(TransAmount,&inbuf[14]);
//                tp = transAmount;
//                inbuf[14] = (byte) (tp / (256 * 256 * 256));
//                tp = tp % (256 * 256 * 256);
//                inbuf[15] = (byte) (tp / (256 * 256));
//                tp = tp % (256 * 256);
//                inbuf[16] = (byte) (tp / (256));
//                tp = tp % (256);
//                inbuf[17] = (byte) tp;
//                //memcpy(&inbuf[18],TransTime,7);
//                for (i = 0; i < 7; i++) inbuf[18 + i] = TransTime[i];
//                //uword_to_buf(CardCnt,&inbuf[25]);
//                inbuf[25] = (byte) (CardCnt / 256);
//                inbuf[26] = (byte) (CardCnt % 256);
//                //memcpy(&inbuf[27],&POSID[2],4);
//                for (i = 0; i < 4; i++) inbuf[27 + i] = POSID[2 + i];
//
//                //memset(outbuf,0,sizeof(outbuf));
//                Arrays.fill(outbuf, (byte) 0);
//                ret = rfidNative.sptcreaderapim1calctac(inbuf, outbuf); //计算M1卡交易认证码
//                if (ret != 0) {
//                    LogE(Tag, "Err:sptc_reader_api_m1_calc_tac,ret = " + ret + "\n");
//                    continue;
//                } else {
//                    LogE(Tag, "OK:sptc_reader_api_m1_calc_tac:");
//                    //for(i=0;i<4;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
//                    LogE(Tag, "CalTAC = 0x" + Bytes2HexString(outbuf, 4) + "\n");
//
//                }
//            }
            break;
        }
        //RFidPowerOff
        ret = rfidNative.rfidpoweroff();
        if (ret != 0) {
            LogE(Tag, "Err:rfidNative.rfidpoweroff\n");
        } else {
            LogE(Tag, "OK:rfidNative.rfidpoweroff\n");
        }
        //Close Port
        ret = rfidNative.close(14);
        if (ret < 0) {
            LogE(Tag, "Err:rfidNative.close\n");
        } else {
            LogE(Tag, "OK:rfidNative.close\n");
        }

        saveCrashInfo2File(mlog);
        mlog = "";
        return resultMap;
    }

    //Log信息打印
    static String mlog = "";

    private static void LogE(String tag, String info) {
        Log.e(tag, info);
        mlog = mlog + info;
    }

    /**
     * 用于格式化日期,作为日志文件名的一部分
     */
    private static SimpleDateFormat format = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss");

    private static void saveCrashInfo2File(String ex) {
        // 保存文件
        long timetamp = System.currentTimeMillis();
        String time = format.format(new Date());
        String fileName = "crash-" + time + "-" + timetamp + ".log";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File dir = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + "xxxx_rash");
                if (!dir.exists())
                    dir.mkdir();
                FileOutputStream fos = new FileOutputStream(new File(dir,
                        fileName));
                fos.write(ex.toString().getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //读卡或扣款错误时，返回的应答代码提示
    private static void getErrorMsg(Context context, int ret) {
        if (ret == 1) {
            Toast.makeText(context, "校验错误", Toast.LENGTH_SHORT).show();
        } else if (ret == 2) {
            Toast.makeText(context, "无效命令", Toast.LENGTH_SHORT).show();
        } else if (ret == 3) {
            Toast.makeText(context, "输入参数错", Toast.LENGTH_SHORT).show();
        } else if (ret == 8) {
            Toast.makeText(context, "无卡", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x10) {
            Toast.makeText(context, "认证失败", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x11) {
            Toast.makeText(context, "读卡故障", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x12) {
            Toast.makeText(context, "写卡故障", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x13) {
            Toast.makeText(context, "操作过程中卡移动", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x14) {
            Toast.makeText(context, "无效卡", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x15) {
            Toast.makeText(context, "止付卡", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x16) {
            Toast.makeText(context, "无交易记录", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x17) {
            Toast.makeText(context, "交易时清备份失败,必须重新读卡", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x19) {
            Toast.makeText(context, "无交通卡SAM卡", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x1B) {
            Toast.makeText(context, "PSAM卡故障", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x20) {
            Toast.makeText(context, "MAC2错误", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x24) {
            Toast.makeText(context, "卡上无此记录", Toast.LENGTH_SHORT).show();
        } else if (ret == 0x25) {
            Toast.makeText(context, "MAC1错误", Toast.LENGTH_SHORT).show();
        }
    }

    //获取卡号【判断黑名单卡】
    public static HashMap<String,String> getCart(Context context){
        byte[] inbuf = new byte[256];
        byte[] outbuf = new byte[256];
        byte[] POSID = new byte[6];
        int i;
        HashMap<String,String> map = new HashMap<>();
        RfidNative rfidNative = new RfidNative();
        //Open Port
        int ret0 = rfidNative.open(14, 115200);
        if (ret0 < 0) {
            LogE(Tag, "Err:rfidNative.open\n");
        } else {
            LogE(Tag, "OK:rfidNative.open\n");
        }
        //RFidPowerOn  打开射频电源
        ret0 = rfidNative.rfidpoweron();
        if (ret0 != 0) {
            LogE(Tag, "Err:rfidNative.rfidpoweron\n");
        } else {
            LogE(Tag, "OK:rfidNative.rfidpoweron\n");
        }
        //get Version   获取动态库版本号
        byte[] Version = new byte[64];
        Arrays.fill(Version, (byte) 0);
        rfidNative.sptcreaderapigetver(Version);
        LogE(Tag, "Get Version: " + Version.toString() + "\n");

        /*
        PSAM卡初始化,slot为1或2, 返回PSAM卡号,6字节hex。返回: 0 成功, !0 失败
        int sptc_reader_api_psam_init()
        */
        byte[] PSamNo = new byte[8];
        Arrays.fill(PSamNo, (byte) 0);
        int ret = rfidNative.sptcreaderapipsaminit(2, PSamNo);
        if (ret == 0) {
            //memcpy(POSID,PSamNo,6);
            for (i = 0; i < 6; i++) POSID[i] = PSamNo[i];
            //map.put("PSAMCARD",String.valueOf(POSID));
        }
        for (int j = 0;j < 3;j++) {
            byte[] outPut = new byte[64];
            byte[] cardPhysicalNum = new byte[4];  //卡物理值
            Arrays.fill(outPut, (byte) 0);
            ret = rfidNative.sptcreaderapigetcardinfo(outPut); //读取公交卡信息
            if (ret != 0) {
                getErrorMsg(context,ret);
                LogE(Tag, "Err:sptc_reader_api_get_card_info: " + ret + "\n");
                ret = rfidNative.sptcreaderapigetdebugstep();
                LogE(Tag, "Ok:sptc_reader_api_get_debug_step :" + ret + "\n");
                continue;
            } else {
                LogE(Tag, "OK:sptc_reader_api_get_card_info\n");
                LogE(Tag, "CardInfo = 0x" + Bytes2HexString(outPut, 48) + "\n");
                System.arraycopy(outPut, 1, cardPhysicalNum, 0, 4);
                String cardPhysicalNo = Tools.bytesToHexString(cardPhysicalNum);//卡物理值
                map.put("cardPhysicalNo",cardPhysicalNo);   //卡物理值
            }
            break;
        }
        ret = rfidNative.rfidpoweroff();
        if (ret != 0) {
            LogE(Tag, "Err:rfidNative.rfidpoweroff\n");
        } else {
            LogE(Tag, "OK:rfidNative.rfidpoweroff\n");
        }
        //Close Port
        ret = rfidNative.close(14);
        if (ret < 0) {
            LogE(Tag, "Err:rfidNative.close\n");
        } else {
            LogE(Tag, "OK:rfidNative.close\n");
        }
        return map;
    }
}


