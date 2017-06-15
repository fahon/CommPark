package willsong.cn.commpark.activity.Print;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.print.demo.BluetoothDeviceList;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import willsong.cn.commpark.R;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

public class BluetoothUtil{

    public static boolean isConnected = false;// 蓝牙连接状态
    public static String devicesName = "未知设备";
    public int interfaceType = 0;
    public static String devicesAddress;
    private IntentFilter bluDisconnectFilter;
    public static PrinterInstance myPrinter;
    private static BluetoothDevice mDevice;
    public ProgressDialog dialog;

    private static boolean hasRegDisconnectReceiver = false;

    public static final String TAG = "SettingActivity";

    private BluetoothAdapter mBtAdapter;

    public static final int CONNECT_DEVICE = 1;

    Activity mContext;

    public BluetoothUtil(Activity context){
        this.mContext = context;
        init();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void init() {
        // 初始化对话框
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("连接打印机中");
        dialog.setMessage("请等待");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

    }

    // 用于接受连接状态消息的 Handler
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }catch (Exception e){
            }
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    isConnected = true;
                    GlobalContants.ISCONNECTED = isConnected;
                    GlobalContants.DEVICENAME = devicesName;
                    if (interfaceType == 0) {
                        bluDisconnectFilter = new IntentFilter();
                        bluDisconnectFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                        mContext.registerReceiver(myReceiver, bluDisconnectFilter);
                        hasRegDisconnectReceiver = true;
                        Toast.makeText(mContext, "连接成功",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PrinterConstants.Connect.FAILED:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    isConnected = false;
//                    Toast.makeText(mContext, R.string.conn_failed,
//                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "连接失败!");
                    try{
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("蓝牙连接失败,确认重连吗？")
                                .setNegativeButton("取消",null)
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        open();
                                    }
                                }).show();
                    }catch (Exception e){

                    }
                    break;
                case PrinterConstants.Connect.CLOSED:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    isConnected = false;
                    GlobalContants.ISCONNECTED = isConnected;
                    GlobalContants.DEVICENAME = devicesName;
                    Toast.makeText(mContext, R.string.conn_closed,
                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "连接关闭!");
                    break;
                case PrinterConstants.Connect.NODEVICE:
                    isConnected = false;
                    Toast.makeText(mContext, R.string.conn_no, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case GlobalContants.CONNECTED:

                    break;
                case 0:
                    Toast.makeText(mContext, "打印机通信正常!", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(mContext, "打印机通信异常常，请检查蓝牙连接!", Toast.LENGTH_SHORT).show();
                    vibrator();
                    break;
                case -2:
                    Toast.makeText(mContext, "打印机缺纸!", Toast.LENGTH_SHORT).show();
                    vibrator();
                    break;
                case -3:
                    Toast.makeText(mContext, "打印机开盖!", Toast.LENGTH_SHORT).show();
                    vibrator();
                    break;
                default:
                    break;
            }
        }
    };

    int count = 0;

    public void vibrator() {
        count++;
        PrefUtils.setInt(mContext, "count3", count);
        Log.e(TAG, "" + count);
        MediaPlayer player = new MediaPlayer().create(mContext, R.raw.test);
        MediaPlayer player2 = new MediaPlayer().create(mContext, R.raw.beep);
        player.start();
        player2.start();
    }

    public BroadcastReceiver myReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {

                if (device != null && myPrinter != null
                        && isConnected && device.equals(mDevice)) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    myPrinter.closeConnection();
                    mHandler.obtainMessage(PrinterConstants.Connect.CLOSED).sendToTarget();
                }
            }

        }
    };


    public void getBluetooth() {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.str_message)
                .setMessage(R.string.str_connlast)
                .setPositiveButton(R.string.yesconn,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface arg0, int arg1) {
                                open();
                            }
                        })
                .setNegativeButton(R.string.str_resel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                bluetoothList();
                            }

                        }).show();
    }


    public void connect2BlueToothdevice(String devicesAddress) {
        dialog.show();
        this.devicesAddress = devicesAddress;
        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
                devicesAddress);
        devicesName = mDevice.getName();
        myPrinter = PrinterInstance.getPrinterInstance(mDevice, mHandler);
        if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {// 未绑定
            IntentFilter boundFilter = new IntentFilter();
            boundFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mContext.registerReceiver(boundDeviceReceiver, boundFilter);
            PairOrConnect(true);
        } else {
            PairOrConnect(false);
        }
    }

    private BroadcastReceiver boundDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mDevice.equals(device)) {
                    return;
                }
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.i(TAG, "bounding......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.i(TAG, "bound success");
                        mContext.unregisterReceiver(boundDeviceReceiver);
                        dialog.show();
                        // 配对完成开始连接
                        if (myPrinter != null) {
                            new connectThread().start();
                        }
                        break;
                    case BluetoothDevice.BOND_NONE:
                        mContext.unregisterReceiver(boundDeviceReceiver);
                        Log.i(TAG, "bound cancel");
                        break;
                    default:
                        break;
                }

            }
        }
    };

    private class connectThread extends Thread {
        @Override
        public void run() {

            if (myPrinter != null) {
                // TODO
                isConnected = myPrinter.openConnection();
                // myPrinter.printText("测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭测试蓝牙连续连接--打印--关闭/n");
                // myPrinter.closeConnection();
                PrefUtils.setBoolean(mContext,GlobalContants.CONNECTSTATE,isConnected);
                mHandler.obtainMessage(GlobalContants.CONNECTED);
            }
        }
    }

    private void PairOrConnect(boolean pair) {
        if (pair) {
            IntentFilter boundFilter = new IntentFilter(
                    BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mContext.registerReceiver(boundDeviceReceiver, boundFilter);
            boolean success = false;
            try {
                Method createBondMethod = BluetoothDevice.class
                        .getMethod("createBond");
                success = (Boolean) createBondMethod.invoke(mDevice);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "createBond is success? : " + success);

        } else {
            new connectThread().start();
        }
    }

    //重新连接
    public void open(){
        // 重新连接
        if (!(mBtAdapter == null)) {
            // 判断设备蓝牙功能是否打开
            if (!mBtAdapter.isEnabled()) {
                // 打开蓝牙功能
                Intent enableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mContext.startActivity(enableIntent);
            } else {
                // mDevice
                devicesAddress = PrefUtils.getString(mContext, GlobalContants.DEVICEADDRESS, "");
                if (devicesAddress == null || devicesAddress.length() <= 0) {
                    Toast.makeText(mContext, "请搜索并连接蓝牙打印设备！", Toast.LENGTH_SHORT).show();
                } else {
                    connect2BlueToothdevice(devicesAddress);
                }
            }
        }
    }

    //扫描蓝牙连接
    public void bluetoothList(){
        if (!(mBtAdapter == null)) {
            // 判断设备蓝牙功能是否打开
            if (!mBtAdapter.isEnabled()) {
                // 打开蓝牙功能
                Intent enableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mContext.startActivity(enableIntent);
                Intent intent = new Intent(
                        mContext,
                        BluetoothDeviceList.class);
                mContext.startActivityForResult(intent, CONNECT_DEVICE);
            } else {
                Intent intent = new Intent(
                        mContext,
                        BluetoothDeviceList.class);
                mContext.startActivityForResult(intent, CONNECT_DEVICE);
            }
        }
    }

}
