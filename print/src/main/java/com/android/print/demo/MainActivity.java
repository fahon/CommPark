package com.android.print.demo;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.usb.UsbManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.demo.utils.BarcodeUtils;
import com.android.print.demo.utils.CodePageUtils;
import com.android.print.demo.utils.FileUtils;
import com.android.print.demo.utils.ImageAndCanvasUtils;
import com.android.print.demo.utils.LabelUtils;
import com.android.print.demo.utils.PrintUtils;
import com.android.print.sdk.PrinterConstants.Connect;
import com.android.print.sdk.PrinterInstance;
import com.android.print.sdk.bluetooth.BluetoothPort;
import com.android.print.sdk.wifi.WifiAdmin;

public class MainActivity extends  ListActivity implements OnClickListener {
	private Context mContext;
	private ImageView imageView;
	private Button btnBluetooth, btnWifi, btnUsb;
	private int offset = 0;
	private int currIndex = 0;// 0--bluetooth,1--wifi,2--usb
	private int bmpW;
	private boolean showUSB; // before android3.0 don't show usb
	private static boolean isConnected;
	protected static IPrinterOpertion myOpertion;
	private PrinterInstance mPrinter;

	public static final int CONNECT_DEVICE = 1;
	public static final int ENABLE_BT = 2;
	private Button printImage;
	private Button printText;
	private Button printFile;
	private Button printNote;
	private Button printBarCode;
	private Button printCodePage;
	private Button printLabel;
	private Button exitApp;
	private Button helpApp;
	private TextView conn_state;
	private TextView conn_address;
	private TextView conn_name;
	private RadioButton paperWidth_58;
	private RadioButton paperWidth_80;
	private RadioButton printer_type_remin;
	private RadioButton printer_type_styuls;
	private boolean is58mm = true;
	private boolean isStylus = false;
	private ProgressDialog dialog;
	private View v1;
	private String bt_mac;
	private String bt_name;
	private String wifi_mac;
	private String wifi_name;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		v1=getLayoutInflater().inflate( R.layout.activity_print_main, null );
		setContentView(v1);
		showUSB = Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1;//对于SDK小于2.3的版本不显示USB界面
		mContext = this;
		InitView();//初始化界面
		InitImageView();
		new FileUtils().createFile(this);//向SD卡上写入打印文件
		ExitApplication.getInstance().addActivity(this);//退出功能

	}

	private void InitView() {

		paperWidth_58 = (RadioButton) findViewById(R.id.width_58mm);
		paperWidth_58.setOnClickListener(this);
		paperWidth_80 = (RadioButton) findViewById(R.id.width_80mm);
		paperWidth_80.setOnClickListener(this);

		printer_type_remin = (RadioButton) findViewById(R.id.type_remin);
		printer_type_remin.setOnClickListener(this);
		printer_type_styuls = (RadioButton) findViewById(R.id.type_styuls);
		printer_type_styuls.setOnClickListener(this);

		printText = (Button) findViewById(R.id.btnPrintText);
		printText.setOnClickListener(this);
		printFile = (Button) findViewById(R.id.btnPrintFile);
		printFile.setOnClickListener(this);
		printBarCode = (Button) findViewById(R.id.btnPrintBarCode);
		printBarCode.setOnClickListener(this);
		printImage = (Button) findViewById(R.id.btnPrintImage);
		printImage.setOnClickListener(this);

		printNote = (Button) findViewById(R.id.btnPrintNote);
		printNote.setOnClickListener(this);
		printCodePage=(Button) findViewById(R.id.btnPrintCodePage);
		printCodePage.setOnClickListener(this);
		printLabel=(Button)findViewById(R.id.btnPrintBiaoqian);
		printLabel.setOnClickListener(this);
		exitApp =(Button)findViewById(R.id.btnExit);
		exitApp.setOnClickListener(this);
		helpApp=(Button)findViewById(R.id.btnHelp);
		helpApp.setOnClickListener(this);

		conn_state=(TextView)findViewById(R.id.connect_state);
		conn_state.setOnClickListener(this);
		conn_name=(TextView)findViewById(R.id.connect_name);
		conn_name.setOnClickListener(this);
		conn_address=(TextView)findViewById(R.id.connect_address);
		conn_address.setOnClickListener(this);


		btnBluetooth = (Button) findViewById(R.id.btnBluetooth);
		btnBluetooth.setOnClickListener(this);

		btnWifi = (Button) findViewById(R.id.btnWifi);
		btnWifi.setOnClickListener(this);

		btnUsb = (Button) findViewById(R.id.btnUsb);
		if (showUSB) {
			btnUsb.setOnClickListener(this);
		} else {
			btnUsb.setVisibility(View.GONE);
		}

		dialog = new ProgressDialog(mContext);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle("Connecting...");
		dialog.setMessage("Please Wait...");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		setTitleTextColor(0);
	}

	private void updateButtonState() {
		if (!isConnected)
		{
			conn_address.setText(R.string.no_conn_address);
			conn_state.setText(R.string.connect);
			conn_name.setText(R.string.no_conn_name);

		} else {

			if(currIndex==0)
			{
			   if( bt_mac!=null && !bt_mac.equals(""))
			   {
				   conn_address.setText(mContext.getResources().getString(R.string.str_address)+bt_mac);
				   conn_state.setText(R.string.disconnect);
				   conn_name.setText(mContext.getResources().getString(R.string.str_name)+bt_name);
			   }else if(bt_mac==null )
			   {
                   bt_mac=BluetoothPort.getmDeviceAddress();
                   bt_name=BluetoothPort.getmDeviceName();
				   conn_address.setText(mContext.getResources().getString(R.string.str_address)+bt_mac);
				   conn_state.setText(R.string.disconnect);
				   conn_name.setText(mContext.getResources().getString(R.string.str_name)+bt_name);
			   }

			}else if(currIndex==1)
			{
			   conn_address.setText(mContext.getResources().getString(R.string.str_address)+wifi_mac);
			   conn_state.setText(R.string.disconnect);
			   conn_name.setText(mContext.getResources().getString(R.string.str_name)+wifi_name);
		    }else if(currIndex==2)
		    {
               conn_address.setText(mContext.getResources().getString(R.string.disconnect));
               conn_state.setText(R.string.disconnect);
               conn_name.setText(mContext.getResources().getString(R.string.disconnect));
		    }
		}

		btnBluetooth.setEnabled(!isConnected);
		btnWifi.setEnabled(!isConnected);
		btnUsb.setEnabled(!isConnected);

		printText.setEnabled(isConnected);
		printBarCode.setEnabled(isConnected);
		printImage.setEnabled(isConnected);
		printNote.setEnabled(isConnected);
		printFile.setEnabled(isConnected);
		printCodePage.setEnabled(isConnected);
		printLabel.setEnabled(isConnected);

	}

	@Override
	protected void onActivityResult(final int requestCode, int resultCode,
			final Intent data) {
		switch (requestCode) {
		case CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK)
			{
			    if(currIndex==0)
			    {
			       bt_mac= data.getExtras().getString(BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
				   bt_name=data.getExtras().getString(BluetoothDeviceList.EXTRA_DEVICE_NAME);
				   dialog.show();
				    new Thread(new Runnable() {
					    public void run() {
						myOpertion.open(bt_mac);
					  }
				    }).start();
			    }else if(currIndex==2)
			    {
			    	myOpertion.open(bt_mac);
			    }else if(currIndex==1)
			    {
			    	wifi_mac = data.getStringExtra("ip_address");
			    	wifi_name=data.getExtras().getString("device_name");
			    	if(!wifi_mac.equals("") && wifi_mac!=null)
			    	{

			    		myOpertion.open(bt_mac);
			    	   dialog.show();
			    	}else
			    	{
			    		mHandler.obtainMessage(Connect.FAILED).sendToTarget();
			    	}
			    }
			}
			break;
		case ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				myOpertion.chooseDevice();
			} else {
				Toast.makeText(this, R.string.bt_not_enabled,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void InitImageView() {
		imageView = (ImageView) findViewById(R.id.cursor);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.slide1)
				.getWidth();

		offset = (screenW / (btnUsb.getVisibility() == View.VISIBLE ? 3 : 2)
				- bmpW - 4) / 2;// ����ƫ��,�����4��}�߱߾�
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);
	}

	public void onPageSelected(View view) {
		int index;
		if (view == btnBluetooth) {
			index = 0;
		} else if (view == btnWifi) {
			index = 1;
		} else {
			index = 2;
		}

		int one = offset * 2 + bmpW;

		Animation animation = new TranslateAnimation(one * currIndex, one
				* index, 0, 0);
		currIndex = index;
		animation.setFillAfter(true);
		animation.setDuration(300);
		imageView.startAnimation(animation);
		setTitleTextColor(index);
	}

	private void setTitleTextColor(int index) {
		switch (index) {
		case 0:
			btnBluetooth.setTextColor(Color.BLUE);
			btnWifi.setTextColor(Color.BLACK);
			btnUsb.setTextColor(Color.BLACK);
			break;
		case 1:
			btnBluetooth.setTextColor(Color.BLACK);
			btnWifi.setTextColor(Color.BLUE);
			btnUsb.setTextColor(Color.BLACK);
			break;
		case 2:
			btnBluetooth.setTextColor(Color.BLACK);
			btnWifi.setTextColor(Color.BLACK);
			btnUsb.setTextColor(Color.BLUE);
			break;

		default:
			break;
		}
		updateButtonState();
	}

	private void openConn() {
		if (!isConnected) {
			switch (currIndex) {
			case 0: // bluetooth
				new AlertDialog.Builder(this).setTitle(R.string.str_message)
				               .setMessage(R.string.str_connlast)
				               .setPositiveButton(R.string.yesconn, new  DialogInterface.OnClickListener()
				               {
								@Override
								public void onClick(DialogInterface arg0,int arg1)
								{
									myOpertion = new BluetoothOperation(MainActivity.this, mHandler);
									Context context=mContext;
									myOpertion.btAutoConn(context,mHandler);

								}
				               })
				               .setNegativeButton(R.string.str_resel, new  DialogInterface.OnClickListener()
				               {

								@Override
								public void onClick(DialogInterface dialog,int which)
								{
									myOpertion = new BluetoothOperation(MainActivity.this, mHandler);
									myOpertion.chooseDevice();
								}

				               })
				               .show();

				break;
			case 1: // wifi


				WifiManager mWifi =(WifiManager) getSystemService(Context.WIFI_SERVICE);
				if (!mWifi.isWifiEnabled())
				{
		            mWifi.setWifiEnabled(true);
		        }
		        WifiInfo wifiInfo = mWifi.getConnectionInfo();
		        wifi_name=wifiInfo.getSSID();
		        String content;

		        if(wifi_name!=null && !wifi_name.equals(""))
		        {
		        	content=mContext.getResources().getString(R.string.str_wifi_now)+""+wifi_name+"。";
		        	DhcpInfo dhcpinfo = mWifi.getDhcpInfo();

		        	WifiAdmin mWifiAdmin=new WifiAdmin(MainActivity.this);

		            wifi_mac= mWifiAdmin.intToIp(dhcpinfo.serverAddress);
		        }else
		        {
		        	content=mContext.getResources().getString(R.string.str_wifi_no);
		        }


						new AlertDialog.Builder(this).setTitle(R.string.str_message)
			               .setMessage(content)
			               .setPositiveButton(R.string.yesconn, new  DialogInterface.OnClickListener()
			               {
							@Override
							public void onClick(DialogInterface arg0,int arg1)
							{
								if(wifi_name!=null && !wifi_name.equals(""))
								{

									myOpertion = new WifiOperation(MainActivity.this, mHandler);
									Intent intent=new Intent();
									intent.putExtra("ip_address", wifi_mac);
									myOpertion.open(wifi_mac);
								}else
								{
									Toast.makeText(mContext, R.string.str_wifi_no, Toast.LENGTH_SHORT);
								}

							}
			               })
			               .setNegativeButton(R.string.str_resel, new  DialogInterface.OnClickListener()
			               {

							@Override
							public void onClick(DialogInterface dialog,int which)
							{
								myOpertion = new WifiOperation(MainActivity.this, mHandler);
								myOpertion.chooseDevice();
							}

			               })
			               .show();

				break;
			case 2: // usb


				new AlertDialog.Builder(this).setTitle(R.string.str_message)
	               .setMessage(R.string.str_connlast)
	               .setPositiveButton(R.string.yesconn, new  DialogInterface.OnClickListener()
	               {
					@Override
					public void onClick(DialogInterface arg0,int arg1)
					{
						myOpertion = new UsbOperation(MainActivity.this, mHandler);
						UsbManager manager = (UsbManager)getSystemService(Context.USB_SERVICE);
						myOpertion.usbAutoConn(manager);

					}
	               })
	               .setNegativeButton(R.string.str_resel, new  DialogInterface.OnClickListener()
	               {

					@Override
					public void onClick(DialogInterface dialog,int which)
					{
						myOpertion = new UsbOperation(MainActivity.this, mHandler);
						myOpertion.chooseDevice();
					}

	               })
	               .show();

				break;
			default:
				break;
			}
		} else {
			myOpertion.close();
			myOpertion = null;
			mPrinter = null;
		}
	}

	//用于接受连接状态消息的 Handler
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Connect.SUCCESS:
				isConnected = true;
                mPrinter = myOpertion.getPrinter();
				break;
			case Connect.FAILED:
				isConnected = false;
				Toast.makeText(mContext, R.string.conn_failed,
						Toast.LENGTH_SHORT).show();
				break;
			case Connect.CLOSED:
				isConnected = false;
				Toast.makeText(mContext, R.string.conn_closed, Toast.LENGTH_SHORT)
						.show();
				break;
			case Connect.NODEVICE:
				isConnected = false;
				Toast.makeText(mContext, R.string.conn_no, Toast.LENGTH_SHORT)
						.show();
				break;

			default:
				break;
			}

			updateButtonState();

			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	};

	@Override
	public void onClick(View view) {
	    if (view == conn_state || view == conn_address || view == conn_name)
		{
			openConn();
		}
	    else if (view == btnBluetooth || view == btnWifi || view == btnUsb)
		{
			onPageSelected(view);
		}
	    else if (view == printText)
	    {
			PrintUtils.printText(mContext.getResources(), mPrinter);
		}
	    else if (view == printImage)
	    {
			new ImageAndCanvasUtils().printImage(mContext.getResources(), mPrinter, isStylus);
			new ImageAndCanvasUtils().printCustomImage(mContext.getResources(), mPrinter,
					isStylus, is58mm);
		} else if (view == printNote) {
			PrintUtils.printNote(mContext.getResources(), mPrinter, is58mm);
		} else if (view == printBarCode) {
			new BarcodeUtils().selectBarCode(this,mPrinter);

		} else if (view == printLabel)
		{
				new AlertDialog.Builder(this).setTitle(R.string.str_message)
	            .setMessage(R.string.note_label)
	            .setPositiveButton(R.string.print, new  DialogInterface.OnClickListener()
	            {
					@Override
					public void onClick(DialogInterface arg0,int arg1)
					{
						new LabelUtils().printLabel(mPrinter);

					}
	            })
	            .setNegativeButton(R.string.button_back, new  DialogInterface.OnClickListener()
	            {

					@Override
					public void onClick(DialogInterface dialog,int which)
					{

					}

	            })
	            .show();

		}else if (view == printCodePage)
		{
			new CodePageUtils().selectCodePage(this,mPrinter);

		}else if (view == helpApp)
		{
	          Intent it=new Intent();
	          it.setClass(this, HelpActivity.class);
	          startActivity(it);

		}else if(view == exitApp)
		{
			ExitApplication.getInstance().exit();
		}
		else if (view == paperWidth_58 || view == paperWidth_80)
		{
			is58mm = view == paperWidth_58;
			paperWidth_58.setChecked(is58mm);
			paperWidth_80.setChecked(!is58mm);

		} else if (view == printer_type_remin || view == printer_type_styuls)
		{
			isStylus = view == printer_type_remin;
			printer_type_remin.setChecked(isStylus);
			printer_type_styuls.setChecked(!isStylus);

		} else if (view == printFile)
		{
			new FileUtils().selectFile(this, v1);
		}

	}




     @Override
	protected void onListItemClick(ListView l, View v, int position, long id)
    {
            new FileUtils().printFile(position, this, mPrinter);
	}


	@Override
	protected void onStop()
	{
		super.onStop();
	   if(myOpertion!=null)
	   {
		 myOpertion.close();
	   }

	}




 }




