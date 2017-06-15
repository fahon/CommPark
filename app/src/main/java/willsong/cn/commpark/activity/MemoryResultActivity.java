package willsong.cn.commpark.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.kevingo.licensekeyboard.MainActivity;
import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;

public class MemoryResultActivity extends OBaseActivity {
	private TextView number,color;
	private Button confirm;
	private int width,height;
	private TextView text_num,text_color,text_title;
	private ImageView image,image_back;
	private String bitmapPath;
	private Bitmap bitmap = null;
	private boolean recogType;
	private Button cardBreak;
	private Button correct;


	private ImageView cardplate;
	private TextView cardPlateHome;
	private Button card_break,cardconfim,error;

	private static final String PATH = Environment
			.getExternalStorageDirectory().toString() + "/DCIM/Camera/";

	//WillSong
	public static class IMPORT_FIELD {
		public static final String TASK = "task";
	}

	public static class TASK_VALUE {
		public static final String ENTER_PARK = "enter_park";
		public static final String EXIT_PARK = "exit_park";
		public static final String CHECK_CAR = "check_car";
	}

	private String task = "";
	private String carColor = "",carNum="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		task = getIntent().getStringExtra(MemoryResultActivity.IMPORT_FIELD.TASK);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		setContentViewWithTitle(R.layout.activity_sult);
		init();

		setRightButtonVisible(false);
		setTitleText("识别结果");
		setTitleLeftButtonOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MemoryResultActivity.this, MemoryCameraActivity.class);
				intent.putExtra("camera", recogType);
				intent.putExtra(MemoryResultActivity.IMPORT_FIELD.TASK,task);
				startActivity(intent);
				finish();
			}
		});
	}

	private void init() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		cardplate = (ImageView) findViewById(R.id.cardPlate);
		cardPlateHome = (TextView) findViewById(R.id.cardPlateHome);
		card_break = (Button) findViewById(R.id.card_break);
		cardconfim = (Button) findViewById(R.id.cardconfim);
		error = (Button) findViewById(R.id.cardError);

		setLeftButtonVisible2(false);

		if(mBusinessManager.getPlateRecognize().toString().equals("1")){//1:车牌识别后，再进入车牌修改界面 其他:直接进入修改界面，需手动选择输入车牌号
			if(getIntent().getStringExtra("scanType").equals("0")){//使用手持机的车牌识别方式进入
			recogType  = getIntent().getBooleanExtra("recogType", false);
			System.out.println("识别时间："+getIntent().getStringExtra("time"));

			bitmapPath = getIntent().getStringExtra("path");
			int left = getIntent().getIntExtra("left", -1);
			int top  = getIntent().getIntExtra("top", -1);
			int w =getIntent().getIntExtra("width", -1);
			int h = getIntent().getIntExtra("height", -1);
			System.out.println("图片路径"+bitmapPath);
			if(bitmapPath!=null&&!bitmapPath.equals("")){
				bitmap = BitmapFactory.decodeFile(bitmapPath);
				bitmap = Bitmap.createBitmap(bitmap, left, top, w, h);

				if(bitmap!=null){
					cardplate.setImageBitmap(bitmap);
				}
			}
//				carColor = getIntent().getStringExtra("color");
//				carNum = ""+getIntent().getCharSequenceExtra("number");
			}else{//使用立式摄像头扫描车牌进入
				byte[] bis=getIntent().getByteArrayExtra("bitmap");
				bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);
				if(bitmap!=null){
					cardplate.setImageBitmap(bitmap);
					cardplate.invalidate();
				}
			}
			recogType  = getIntent().getBooleanExtra("recogType", false);
			carColor = getIntent().getStringExtra("color");
			carNum = ""+getIntent().getStringExtra("number");
		}else{
			recogType = false;
			carColor = "";
			carNum = "";
		}

		cardPlateHome.setText(carNum);
//		cardPlateHome.setTextColor(Color.BLACK);

		error.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = intentInstance(MainActivity.class);
				it.putExtra("commonLicence",""+mBusinessManager.getIntentCommonLicence());
				MemoryResultActivity.this.startActivityForResult(it, 0);
			}
		});
		cardconfim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(cardPlateHome.getText().toString().trim().equals("")){
					MyTools.showToastLong(true, "请输入车牌号码", mContext);
					return;
				}
				if (MemoryResultActivity.TASK_VALUE.ENTER_PARK.equals(task)) {//进场
					Intent it = new Intent(MemoryResultActivity.this, EnterParkInfoActivity.class);
					it.putExtra(EnterParkInfoActivity.IMPORT_FIELD.PLATE_CODE,cardPlateHome.getText().toString().trim());
					if(SharedPreferencesConfig.getString(mContext,"loginFlag").equals("1")){
						it.putExtra("color",carColor);
					}
					startActivity(it);
				}else if (MemoryResultActivity.TASK_VALUE.EXIT_PARK.equals(task)) {//出场
					Intent it = new Intent(MemoryResultActivity.this, ExitParkActivity.class);
					it.putExtra(ExitParkActivity.IMPORT_FIELD.PLATE_CODE,cardPlateHome.getText().toString().trim());
					if(SharedPreferencesConfig.getString(mContext,"loginFlag").equals("1")){
						it.putExtra("color",carColor);
					}
					startActivity(it);
				}else if (MemoryResultActivity.TASK_VALUE.CHECK_CAR.equals(task)) {//寻场
					Intent it = new Intent(MemoryResultActivity.this, CarCheckActivity.class);
					it.putExtra(CarCheckActivity.IMPORT_FIELD.PLATE_CODE,cardPlateHome.getText().toString().trim());
					startActivity(it);
				}
				finish();
			}
		});

		card_break.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mBusinessManager.getPlateRecognize().toString().equals("1")){
					Intent intent = new Intent(MemoryResultActivity.this,MemoryCameraActivity.class);
					intent.putExtra("camera", recogType);//判断是拍照识别还是自动识别 true:自动识别 false:拍照识别
					intent.putExtra(MemoryResultActivity.IMPORT_FIELD.TASK, task);
					startActivity(intent);
					finish();
				}else{
					finish();
				}
			}
		});
	}

	/**
	 *
	 * @Title: findView
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * 设定文件
	 * @return void    返回类型
	 * @throws
	 */
	private void findView() {
		// TODO Auto-generated method stub

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）
		number = (TextView) findViewById(R.id.plate_number);
		color = (TextView) findViewById(R.id.plate_color);
		confirm = (Button) findViewById(R.id.confirm);
		text_num=(TextView) findViewById(R.id.text_number);
		text_color = (TextView) findViewById(R.id.text_color);
		image = (ImageView) findViewById(R.id.plate_image);
		image_back = (ImageView) findViewById(R.id.plate_back);
		text_title  = (TextView) findViewById(R.id.plate_title);
		correct = (Button) findViewById(R.id.correct);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.BELOW,R.id.plate_image);
		layoutParams.leftMargin = width/4;
		layoutParams.bottomMargin = height/6;
		text_num.setLayoutParams(layoutParams);

		layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.text_number);
		layoutParams.addRule(RelativeLayout.BELOW,R.id.plate_image);
		number.setGravity(Gravity.CENTER);
		layoutParams.bottomMargin = height/8;
		number.setLayoutParams(layoutParams);

		layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.BELOW,R.id.text_number);
		layoutParams.leftMargin = width/4;
		layoutParams.bottomMargin = height/10;
		text_color.setLayoutParams(layoutParams);

		layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.text_color);
		layoutParams.addRule(RelativeLayout.BELOW,R.id.text_number);
		layoutParams.leftMargin = width/5;
		layoutParams.bottomMargin = height/10;
		color.setLayoutParams(layoutParams);

//		layoutParams = new RelativeLayout.LayoutParams(width/2, RelativeLayout.LayoutParams.WRAP_CONTENT);
//		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
//		layoutParams.bottomMargin = height/5;
//		confirm.setLayoutParams(layoutParams);
		//cardBreak.setLayoutParams(layoutParams);

		int bm_width = (int) (width*0.5);
		int bm_height = bm_width*1;
		layoutParams= new RelativeLayout.LayoutParams(bm_width, bm_height);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
		layoutParams.leftMargin = width/4;
		layoutParams.topMargin = height/8;
		image.setLayoutParams(layoutParams);

		int back_h = (int) (height * 0.066796875);
		int back_w = back_h * 1;
		layoutParams= new RelativeLayout.LayoutParams(back_w, back_h);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
		layoutParams.leftMargin =  (int) (width * 0.05);
		image_back.setLayoutParams(layoutParams);
		image_back.setVisibility(View.GONE);

		layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		text_title.setLayoutParams(layoutParams);


		setLeftButtonVisible2(false);

		bitmapPath = getIntent().getStringExtra("path");
		int left = getIntent().getIntExtra("left", -1);
		int top  = getIntent().getIntExtra("top", -1);
		int w =getIntent().getIntExtra("width", -1);
		int h = getIntent().getIntExtra("height", -1);
		System.out.println("图片路径"+bitmapPath);
		if(bitmapPath!=null&&!bitmapPath.equals("")){
			bitmap = BitmapFactory.decodeFile(bitmapPath);
			bitmap = Bitmap.createBitmap(bitmap, left, top, w, h);

			if(bitmap!=null){
				image.setImageBitmap(bitmap);
			}
		}

		text_title.setTextSize(20);
		number.setText(getIntent().getCharSequenceExtra("number"));
		color.setText(getIntent().getCharSequenceExtra("color"));
		color.setTextColor(Color.BLACK);
		number.setTextColor(Color.BLACK);
		text_num.setTextColor(Color.BLACK);
		text_color.setTextColor(Color.BLACK);
		image_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// TODO Auto-generated method stub
				Intent intent = new Intent(MemoryResultActivity.this,MemoryCameraActivity.class);
				intent.putExtra("camera", recogType);//
				startActivity(intent);

				finish();
			}
		});
		cardBreak.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MemoryResultActivity.this,MemoryCameraActivity.class);
				intent.putExtra("camera", recogType);//
				startActivity(intent);

				finish();
			}
		});
		correct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = intentInstance(MainActivity.class);
				MemoryResultActivity.this.startActivityForResult(it, 0);
			}
		});
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (MemoryResultActivity.TASK_VALUE.ENTER_PARK.equals(task)) {//进场
					Intent it = new Intent(MemoryResultActivity.this, EnterParkInfoActivity.class);
					it.putExtra(EnterParkInfoActivity.IMPORT_FIELD.PLATE_CODE,""+getIntent().getCharSequenceExtra("number"));
					startActivity(it);
				}else if (MemoryResultActivity.TASK_VALUE.EXIT_PARK.equals(task)) {//出场
					Intent it = new Intent(MemoryResultActivity.this, ExitParkActivity.class);
					it.putExtra(ExitParkActivity.IMPORT_FIELD.PLATE_CODE,""+getIntent().getCharSequenceExtra("number"));
					startActivity(it);
				}else if (MemoryResultActivity.TASK_VALUE.CHECK_CAR.equals(task)) {//寻场
					Intent it = new Intent(MemoryResultActivity.this, CarCheckActivity.class);
					it.putExtra(CarCheckActivity.IMPORT_FIELD.PLATE_CODE,""+getIntent().getCharSequenceExtra("number"));
					startActivity(it);
				}
			}
		});
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("onActivityResult=", "" + requestCode + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == MainActivity.RESULTCODE) {
			String plateNumber = data.getStringExtra(MainActivity.RESULT_FIELD.LICENSE);
			cardPlateHome.setText(plateNumber);
		} else if (requestCode == 0 && resultCode == MainActivity.RESULTCODE_BACK) {
			//什么都不做
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(bitmap!=null){
			bitmap.recycle();
			bitmap = null;
		}
	}
}
