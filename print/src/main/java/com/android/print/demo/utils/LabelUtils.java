package com.android.print.demo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.android.print.demo.R;
import com.android.print.sdk.PrinterInstance;
import com.android.print.sdk.PrinterConstants.Command;

public class LabelUtils
{
	public void printLabel(PrinterInstance mPrinter)
	{

          mPrinter.printText(R.string.str_label+"");
          mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

		//数据模拟
	    String barcode="94478930500020125";
	    String userCode="044298";
	    String departmentCityName1="上海市";
	    String send="";
	    String destTransCenterName1="廊坊枢纽中";
	    String destinationName1="北京海淀区四季青营业部";



	    String pieces="0002/2";
	    String goodsType="";
	    String wrapType1="2纸";
	    String wblCode1="944789305";
	    String transType1="精准汽运";

		mPrinter.prn_PageSetup(700,576);


		// 边框
		int line_x1=5;
		int line_x2=636;
		int line_y1=1;
		int line_y2=101-5;
		int line_y3=199-5-5;
		int line_y4=252-5-5;
		int line_y5=305-5-5;
		int lineWidth=2;
		int split_x=113;

		// 添加线条, 参数与PDA上一致
		mPrinter.prn_DrawLine(lineWidth,line_x1,line_y1,line_x2,line_y1);
		mPrinter.prn_DrawLine(lineWidth,line_x1,line_y2,line_x2,line_y2);
		mPrinter.prn_DrawLine(lineWidth,line_x1,line_y3,line_x1+split_x*4,line_y3);
		mPrinter.prn_DrawLine(lineWidth,line_x1,line_y4,line_x2,line_y4);
		mPrinter.prn_DrawLine(lineWidth,line_x1,line_y5,line_x2,line_y5);

		mPrinter.prn_DrawLine(lineWidth,line_x1,line_y1,line_x1,line_y5);//侧边两列下拉
		mPrinter.prn_DrawLine(lineWidth,line_x2,line_y1,line_x2,line_y5);//侧边两列下拉

		mPrinter.prn_DrawLine(lineWidth,line_x1+split_x  ,line_y3+lineWidth,line_x1+split_x  ,line_y5);
		mPrinter.prn_DrawLine(lineWidth,line_x1+split_x*2,line_y3+lineWidth,line_x1+split_x*2,line_y5);
		mPrinter.prn_DrawLine(lineWidth,line_x1+split_x*3,line_y3+lineWidth,line_x1+split_x*3,line_y5);
		mPrinter.prn_DrawLine(lineWidth,line_x1+split_x*4,line_y1          ,line_x1+split_x*4,line_y5);


		mPrinter.prn_DrawText(15,305,"德邦物流","宋体",32,0,0,1,0);


		//添加条码
		int barcodetype=12; // 这里不要改---表示code-128编码格式
		mPrinter.prn_DrawBarcode(185, 300, barcode, 12, 0, 0, 80);
		// 条码下方的数字
		mPrinter.prn_DrawText(232, 385, barcode, "宋体", 23, 0, 0, 0, 0);
		mPrinter.prn_DrawText(35, 340, userCode, "宋体", 23, 0, 0, 0, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		//添加标签打印时间
		mPrinter.prn_DrawText(20, 365, sdf.format(new Date()), "宋体", 23, 0, 0, 0, 0);
		//添加出发外场所属城市
		int iLenght = departmentCityName1.length();
		if (iLenght<3) {

			mPrinter.prn_DrawText(8,130,departmentCityName1,"",30,0,0,0,0);

		}else if(iLenght==3){
			String str1 = departmentCityName1.substring(0,2);
			String str2 = departmentCityName1.substring(2);
			mPrinter.prn_DrawText(8, 118, str1, "宋体", 30, 0, 0, 0, 0);
    		mPrinter.prn_DrawText(20, 147, str2, "宋体", 30, 0, 0, 0, 0);

		}else{ // 大于三个, 但最终只打印四个
			String str1 = departmentCityName1.substring(0,2);
			String str2 = departmentCityName1.substring(2,4);
			mPrinter.prn_DrawText(8,118,str1,"",30,0,0,0,0);
			mPrinter.prn_DrawText(8,147,str2,"",30,0,0,0,0);


		}
		// 添加送字 自提时不打印 收传入的数据决定
		mPrinter.prn_DrawText(3,20,send,"",30,0,0,0,0);

		//到达外场----长度在前面已经判断好了
		mPrinter.prn_DrawText(181, 15, destTransCenterName1, "宋体", 45, 0, 0, 0, 0);

		//目的站 大于六个字的时候字体小一号
		destinationName1 = "-"+ destinationName1;
		if (destinationName1.length()<=7) {
			mPrinter.prn_DrawText(14*8-35,125,destinationName1,"",45,0,0,0,0);
		}else{
			mPrinter.prn_DrawText(77, 125, destinationName1, "宋体", 35, 0, 0, 0, 0);
		}

			//中转外场编码  最多四个
			mPrinter.prn_DrawText(18+110*0, 192, "D02", "黑体", 45, 0, 0, 0, 0);
    		mPrinter.prn_DrawText(18+110*0, 245, "27", "黑体", 45, 0, 0, 0, 0);

    		mPrinter.prn_DrawText(18+110*1, 192, "D03", "黑体", 45, 0, 0, 0, 0);
    		mPrinter.prn_DrawText(18+110*1, 245, "510", "黑体", 45, 0, 0, 0, 0);


		//件数---这里是一个字体串  如  0001/5  一次性生成
		mPrinter.prn_DrawText(472, 10, pieces, "宋体", 35, 0, 0, 0, 0);


		//包装   只取8小字符

		if (wrapType1.length()>7) {
			wrapType1 = wrapType1.substring(0, 7);
		}
		mPrinter.prn_DrawText(472, 55, wrapType1, "宋体", 30, 0, 0, 0, 0);

		//运单号,运单号分两部分，第二行显示四位,或者五位，第一行根据运单号个数自动扩展

		String b1 = wblCode1.substring(0,5);
		String b2 = wblCode1.substring(5);
		mPrinter.prn_DrawText(488, 120, b1, "黑体", 50, 0, 0, 0, 0);
		mPrinter.prn_DrawText(488, 190, b2, "黑体", 50, 0, 0, 0, 0);

		//运输性质---大于四个字的打大一点
		if (transType1.length()>4) {
			mPrinter.prn_DrawText(58*8-2,242,transType1,"",30,0,0,0,1);

		}else{
			mPrinter.prn_DrawText(462, 252, transType1, "宋体", 25, 0, 0, 0, 1);
		}

		// 执行打印
		mPrinter.prn_PagePrint(1);

		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);


	}



}
