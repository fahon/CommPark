package w.song.orchid.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

public class ImageTool {
	// 切割图片的一部分
	public static Bitmap imageCut(Bitmap bmp, int left, int top, int right,
			int bottom) {
		Bitmap resultBmp = null;
		int resultWidth = right - left + 1;
		int resultHeight = bottom - top + 1;
		int colors[] = new int[resultWidth * resultHeight];
		bmp.getPixels(colors, 0, resultWidth, left, top, resultWidth,
				resultHeight);
		if (bmp.isRecycled())
			bmp.recycle();
		resultBmp = Bitmap.createBitmap(colors, resultWidth, resultHeight,
				Bitmap.Config.ARGB_8888);
		return resultBmp;
	}

	// 把图片按固定比例缩小
	public static Bitmap imageZoom(Bitmap bmp, int iWidth, int iHeight) {
		Bitmap newBmp = null;
		int imageHeight = bmp.getHeight();
		int imageWidth = bmp.getWidth();
		float scaleW = 1;
		float scaleH = 1;
		double scalex = (float) iWidth / imageWidth;
		double scaley = (float) iHeight / imageHeight;
		scaleW = (float) (scaleW * scalex);
		scaleH = (float) (scaleH * scaley);
		Matrix matrix = new Matrix();
		matrix.postScale(scaleW, scaleH);
		newBmp = Bitmap.createBitmap(bmp, 0, 0, imageWidth, imageHeight,
				matrix, true);
		// if (bmp.isRecycled())
		// bmp.recycle();

		return newBmp;
	}

	// 图片合并
	public static Bitmap imageMerge(Bitmap[] bmps) {
		Bitmap resultBmp = null;
		// 判断bmps中有没有值为null的bmp，有的话将其初始化为一张960*80的空白图片
		int bmp_null[] = new int[960 * 80];
		for (int i = 0; i < 960 * 80; i++)
			bmp_null[i] = 0xffffffff;
		for (int i = 0; i < bmps.length; i++) {
			if (bmps[i] == null) {
				bmps[i] = Bitmap.createBitmap(960, 80, Bitmap.Config.ARGB_8888);
				bmps[i].setPixels(bmp_null, 0, 960, 0, 0, 960, 80);
			}
		}
		int devideLineHeight = 5;
		int resultWidth = 0; // 合并完的图像的宽度；
		int resultHeight = 0; // 合并完的图像的高度；
		for (int i = 0; i < bmps.length; i++) {
			if (i != (bmps.length - 1)) {
				if (bmps[i].getWidth() > resultWidth)
					resultWidth = bmps[i].getWidth();
				resultHeight = resultHeight + bmps[i].getHeight()
						+ devideLineHeight; // 不同图片之间用一条宽度为5像素的红线隔开
			} else {
				if (bmps[i].getWidth() > resultWidth)
					resultWidth = bmps[i].getWidth();
				resultHeight = resultHeight + bmps[i].getHeight();
			}
		}
		resultWidth = 960; // 暂时将宽度定义为960
		Log.i("Width & Height", "******* " + resultWidth + " * " + resultHeight
				+ " ********");
		// 定义各图片之间分割线的像素数组
		int devideLine[] = new int[resultWidth * devideLineHeight];
		for (int i = 0; i < resultWidth * devideLineHeight; i++)
			devideLine[i] = 0xffff0000;
		// 生成了一张resultWidth*resultHeight的黑色图片
		resultBmp = Bitmap.createBitmap(resultWidth, resultHeight,
				Bitmap.Config.ARGB_8888);
		// 为了识别方便，将这张黑色图片变为纯白色
		int originalColors[] = new int[resultWidth * resultHeight];
		for (int i = 0; i < resultWidth * resultHeight; i++)
			originalColors[i] = 0xffffffff;
		resultBmp.setPixels(originalColors, 0, resultWidth, 0, 0, resultWidth,
				resultHeight); // 一张纯白色的图片
		// 下面的循环用图片数组中各个图片的像素数组及分割线的像素数组填充最终图片
		int y = 0;
		int len = bmps.length;
		for (int i = 0; i < len; i++) {
			int iWidth = bmps[i].getWidth();
			int iHeight = bmps[i].getHeight();
			int colors[] = new int[iWidth * iHeight];
			bmps[i].getPixels(colors, 0, iWidth, 0, 0, iWidth, iHeight);
			if (i != (len - 1)) {
				resultBmp.setPixels(colors, 0, iWidth, 0, y, iWidth, iHeight);
				y = y + iHeight;
				resultBmp.setPixels(devideLine, 0, resultWidth, 0, y,
						resultWidth, devideLineHeight);
				y = y + devideLineHeight;
			} else {
				resultBmp.setPixels(colors, 0, iWidth, 0, y, iWidth, iHeight);
			}
			if (!bmps[i].isRecycled())
				bmps[i].recycle();
		}
		return resultBmp;
	}

	// 保存图片
	public static void saveImage(Bitmap bmp, String path, String filename,
			int quality) {
		String time = callTime();
		if (bmp != null) {
			try {
				/* 文件不存在就创建 */
				Log.v("url===", Environment.getExternalStorageDirectory()
						.getAbsolutePath());
				File f = new File(Environment.getExternalStorageDirectory(),
						path);
				if (!f.exists()) {
					f.mkdir();
				}
				/* 保存相片文件 */
				File n = null;
				// if(path.equals("MyPhoto"))
				// {
				// n = new File(f, filename + Constant.cntOfMyPhoto + ".jpg");
				// Constant.cntOfMyPhoto ++;
				// }
				// else
				// {
				// n = new File(f, filename + Constant.cntOfTemp + ".jpg");
				// Constant.cntOfTemp ++;
				// }
				// Constant.fileNameOfTemp = filename + Constant.cntOfTemp;
				n = new File(f, filename + time + ".jpg");
				// Constant.fileNameOfTemp = filename + time;
				FileOutputStream bos = new FileOutputStream(n.getAbsolutePath());
				/* 文件转换 */
				bmp.compress(Bitmap.CompressFormat.JPEG, quality, bos);
				/* 调用flush()方法，更新BufferStream */
				bos.flush();
				/* 结束OutputStream */
				bos.close();
				// Constant.upLoadFile = "/sdcard/MyPhoto/" + filename + sTime +
				// ".jpg";
				// Constant.upLoadFile = "/sdcard/MyPhoto/" + filename + time +
				// ".jpg";
				// Constant.newFileName = filename + time + ".jpg";
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (bmp.isRecycled())
				bmp.recycle();
		}
	}

	// 重置选择框的初始位置
	public static void resetRect() {
		// Constant.left = 200;
		// Constant.top = 140;
		// Constant.right = 280;
		// Constant.bottom = 170;
	}

	// 获取系统时间
	public static String callTime() {
		long backTime = new Date().getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(backTime));
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int date = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		String time = "" + year + month + date + hour + minute + second;
		Log.i("CurrentTime", "^^^^^^^^^^^^^" + time + "^^^^^^^^^^^^^");
		return time;
		// return date + 100 * (month + 1) + 10000 * year;
	}
}
