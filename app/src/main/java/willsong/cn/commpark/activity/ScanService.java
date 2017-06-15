package willsong.cn.commpark.activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.myscandemo.IScan;
import com.hsm.barcode.DecodeResult;
import com.hsm.barcode.Decoder;
import com.hsm.barcode.DecoderConfigValues;
import com.hsm.barcode.DecoderException;
import com.hsm.barcode.SymbologyConfig;

public class ScanService extends Service {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {

		return mAidlScan;
	}



	private AidlScan mAidlScan = new AidlScan();

	/**
	 * 实现具体的代码
	 * @author Administrator
	 *
	 */
	class AidlScan extends IScan.Stub{

		private Decoder mDecoder ;
		private DecodeResult mDecodeResult;

		private String barcode = null;
		private boolean scanning = false ;
		@Override
		public void initEngine() throws RemoteException {
			mDecoder = new Decoder();
			try {
				mDecoder.connectDecoderLibrary();
				initPara();
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void initPara(){
			try {
				mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_QR);
				mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_PDF417);
				mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_EAN13);

//				mDecoder.enableSymbology(SymbologyID.SYM_DATAMATRIX);
//				mDecoder.enableSymbology(SymbologyID.SYM_UPCA);
//				mDecoder.enableSymbology(SymbologyID.SYM_CHINAPOST);

//				mDecoder.enableSymbology(SymbologyID.SYM_CODE39);
//				mDecoder.enableSymbology(SymbologyID.SYM_CODE128);
//				mDecoder.enableSymbology(SymbologyID.SYM_EAN8);
//				mDecoder.enableSymbology(SymbologyID.SYM_CODE32);
				//打开EAN13码校验
				SymbologyConfig config = new SymbologyConfig(DecoderConfigValues.SymbologyID.SYM_EAN13);
				config.Flags = 5 ;
				config.Mask = 1 ;
				mDecoder.setSymbologyConfig(config);
				try {
					mDecoder.startScanning();
					Thread.sleep(50);
					mDecoder.stopScanning();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private Thread scanThread = null ;
		@Override
		public String scan() throws RemoteException {
			barcode = null;
			if(!scanning){
				if(scanThread != null ){
					//线程不为null强制中断
					scanThread.interrupt() ;
					scanThread = null ;
				}
				//创建单线程
				scanThread = new Thread(scanRun);
				scanThread.start() ;
			}
			return getBarcode();
		}

		private Runnable scanRun = new Runnable() {

			@Override
			public synchronized void run() {
				if(mDecoder != null){
					scanning = true ;
					try {//ɨ��
						boolean is = mDecoder.callbackKeepGoing();
						Thread.sleep(50) ;
						//扫描，超时为5秒
						mDecoder.waitForDecodeTwo(5000, mDecodeResult);

						if(mDecoder.getBarcodeData() != null && mDecoder.getBarcodeLength() > 0){
							barcode = mDecoder.getBarcodeData() ;
							byte[] barByte = barcode.getBytes() ;
						}
						//减慢速度
						Thread.sleep(50) ;
						scanning = false ;
					} catch (DecoderException e) {
						try {
							Thread.sleep(100) ;
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						e.printStackTrace();

						scanning = false ;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		};

		private String getBarcode(){
			int count = 50;
			while(count > 0){
				count--;
				if(barcode != null){
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return barcode;
		}

		@Override
		public void close() throws RemoteException {
			if(mDecoder != null){
				try {
					mDecoder.disconnectDecoderLibrary();
				} catch (DecoderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

}
