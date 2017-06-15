package com.android.print.demo.utils;

import com.android.print.demo.R;
import com.android.print.sdk.CanvasPrint;
import com.android.print.sdk.FontProperty;
import com.android.print.sdk.PrinterInstance;
import com.android.print.sdk.PrinterType;
import com.android.print.sdk.PrinterConstants.Command;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.media.ThumbnailUtils;

public class ImageAndCanvasUtils
{

	public void printImage(Resources resources,
			PrinterInstance mPrinter, boolean isStylus) {
		mPrinter.init();

		mPrinter.setFont(0, 0, 0, 0);
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		mPrinter.printText(resources.getString(R.string.str_image));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

		Bitmap bitmap1 = BitmapFactory.decodeResource(resources,
				R.drawable.image_logo1);



		//Bitmap bitmap=convertToBlackWhite(bmp);

		if (isStylus) {

			mPrinter.printImageStylus(bitmap1, 1);

		} else {

			mPrinter.printImage(bitmap1);

		}
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2); // 换2行
	}



	public void printCustomImage(Resources resources,
			PrinterInstance mPrinter, boolean isStylus, boolean is58mm) {
		mPrinter.init();

		mPrinter.setFont(0, 0, 0, 0);
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);

		mPrinter.printText(resources.getString(R.string.str_canvas));

		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

		CanvasPrint cp = new CanvasPrint();
		/*
		 * 初始化画布，画布的宽度为变量，一般有两个选择： 1、58mm型号打印机实际可用是48mm，48*8=384px
		 * 2、80mm型号打印机实际可用是72mm，72*8=576px 因为画布的高度是无限制的，但从内存分配方面考虑要小于4M比较合适，
		 * 所以预置为宽度的5倍。 初始化画笔，默认属性有： 1、消除锯齿 2、设置画笔颜色为黑色
		 */
		// init 方法包含cp.initCanvas(550)和cp.initPaint(), T9打印宽度为72mm,其他为47mm.
		if (isStylus) {
			cp.init(PrinterType.T5);
		} else {
			if (is58mm) {
				cp.init(PrinterType.TIII);
			} else {
				cp.init(PrinterType.T9);
			}
		}

		// 非中文使用空格分隔单词
		cp.setUseSplit(true);
		// cp.setUseSplitAndString(true, " ");
		// 阿拉伯文靠右显示
		cp.setTextAlignRight(true);
		/*
		 * 插入图片函数: drawImage(float x, float y, String path)
		 * 其中(x,y)是指插入图片的左上顶点坐标。
		 */
		FontProperty fp = new FontProperty();
		fp.setFont(false, false, false, false, 25, null);
		// 通过初始化的字体属性设置画笔
		cp.setFontProperty(fp);
		cp.drawText("Contains English language:");
		fp.setFont(false, false, false, false, 30, null);
		cp.setFontProperty(fp);
		cp.drawText("开始打印简体中文开始打印sline: mở nhiều cuộc không mở This is english and test and test and test");
		cp.drawText("开始打印简体中文开始打印sline: mở nhiều cuộc không mở This is english wording111111");


		cp.drawImage(BitmapFactory.decodeResource(resources,
				R.drawable.my_picture));

		mPrinter.printText("Print Custom Image:\n");
		if (isStylus) {
			// 针打图形,第二个参数为0倍高倍宽， 为1只倍高
			mPrinter.printImageStylus(cp.getCanvasImage(), 1);
		} else {
			mPrinter.printImage(cp.getCanvasImage());
		}

		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
	}











	/**
	 * 将彩色图转换为黑白图
	 *
	 * @param 位图
	 * @return 返回转换好的位图
	 */
	public static Bitmap convertToBlackWhite(Bitmap bmp) {
		int width = bmp.getWidth(); // 获取位图的宽
		int height = bmp.getHeight(); // 获取位图的高
		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];

				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);

		newBmp.setPixels(pixels, 0, width, 0, 0, width, height);

		Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, 492, 297);
		return resizeBmp;
	}


}
