package willsong.cn.commpark.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ice.ice_plate.*;
import com.ice.iceplate.ActivateService;
import com.ice.iceplate.R;

import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;

public class IcePlateMainActivity extends Activity implements OnClickListener {

	private Button btnPic;
	private Button btnVedio;
	private Button btnActivate;
	private EditText editText;
	private boolean recogMode;

	public ActivateService.ActivateBinder acBinder;
	public ServiceConnection acConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			acConnection = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			acBinder = (ActivateService.ActivateBinder) service;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//禁用系统按键
        SharedPreferencesConfig.closeHome(IcePlateMainActivity.this,true);

		Intent actiIntent = new Intent(IcePlateMainActivity.this,ActivateService.class);
		bindService(actiIntent, acConnection, Service.BIND_AUTO_CREATE);

		setViews();
		setListeners();


	}


	private void setListeners() {
		btnPic.setOnClickListener(this);
		btnVedio.setOnClickListener(this);
		btnActivate.setOnClickListener(this);
	}

	private void setViews() {
		btnPic = (Button) findViewById(R.id.btn_by_photo);
		btnVedio = (Button) findViewById(R.id.btn_by_vedio);
		btnActivate = (Button) findViewById(R.id.btn_activate);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, com.ice.ice_plate.MemoryCameraActivity.class);

		int i = v.getId();
		if (i == R.id.btn_by_photo) {
			recogMode = false;
			intent.putExtra("camera", recogMode);
			startActivity(intent);

		} else if (i == R.id.btn_by_vedio) {
			recogMode = true;
			intent.putExtra("camera", recogMode);
			startActivity(intent);

		} else if (i == R.id.btn_activate) {
			activateSN();
		}
	}

	private void activateSN() {
		editText = new EditText(this);
		new AlertDialog.Builder(this).setTitle(R.string.dialog_title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(editText)
				.setCancelable(false)
				.setPositiveButton(R.string.license_verification, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String snInput = editText.getText().toString().trim();
						int code = -1;
						try {
							code = acBinder.login(snInput);
						}catch(Exception e){
							code = 0;
						}
						if(code == 0){
							new AlertDialog.Builder(IcePlateMainActivity.this).setMessage("恭喜,程序激活成功!").show();
							SharedPreferencesConfig.saveStringConfig(IcePlateMainActivity.this,"ActivateCode",snInput);
						}else if(code == 1795){
							new AlertDialog.Builder(IcePlateMainActivity.this).setMessage("程序激活失败,激活的机器数量已达上限，授权码不能在更多的机器上使用").show();
						}else if(code == 1793){
							new AlertDialog.Builder(IcePlateMainActivity.this).setMessage("程序激活失败,授权码已过期").show();
						}else if(code == 276){
							new AlertDialog.Builder(IcePlateMainActivity.this).setMessage("程序激活失败,没有找到相应的本地授权许可数据文件").show();
						}else if(code == 284){
							new AlertDialog.Builder(IcePlateMainActivity.this).setMessage("程序激活失败,授权码输入错误，请检查授权码拼写是否正确").show();
						}else{
							new AlertDialog.Builder(IcePlateMainActivity.this).setMessage("程序激活失败,错误码为："+ code).show();
						}
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
						dialog.dismiss();

					}
				})
				.setNegativeButton(R.string.disactivate, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
						dialog.dismiss();
						Toast.makeText(IcePlateMainActivity.this, "取消了授权动作", Toast.LENGTH_SHORT).show();
					}
				}).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (acBinder != null) {
			unbindService(acConnection);
		}
	}


}
