package com.example.fxpsam;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.*;
import java.text.*;

import com.android.rfid.Tools;
import com.fxpsam.nativeJni.RfidNative;

public class MainActivity extends Activity implements OnClickListener{

//	private RfidManager rfid ;
	private RfidNative rfid ;
	private int port = 14 ;  //串口14
	private int baudrate = 115200 ;//波特率115200
	
	private Button btnOpenRfid ;  //RFID读写器打开
	private Button btnCloseRfid ; //RFID读写器关闭
	private Button btnClear ;  //清空
	private Button btnPsamReset ; //psam上电复位
	private Button btnPsamClose ;  //psam下电关闭
	private Button bntPsamApdu08 ;//取随机数08的APDU指令
	private EditText editTips ;   //显示提示
	
	private Button btnReadReg ;  //读寄存器
	private Button btnWriteReg ;  //写寄存器
	private EditText editAddr ; //地址
	private EditText editWriteValue ; //写入值
	
	private Spinner spinnerPsam ;//psam卡座选择
	
	private String[] psamStrArray = {"sam1", "sam2"} ;
	private int psamCard = 1; 
	
	private String Tag = "MainActivity ";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//初始化UI
		initView() ;
	}
	
	private void initView(){
		btnOpenRfid = (Button) findViewById(R.id.button_open_rfid) ;  //RFID读写器打开
		btnCloseRfid = (Button) findViewById(R.id.button_close_rfid); //RFID读写器关闭
		btnClear = (Button) findViewById(R.id.button_clear);  //清空
		btnPsamReset = (Button) findViewById(R.id.button_psam_reset); //psam上电复位
		btnPsamClose = (Button) findViewById(R.id.button_psam_close);  //psam下电关闭
		bntPsamApdu08 = (Button) findViewById(R.id.button_08);//取随机数08的APDU指令
		editTips = (EditText) findViewById(R.id.editText_tips ) ;
		spinnerPsam = (Spinner) findViewById(R.id.spinner_psam) ;
		btnReadReg =  (Button) findViewById(R.id.button_read_reg); 
		btnWriteReg =  (Button) findViewById(R.id.button_write_reg);
		editAddr = (EditText) findViewById(R.id.edit_read_reg ) ;
		editWriteValue = (EditText) findViewById(R.id.edit_write_reg ) ;
		
		editTips = (EditText) findViewById(R.id.editText_tips ) ;
		spinnerPsam.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item,
				psamStrArray));
		spinnerPsam.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long id) {
				if(position == 0){
					psamCard = 1 ; //psam1
				}else if(position == 1){
					psamCard = 2 ; //psam2
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		btnOpenRfid.setOnClickListener(this) ;
		btnCloseRfid.setOnClickListener(this) ;
		btnClear.setOnClickListener(this) ;
		btnPsamReset.setOnClickListener(this) ;
		btnPsamClose.setOnClickListener(this) ;
		bntPsamApdu08.setOnClickListener(this) ;
		btnReadReg.setOnClickListener(this) ;
		btnWriteReg.setOnClickListener(this); 
		
		//未打开设备时，功能按钮不可用
		setButtonClickable(btnCloseRfid, false ) ;
		setButtonClickable(btnPsamReset, false ) ;
		setButtonClickable(btnPsamClose, false ) ;
		setButtonClickable(bntPsamApdu08, false ) ;
		setButtonClickable(btnReadReg, false ) ;
		setButtonClickable(btnWriteReg, false ) ;

	}
	
	//延时
	private void sleepT(long time){
		try {
			Thread.sleep(time) ;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Log信息打印
	private void LogE(String tag, String info ){
		Log.e(tag, info) ;
	}

	
	//添加提示信息
	private void addTips(String tips){
		if(editTips.getText().toString().length() > 2000){
			editTips.setText("") ;
		}
		editTips.append(tips) ;
	}
	
	//设置按钮是否可按
	private void setButtonClickable(Button btn, boolean clickable){
		btn.setClickable(clickable) ;
		if(clickable){
			btn.setTextColor(Color.BLACK) ;
		}else{
			btn.setTextColor(Color.GRAY) ;
		}
	}

	private boolean isOpen = false ;  //rfid是否开启
	private int ret ;  //操作返回
	private byte[] resetByte = new byte[128] ;  //上电复位返回
	private byte[] apdu08 = Tools.HexString2Bytes("0084000008"); //apdu 08随机数指令
	private byte[] apduRecv = new byte[1024] ;  //apdu指令返回
	@Override
	public void onClick(View v) {
		int i1 = v.getId();
		if (i1 == R.id.button_open_rfid) {/*
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
			int i;
			byte[] inbuf = new byte[256];
			byte[] outbuf = new byte[256];
			int TransAmount;
			int TransSerialNo = 1;
			long CardLastRemain;
			int CardCnt;
			byte CardType;
			byte CardKind;
			byte[] CardSnr = new byte[5];
			byte[] TransTime = new byte[7];
			byte[] CityCode = new byte[2];
			byte[] POSID = new byte[6];
			//
			RfidNative rfidNative = new RfidNative();
			//Open Port
			ret = rfidNative.open(14, 115200);
			if (ret < 0) {
				LogE(Tag, "Err:rfidNative.open\n");
			} else {
				LogE(Tag, "OK:rfidNative.open\n");
			}
			//RFidPowerOn
			ret = rfidNative.rfidpoweron();
			if (ret != 0) {
				LogE(Tag, "Err:rfidNative.rfidpoweron\n");
			} else {
				LogE(Tag, "OK:rfidNative.rfidpoweron\n");
			}
			//get Version
			byte[] Version = new byte[64];
			Arrays.fill(Version, (byte) 0);
			rfidNative.sptcreaderapigetver(Version);
			LogE(Tag, "Get Version: " + Version.toString() + "\n");

			//Psam Init
			byte[] PSamNo = new byte[8];
			Arrays.fill(PSamNo, (byte) 0);
			int ret = rfidNative.sptcreaderapipsaminit(2, PSamNo);
			if (ret == 0) {
				//memcpy(POSID,PSamNo,6);
				for (i = 0; i < 6; i++) POSID[i] = PSamNo[i];
			}
			//
			for (int j = 0; j < 3; j++) {
				//Get Card Info
				byte[] outPut = new byte[48];
				Arrays.fill(outPut, (byte) 0);
				ret = rfidNative.sptcreaderapigetcardinfo(outPut);
				if (ret != 0) {
					LogE(Tag, "Err:sptc_reader_api_get_card_info: " + ret + "\n");
					ret = rfidNative.sptcreaderapigetdebugstep();
					LogE(Tag, "Ok:sptc_reader_api_get_debug_step :" + ret + "\n");
					continue;
				} else {
					LogE(Tag, "OK:sptc_reader_api_get_card_info\n");
					LogE(Tag, "CardInfo = 0x" + Tools.Bytes2HexString(outPut, 24) + "\n");
					//for(i=0;i<24;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
					CardType = outPut[0];
					CardSnr[0] = outPut[1];
					CardSnr[1] = outPut[2];
					CardSnr[2] = outPut[3];
					CardSnr[3] = outPut[4];
					CardCnt = outPut[16] * 256 + outPut[17];
					CityCode[0] = outPut[5];
					CityCode[1] = outPut[6];
					CardKind = outPut[7];
					CardLastRemain = outPut[12] * 256 * 256 + outPut[13] * 256 + outPut[14];
				}
				//read last record
				Arrays.fill(outbuf, (byte) 0);
				ret = rfidNative.sptcreaderapigetlastrecord(outbuf);
				if (ret != 0) {
					LogE(Tag, "Err:sptc_reader_api_get_last_record,ret=" + ret + "\n");
					continue;
				} else {
					LogE(Tag, "OK:sptc_reader_api_get_last_record:");
					//for(i=0;i<19;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
					LogE(Tag, "Record = 0x" + Tools.Bytes2HexString(outbuf, 19) + "\n");
				}
				//debit
				Calendar ca = Calendar.getInstance();
				int st_wYear = ca.get(Calendar.YEAR);//获取年份
				int st_wMonth = ca.get(Calendar.MONTH);//获取月份
				int st_wDay = ca.get(Calendar.DATE);//获取日
				int st_wMinute = ca.get(Calendar.MINUTE);//分
				int st_wHour = ca.get(Calendar.HOUR);//小时
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
				TransSerialNo++;
				//udword_to_buf3(TransSerialNo,&inbuf[7]);
				inbuf[7] = 0;
				inbuf[8] = 0;
				inbuf[9] = (byte) TransSerialNo;
				TransAmount = 1;
				//udword_to_buf3(TransAmount,&inbuf[10]);
				inbuf[10] = 0;
				inbuf[11] = 0;
				inbuf[12] = (byte) TransAmount;

				//memset(outbuf,0,sizeof(outbuf));
				Arrays.fill(outbuf, (byte) 0);
				ret = rfidNative.sptcreaderapidebit(inbuf, outbuf);
				if (ret != 0) {
					LogE(Tag, "Err:sptc_read_api_debit,ret = " + ret + "\n");
					continue;
				} else {
					LogE(Tag, "OK:sptc_read_api_debit:");
					//for(i=0;i<9;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
					LogE(Tag, "Debit = 0x" + Tools.Bytes2HexString(outbuf, 9) + "\n");
				}
				//
				if (CardType == 0x01) {
					Arrays.fill(inbuf, (byte) 0);
					//udword_to_buf3(TransSerialNo,inbuf);
					inbuf[0] = 0;
					inbuf[1] = 0;
					inbuf[2] = (byte) TransSerialNo;
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
						continue;
					} else {
						LogE(Tag, "OK:sptc_reader_api_cpu_get_transaction_prove:");
						//for(i=0;i<4;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
						LogE(Tag, "TransProve = 0x" + Tools.Bytes2HexString(outbuf, 4) + "\n");
					}
				} else {
					Arrays.fill(inbuf, (byte) 0);
					//udword_to_buf3(TransSerialNo,inbuf);
					inbuf[2] = (byte) TransSerialNo;
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
					tp = TransAmount;
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
					ret = rfidNative.sptcreaderapim1calctac(inbuf, outbuf);
					if (ret != 0) {
						LogE(Tag, "Err:sptc_reader_api_m1_calc_tac,ret = " + ret + "\n");
						continue;
					} else {
						LogE(Tag, "OK:sptc_reader_api_m1_calc_tac:");
						//for(i=0;i<4;i++) _tprintf(_T(" %02x"),(UBYTE)outbuf[i]);
						LogE(Tag, "CalTAC = 0x" + Tools.Bytes2HexString(outbuf, 4) + "\n");
					}
				}
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

		} else if (i1 == R.id.button_close_rfid) {
			int ret;
			if (isOpen) {
				ret = rfid.close(port);
				if (ret >= 0) {
					rfid = null;
					isOpen = false;
					addTips("close RFID success!! \n");
					setButtonClickable(btnCloseRfid, false);
					setButtonClickable(btnPsamReset, false);
					setButtonClickable(btnPsamClose, false);
					setButtonClickable(bntPsamApdu08, false);
					setButtonClickable(btnReadReg, false);
					setButtonClickable(btnWriteReg, false);
					setButtonClickable(btnOpenRfid, true);
				} else {
					addTips("close RFID fail!! \n" + "Error code = " + ret + "\n");
				}
			}


		} else if (i1 == R.id.button_clear) {
			editTips.setText("");

		} else if (i1 == R.id.button_psam_reset) {
			int ret;
			if (isOpen) {
				ret = rfid.psamreset(psamCard, resetByte);
				if (ret >= 0) {
					addTips("PSAM " + psamCard + "reset success!! \n " + "Recv Data:"
							+ Tools.Bytes2HexString(resetByte, ret) + "\n");
				} else {
					addTips("PSAM " + psamCard + "reset fail!! \n " + "Error code = " + ret + "\n");
				}
			}

		} else if (i1 == R.id.button_psam_close) {
			int ret;
			if (isOpen) {
				ret = rfid.psamclose(psamCard);
				if (ret >= 0) {
					addTips("PSAM " + psamCard + "close success!! \n ");
				} else {
					addTips("PSAM " + psamCard + "reset fail!! \n " + "Error code = " + ret + "\n");
				}
			}

		} else if (i1 == R.id.button_08) {
			int ret;
			if (isOpen) {
				ret = rfid.psamapdu(psamCard, apdu08, apduRecv);
				if (ret >= 0) {
					addTips("PSAM " + psamCard + "apdu success!! \n " + "Recv Data:"
							+ Tools.Bytes2HexString(apduRecv, ret) + "\n");
				} else {
					addTips("PSAM " + psamCard + "apdu fail!! \n " + "Error code = " + ret + "\n");
				}
			}

		} else if (i1 == R.id.button_read_reg) {
			int ret;
			byte[] regValue = new byte[16];
			String addr = editAddr.getText().toString();
			if (addr == null || addr.length() == 0) {
				addTips("please input reg address !! \n");
				return;
			}
			int addrInt = Integer.valueOf(addr);
			if (isOpen) {
				ret = rfid.readreg(addrInt, regValue);
				if (ret >= 0) {
					addTips("read reg success!! value = 0x" + Tools.Bytes2HexString(regValue, ret) + "\n");
					LogE(Tag, "read reg value = 0x" + Tools.Bytes2HexString(regValue, ret));
				} else {
					addTips("read reg fail!! error code = " + ret + "\n");
				}
			}

		} else if (i1 == R.id.button_write_reg) {
			int ret;
			byte[] writeValue = null;
			byte[] regWrite = new byte[16];
			int addrWInt = 0;
			String writeStr = editWriteValue.getText().toString();
			String addrw = editAddr.getText().toString();
			if (addrw == null || addrw.length() == 0) {
				addTips("please input reg address !! \n");
				return;
			}
			if (writeStr == null || writeStr.length() == 0) {
				addTips("please input reg value !! \n");
				return;
			}
			addrWInt = Integer.valueOf(addrw);
			writeValue = Tools.HexString2Bytes(writeStr);
			ret = rfid.writereg(addrWInt, writeValue, regWrite);
			if (ret >= 0) {
				addTips("write reg success!! result = 0x" + Tools.Bytes2HexString(regWrite, ret) + "\n");
//				LogE(Tag, "read reg value = 0x" + Tools.Bytes2HexString(regValue, ret))  ;
			} else {
				addTips("write reg fail!! error code = " + ret + "\n");
			}

		}
		
	}
}
