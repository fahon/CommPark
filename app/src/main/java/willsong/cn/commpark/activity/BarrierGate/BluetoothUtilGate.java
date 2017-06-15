package willsong.cn.commpark.activity.BarrierGate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

import w.song.orchid.util.MyTools;
import willsong.cn.commpark.activity.Print.PrefUtils;

/**
 * Created by guof on 2017/1/16.
 */

public class BluetoothUtilGate {
    private BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog dialog;
    private static BluetoothDevice mGateDevice;
    public static String devicesGateAddress;

    private BluetoothLeService mBluetoothLeService;
    public boolean mConnected = false;

    // 写数据
    private BluetoothGattCharacteristic characteristic;
    private BluetoothGattService mnotyGattService;

    private String defaultBT = "12345678";

    Activity mContext;

    public BluetoothUtilGate(Activity context) {
        this.mContext = context;
        init();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void init() {
        // 初始化对话框
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("连接道闸蓝牙中");
        dialog.setMessage("请等待");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    //重新连接
    public void open() {
        // 重新连接
        if (!(mBluetoothAdapter == null)) {
            // 判断设备蓝牙功能是否打开
            if (!mBluetoothAdapter.isEnabled()) {
                // 打开蓝牙功能
                Intent enableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mContext.startActivity(enableIntent);
            } else {
                // mDevice
                String devicesAddress = PrefUtils.getString(mContext, SampleGattAttributes.GATEDEVICEADDRESS, "");
                if (devicesAddress == null || devicesAddress.length() <= 0) {
                    Toast.makeText(mContext, "请搜索并连接蓝牙打印设备！", Toast.LENGTH_SHORT).show();
                } else {
                    connect2BlueToothGatedevice(devicesAddress);
                }
            }
        }
    }

    public void connect2BlueToothGatedevice(String devicesAddress) {
        dialog.show();
        mGateDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
                devicesAddress);
        devicesGateAddress = mGateDevice.getAddress();
        Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
        mContext.bindService(gattServiceIntent, mServiceConnection, mContext.BIND_AUTO_CREATE);

        mContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(devicesGateAddress);
            Log.d("TAG", "Connect request result=" + result);
        }
    }

    // Handles various events fired by the Service.处理服务所激发的各种事件
    // ACTION_GATT_CONNECTED: connected to a GATT server.连接一个GATT服务
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.从GATT服务中断开连接
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.查找GATT服务
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read
    // or notification operations.从服务中接受数据
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                mConnected = true;
                Toast.makeText(mContext, "道闸连接成功",
                        Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
                mConnected = false;
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(mContext, "道闸连接失败",
                        Toast.LENGTH_SHORT).show();
                try{
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("道闸蓝牙连接失败,确认重连吗？")
                            .setNegativeButton("取消",null)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    open();
                                }
                            }).show();
                }catch (Exception e){

                }
            }
            // 发现有可支持的服务
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                Log.d("getSupportedGattService", mBluetoothLeService
                        .getSupportedGattServices().size() + "");

                // 写数据的服务和characteristic
                mnotyGattService = mBluetoothLeService
                        .getSupportedGattServices(UUID
                                .fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                characteristic = mnotyGattService.getCharacteristic(UUID
                        .fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                // //读数据的服务和characteristic
                // readMnotyGattService =
                // mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                // readCharacteristic =
                // readMnotyGattService.getCharacteristic(UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb"));
            }
            // 显示数据
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // 将数据显示在mDataField上
                String data = intent
                        .getStringExtra(BluetoothLeService.EXTRA_DATA);
                System.out.println("data----" + data);
            }
        }
    };
    // 管理服务的生命周期
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("TAG", "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(devicesGateAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void openPark(){
        if(mBluetoothLeService == null&&mConnected==false){
            MyTools.showToastShort(false,"连接断开",mContext);
            return;
        }
        byte[] arrayOfByte4 = { -59, 8, 1, -103, -103, -103, -103,
                -103, -103, -103, -103, -86 };
        arrayOfByte4[3] = ((byte) defaultBT.charAt(0));
        arrayOfByte4[4] = ((byte) defaultBT.charAt(1));
        arrayOfByte4[5] = ((byte) defaultBT.charAt(2));
        arrayOfByte4[6] = ((byte) defaultBT.charAt(3));
        arrayOfByte4[7] = ((byte) defaultBT.charAt(4));
        arrayOfByte4[8] = ((byte) defaultBT.charAt(5));
        arrayOfByte4[9] = ((byte) defaultBT.charAt(6));
        arrayOfByte4[10] = ((byte) defaultBT.charAt(7));

        characteristic.setValue(arrayOfByte4);
        mBluetoothLeService.writeCharacteristic(characteristic);
    }
    public void closePark(){
        if(mBluetoothLeService == null&&mConnected==false){
            MyTools.showToastShort(false,"连接断开",mContext);
            return;
        }
        byte[] arrayOfByte4 = { -59, 9, 1, -103, -103, -103, -103,
                -103, -103, -103, -103, -86 };
        arrayOfByte4[3] = ((byte) defaultBT.charAt(0));
        arrayOfByte4[4] = ((byte) defaultBT.charAt(1));
        arrayOfByte4[5] = ((byte) defaultBT.charAt(2));
        arrayOfByte4[6] = ((byte) defaultBT.charAt(3));
        arrayOfByte4[7] = ((byte) defaultBT.charAt(4));
        arrayOfByte4[8] = ((byte) defaultBT.charAt(5));
        arrayOfByte4[9] = ((byte) defaultBT.charAt(6));
        arrayOfByte4[10] = ((byte) defaultBT.charAt(7));

        characteristic.setValue(arrayOfByte4);
        mBluetoothLeService.writeCharacteristic(characteristic);
    }
   public void unregistermReceiver(){
       mContext.unregisterReceiver(mGattUpdateReceiver);
   }
}
