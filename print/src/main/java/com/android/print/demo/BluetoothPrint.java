package com.android.print.demo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import com.android.print.sdk.PrinterInstance;

/**
 * Created by Administrator on 2016/11/16 0016.
 */

public class BluetoothPrint implements IPrinterOpertion {

    private BluetoothAdapter adapter;
    private Context mContext;

    private BluetoothDevice mDevice;
    private Handler mHandler;
    private PrinterInstance mPrinter;
    public static boolean hasRegDisconnectReceiver;
    private IntentFilter filter;
    private String mac;

    @Override
    public void open(String data) {

    }

    @Override
    public void close() {

    }

    @Override
    public void chooseDevice() {

    }

    @Override
    public PrinterInstance getPrinter() {
        return null;
    }

    @Override
    public void usbAutoConn(UsbManager manager) {

    }

    @Override
    public void btAutoConn(Context context, Handler mHandler) {

    }
}
