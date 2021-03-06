package com.android.print.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import com.android.print.demo.R;
import com.android.print.sdk.Barcode;
import com.android.print.sdk.CanvasPrint;
import com.android.print.sdk.FontProperty;
import com.android.print.sdk.PrinterConstants.BarcodeType;
import com.android.print.sdk.PrinterConstants.Command;
import com.android.print.sdk.PrinterInstance;
import com.android.print.sdk.PrinterType;
import com.android.print.sdk.Table;

public class PrintUtils {

	public static void printText(Resources resources, PrinterInstance mPrinter) {
		mPrinter.init();


		mPrinter.printText(resources.getString(R.string.str_text));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);


		mPrinter.setFont(0, 0, 0, 0);
		mPrinter.setPrinter(Command.ALIGN, 0);
		mPrinter.printText(resources.getString(R.string.str_text_left));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);// 换2行


		mPrinter.setPrinter(Command.ALIGN, 1);
		mPrinter.printText(resources.getString(R.string.str_text_center));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);// 换2行

		mPrinter.setPrinter(Command.ALIGN, 2);
		mPrinter.printText(resources.getString(R.string.str_text_right));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3); // 换3行

		mPrinter.setPrinter(Command.ALIGN, 0);
		mPrinter.setFont(0, 0, 1, 0);
		mPrinter.printText(resources.getString(R.string.str_text_strong));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2); // 换2行

		mPrinter.setFont(0, 0, 0, 1);
		mPrinter.sendByteData(new byte[]{(byte)0x1C,(byte)0x21,(byte)0x80});
		mPrinter.printText(resources.getString(R.string.str_text_underline));
		mPrinter.sendByteData(new byte[]{(byte)0x1C,(byte)0x21,(byte)0x00});
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2); // 换2行

		mPrinter.setFont(0, 0, 0, 0);
		mPrinter.printText(resources.getString(R.string.str_text_height));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
		for(int i=0;i<4;i++)
	    {
	    	mPrinter.setFont(i, i, 0, 0);
	   		mPrinter.printText((i+1)+resources.getString(R.string.times));

	    }
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

		  for(int i=0;i<4;i++)
	       {


	    	mPrinter.setFont(i, i, 0, 0);
	   		mPrinter.printText(resources.getString(R.string.bigger)+(i+1)+resources.getString(R.string.bigger1));
	   		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

	       }

		    mPrinter.setFont(0, 0, 0, 0);
		    mPrinter.setPrinter(Command.ALIGN, 0);
		    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

	}










   public static void printNote(Resources resources, PrinterInstance mPrinter,
			boolean is58mm) {
		mPrinter.init();

		mPrinter.setFont(0, 0, 0, 0);
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		mPrinter.printText(resources.getString(R.string.str_note));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);


		StringBuffer sb = new StringBuffer();

		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
		mPrinter.setCharacterMultiple(1, 1);
		mPrinter.printText(resources.getString(R.string.shop_company_title)
				+ "\n");

		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		// 字号使用默认
		mPrinter.setCharacterMultiple(0, 0);
		sb.append(resources.getString(R.string.shop_num) + "574001\n");
		sb.append(resources.getString(R.string.shop_receipt_num)
				+ "S00003169\n");
		sb.append(resources.getString(R.string.shop_cashier_num)
				+ "s004_s004\n");

		sb.append(resources.getString(R.string.shop_receipt_date)
				+ "2012-06-17\n");
		sb.append(resources.getString(R.string.shop_print_time)
				+ "2012-06-17 13:37:24\n");
		mPrinter.printText(sb.toString()); // 打印

		printTable1(resources, mPrinter, is58mm); // 打印表格

		sb = new StringBuffer();
		if (is58mm) {
			sb.append(resources.getString(R.string.shop_goods_number)
					+ "                6.00\n");
			sb.append(resources.getString(R.string.shop_goods_total_price)
					+ "                35.00\n");
			sb.append(resources.getString(R.string.shop_payment)
					+ "                100.00\n");
			sb.append(resources.getString(R.string.shop_change)
					+ "                65.00\n");
		} else {
			sb.append(resources.getString(R.string.shop_goods_number)
					+ "                                6.00\n");
			sb.append(resources.getString(R.string.shop_goods_total_price)
					+ "                                35.00\n");
			sb.append(resources.getString(R.string.shop_payment)
					+ "                                100.00\n");
			sb.append(resources.getString(R.string.shop_change)
					+ "                                65.00\n");
		}

		sb.append(resources.getString(R.string.shop_company_name) + "\n");
		sb.append(resources.getString(R.string.shop_company_site)
				+ "www.jiangsuxxxx.com\n");
		sb.append(resources.getString(R.string.shop_company_address) + "\n");
		sb.append(resources.getString(R.string.shop_company_tel)
				+ "0574-12345678\n");
		sb.append(resources.getString(R.string.shop_Service_Line)
				+ "4008-123-456 \n");
		if (is58mm) {
			sb.append("==============================\n");
		} else {
			sb.append("==============================================\n");
		}
		mPrinter.printText(sb.toString());

		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);
		mPrinter.setCharacterMultiple(0, 1);
		mPrinter.printText(resources.getString(R.string.shop_thanks) + "\n");
		mPrinter.printText(resources.getString(R.string.shop_demo) + "\n\n\n");

		mPrinter.setFont(0, 0, 0, 0);
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

	}



	/*public static void printTable(Resources resources,
			PrinterInstance mPrinter, boolean is58mm) {
		mPrinter.init();

		mPrinter.setFont(0, 0, 0, 0);
		mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
		mPrinter.printText("打印表格效果演示：");
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

		// getTable方法:参数1,以特定符号分隔的列名; 2,列名分隔符;
		// 3,各列所占字符宽度,中文2个,英文1个. 默认字体总共不要超过48
		// 表格超出部分会另起一行打印.若想手动换行,可加\n.
		mPrinter.setCharacterMultiple(0, 0);
		String column = resources.getString(R.string.note_title);
		Table table;
		if (is58mm) {
			table = new Table(column, ";", new int[] { 14, 6, 6, 6 });
		} else {
			table = new Table(column, ";", new int[] { 16, 8, 8, 12 });
		}

		table.setColumnAlignRight(true);
		table.addRow("1," + resources.getString(R.string.coffee)
				+ ";2.00;5.00;10.00");
		table.addRow("2," + resources.getString(R.string.tableware)
				+ ";2.00;5.00;10.00");
		table.addRow("3," + resources.getString(R.string.frog)
				+ ";1.00;68.00;68.00");
		table.addRow("4," + resources.getString(R.string.cucumber)
				+ ";1.00;4.00;4.00");
		table.addRow("5," + resources.getString(R.string.peanuts)
				+ "; 1.00;5.00;5.00");
		table.addRow("6," + resources.getString(R.string.rice)
				+ ";1.00;2.00;2.00");
		mPrinter.printTable(table);

		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
	}*/

	public static void printTable1(Resources resources,
			PrinterInstance mPrinter, boolean is58mm) {
		mPrinter.init();
		String column = resources.getString(R.string.note_title);
		Table table;
		if (is58mm) {
			table = new Table(column, ";", new int[] { 14, 6, 6, 6 });
		} else {
			table = new Table(column, ";", new int[] { 18, 10, 10, 12 });
		}
		table.addRow("" + resources.getString(R.string.bags) + ";10.00;1;10.00");
		table.addRow("" + resources.getString(R.string.hook) + ";5.00;2;10.00");
		table.addRow("" + resources.getString(R.string.umbrella)
				+ ";5.00;3;15.00");
		mPrinter.printTable(table);
	}









}
