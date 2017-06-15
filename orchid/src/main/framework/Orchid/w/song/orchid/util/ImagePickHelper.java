package w.song.orchid.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import willsong.cn.orchid.R;

public class ImagePickHelper {
	private Context context;
	protected static final int PICK_FROM_FILE = 9;
	protected static final int PICK_FROM_CAMERA = 10;
	protected static final int CROP_FROM_DATA = 11;

	private final static String TAG = "ImagePickHelper";

	private static final String TEMP_PHOTO_FILE = "tempPhoto.jpg";

	public int outputX = 800;
	public int outputY = 800;
	public int aspectX = 4;
	public int aspectY = 4;
	protected boolean faceDetection = true;

	protected boolean cropFlag = true;

	private OnPickFinishedListener onPickFinishedListener;

	public interface OnPickFinishedListener {
		void onPickFinished(Bitmap photo);
	}

	public ImagePickHelper(Context context, OnPickFinishedListener onPickFinishedListener) {
		this.context = context;
		this.onPickFinishedListener = onPickFinishedListener;
	}

	public void starPick() {
		String[] ss = new String[] { "选择已有照片", "启动摄像头拍照" };
		new AlertDialog.Builder(context).setTitle("来源选择").setItems(ss, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (which == 0) {
					starPickFromFile();
				} else if (which == 1) {
					starPickFromCamera();
				}
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int which) {
				d.dismiss();
			}
		}).show();

	}

	public void starPickFromFile() {
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			((Activity) context).startActivityForResult(intent, PICK_FROM_FILE);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, "没有图片选择程序", Toast.LENGTH_LONG).show();
		}
	}

	public void starPickFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
		try {
			intent.putExtra("return-data", true);
			((Activity) context).startActivityForResult(intent, PICK_FROM_CAMERA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, "没有拍照程序", Toast.LENGTH_LONG).show();
		}
	}

	private Uri getTempUri() {
		return Uri.fromFile(getTempFile());
	}

	private File getTempFile() {
		if (isSDCARDMounted()) {

			File f = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE);
			try {
				f.createNewFile();
			} catch (IOException e) {
				Toast.makeText(context, "创建临时文件出错", Toast.LENGTH_LONG).show();
			}
			return f;
		} else {
			return null;
		}
	}

	private boolean isSDCARDMounted() {
		String status = Environment.getExternalStorageState();

		return status.equals(Environment.MEDIA_MOUNTED);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CROP_FROM_DATA: {
			final Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				if (onPickFinishedListener != null) {
					onPickFinishedListener.onPickFinished(photo);
				}
			}
			File f = getTempFile();
			if (f.exists()) {
				f.delete();
			}

			break;
		}
		case PICK_FROM_CAMERA: {
			File f = getTempFile();
			if (f.length() < 1) {
				Toast.makeText(context, "获取拍照图片失败", Toast.LENGTH_SHORT).show();
				break;
			}
			processCrop(getTempUri());
			break;
		}
		case PICK_FROM_FILE: {
			if (data == null || data.getData() == null) {
				Log.w(TAG, "Null data, but RESULT_OK, from image picker!");
				Toast.makeText(context, "选择图片失败", Toast.LENGTH_SHORT).show();
				return;
			}
			processCrop(data.getData());
			break;
		}
		}
	}

	private void processCrop(final Uri fileUri) {
		try {
			if (!cropFlag) {
				noCrop(fileUri);
				return;
			}

			final List<CropOption> cropOptions = new ArrayList<CropOption>();

			// this 2 lines are all you need to find the intent!!!
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setType("image/*");

			List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
			if (list.size() == 0) {
				Toast.makeText(context, "没有找到图片裁剪程序", Toast.LENGTH_LONG);
			}

			intent.setData(fileUri);
			intent.putExtra("outputX", outputX);
			intent.putExtra("outputY", outputY);
			intent.putExtra("aspectX", aspectX);
			intent.putExtra("aspectY", aspectY);
			intent.putExtra("scale", true);
			intent.putExtra("noFaceDetection", !faceDetection);
			intent.putExtra("return-data", true);

			for (ResolveInfo res : list) {
				final CropOption co = new CropOption();
				co.TITLE = context.getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
				co.ICON = context.getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
				co.CROP_APP = new Intent(intent);
				co.CROP_APP.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
				
				if(res.activityInfo.packageName.contains("taobao")==false){
					cropOptions.add(co);
				}
			}

			// set up the chooser dialog
			CropOptionAdapter adapter = new CropOptionAdapter(context, cropOptions);
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("选择裁剪程序");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					try{
						((Activity) context).startActivityForResult(cropOptions.get(item).CROP_APP, CROP_FROM_DATA);
					}catch(Exception e){
						dialog.dismiss();
						Toast.makeText(context, cropOptions.get(item).TITLE+"图片裁剪异常,请选择其他图片裁剪程序!", Toast.LENGTH_LONG).show();
					}				
				}
			});
			builder.setNegativeButton("不进行裁剪", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int which) {
					d.dismiss();
					noCrop(fileUri);
				}

			});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			Log.e(TAG, "processing capture", e);
		}
	}

	private void noCrop(final Uri fileUri) {
		Bitmap largePhoto = null;
		try {
			largePhoto = decodeFile(fileUri);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (largePhoto == null) {
			Toast.makeText(context, "加载图片出错", Toast.LENGTH_LONG).show();
			return;
		}
		int width = largePhoto.getWidth();
		int height = largePhoto.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) outputX) / width;
		if (scaleWidth > 1) {
			scaleWidth = 1;
		}
		// float scaleHeight = ((float) outputY) / height;
		float scaleHeight = scaleWidth;// 适配宽度等比缩放
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap photo = Bitmap.createBitmap(largePhoto, 0, 0, width, height, matrix, true);
		if (onPickFinishedListener != null) {
			onPickFinishedListener.onPickFinished(photo);
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(Uri fileUri) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(context.getContentResolver().openInputStream(fileUri), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 500;

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(fileUri), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// this is something to keep our information
	class CropOption {
		CharSequence TITLE;
		Drawable ICON;
		Intent CROP_APP;
	}

	// we will present the available selection in a list dialog, so we need an adapter
	class CropOptionAdapter extends ArrayAdapter<CropOption> {
		private List<CropOption> _items;
		private Context _ctx;

		CropOptionAdapter(Context ctx, List<CropOption> items) {
			super(ctx, R.layout.crop_option, items);
			_items = items;
			_ctx = ctx;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = LayoutInflater.from(_ctx).inflate(R.layout.crop_option, null);

			CropOption item = _items.get(position);
			if (item != null) {
				((ImageView) convertView.findViewById(R.id.crop_icon)).setImageDrawable(item.ICON);
				((TextView) convertView.findViewById(R.id.crop_name)).setText(item.TITLE);
				return convertView;
			}
			return null;
		}
	}
}
