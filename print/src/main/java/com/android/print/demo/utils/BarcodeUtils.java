package com.android.print.demo.utils;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.android.print.demo.R;
import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants.BarcodeType;
import com.android.print.sdk.PrinterConstants.Command;
import com.android.print.sdk.PrinterInstance;

public class BarcodeUtils
{
	private int bCodeid=0;
	private int bTypeid=1;
	private String barName;

	 public void selectBarCode(final Context context, final PrinterInstance mPrinter)
     {
    	 LayoutInflater inflater = LayoutInflater.from(context);
         View BarcodeView = inflater.inflate(R.layout.barcode, null);
         final Spinner code_types = (Spinner)BarcodeView.findViewById(R.id.code_types);
         final RadioButton bar_type1=(RadioButton)BarcodeView.findViewById(R.id.bar_type_1);
         final RadioButton bar_type2=(RadioButton)BarcodeView.findViewById(R.id.bar_type_2);

         spinnerFl(code_types,context);

         bar_type1.setOnClickListener(new OnClickListener()
         {

			@Override
			public void onClick(View v)
			{

				    bTypeid=1;
                	bar_type1.setChecked(true);
                	bar_type2.setChecked(false);
                	  spinnerFl(code_types,context);


			}});

         bar_type2.setOnClickListener(new OnClickListener()
         {

			@Override
			public void onClick(View v)
			{

				    bTypeid=2;
                	bar_type2.setChecked(true);
                	bar_type1.setChecked(false);
                    spinnerFl(code_types,context);


			}});

         code_types.setPromptId(R.string.barcode);


         code_types.setOnItemSelectedListener(new OnItemSelectedListener()
         {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3)
			{
				bCodeid=arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}});



         final AlertDialog.Builder builder = new AlertDialog.Builder(context);
         builder.setCancelable(false);
         builder.setTitle(R.string.barcode);
         builder.setView(BarcodeView);

         builder.setPositiveButton(R.string.print, new DialogInterface.OnClickListener()
         {


 			@Override
 			public void onClick(DialogInterface dialog, int which)
 			{
                byte type=0;
                int num=0;
                int num1=0;
 				if(bTypeid==1)
                {
 					num=150;
 					num1=2;
                	switch(bCodeid)
                	{
                	   case 0:
                		   type=BarcodeType.CODE39;
                		   barName="BarcodeType.CODE39";
                		   break;
                	   case 1:
                		   type=BarcodeType.CODABAR;
                		   barName="BarcodeType.CODABAR";
                		   break;
                	   case 2:
                		   type=BarcodeType.ITF;
                		   barName="BarcodeType.ITF";

                		   break;
                	   case 3:
                		   type=BarcodeType.CODE93;
                		   barName="BarcodeType.CODE93";
                		   break;
                	   case 4:
                		   type=BarcodeType.CODE128;
                		   barName="BarcodeType.CODE128";
                		   break;
                	   case 5:
                		   type=BarcodeType.UPC_A;
                		   barName="BarcodeType.UPC_A";
                		   break;
                	   case 6:
                		   type=BarcodeType.UPC_E;
                		   barName="BarcodeType.UPC_E";
                		   break;
                	   case 7:
                		   type=BarcodeType.JAN13;
                		   barName="BarcodeType.JAN13";
                		   break;
                	   case 8:
                		   type=BarcodeType.JAN8;
                		   barName="BarcodeType.JAN8";
                		   break;
                	   case 9:
                		   type=100;
                		   barName="All Types";
                		   break;
                	}
                }else
                {
                	num=3;
                	num1=6;
                	switch(bCodeid)
                	{
                	   case 0:
                		   type=BarcodeType.PDF417;
                		   barName="BarcodeType.PDF417";
                		   break;
                	   case 1:
                		   type=BarcodeType.QRCODE;
                		   barName="BarcodeType.QRCODE";
                		   break;
                	   case 2:
                		   type=BarcodeType.DATAMATRIX;
                		   barName="BarcodeType.DATAMATRIX";
                		   break;
                	   case 3:
                		   type=100;
                		   barName="All Types";
                		   break;

                	}
                }

 				mPrinter.printText(context.getResources().getString(R.string.print)+barName+context.getResources().getString(R.string.str_show));
 				mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
 				if(type==BarcodeType.UPC_A || type==BarcodeType.UPC_E || type==BarcodeType.JAN13 )
 				{
 					Barcode barcode = new Barcode(type, 2, 63, 2, "000000000000");
   				    mPrinter.printBarCode(barcode);
 				}else if(type==BarcodeType.JAN8)
 				{
 					Barcode barcode = new Barcode(type, 2, 63, 2, "0000000");
   				    mPrinter.printBarCode(barcode);
 				}else if(type==100)
 				{
                    if(bTypeid==1)
                    {
                    	mPrinter.printText(context.getResources().getString(R.string.print)+"BarcodeType.CODE39"+context.getResources().getString(R.string.str_show));
         				mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
         				Barcode barcode0 = new Barcode(BarcodeType.CODE39, 2, 150, 2, "123456");
       				    mPrinter.printBarCode(barcode0);
       				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

       				   mPrinter.printText(context.getResources().getString(R.string.print)+" BarcodeType.CODABAR"+context.getResources().getString(R.string.str_show));
      				   mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
      				    Barcode barcode1 = new Barcode(BarcodeType.CODABAR, 2, 150, 2, "123456");
    				    mPrinter.printBarCode(barcode1);
    				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

    				    mPrinter.printText(context.getResources().getString(R.string.print)+"BarcodeType.ITF"+context.getResources().getString(R.string.str_show));
       				   mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
       				    Barcode barcode2 = new Barcode(BarcodeType.ITF, 2, 150, 2, "123456");
     				    mPrinter.printBarCode(barcode2);
     				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

     				   mPrinter.printText(context.getResources().getString(R.string.print)+"BarcodeType.CODE93"+context.getResources().getString(R.string.str_show));
       				   mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
       				    Barcode barcode3 = new Barcode(BarcodeType.CODE93, 2, 150, 2, "123456");
     				    mPrinter.printBarCode(barcode3);
     				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

     				   mPrinter.printText(context.getResources().getString(R.string.print)+" BarcodeType.CODE128"+context.getResources().getString(R.string.str_show));
       				   mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
       				    Barcode barcode4 = new Barcode(BarcodeType.CODE128, 2, 150, 2, "123456");
     				    mPrinter.printBarCode(barcode4);
     				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

     				   mPrinter.printText(context.getResources().getString(R.string.print)+" BarcodeType.UPC_A"+context.getResources().getString(R.string.str_show));
       				   mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
       				    Barcode barcode5 = new Barcode(BarcodeType.UPC_A, 2, 63, 2, "000000000000");
     				    mPrinter.printBarCode(barcode5);
     				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

     				   mPrinter.printText(context.getResources().getString(R.string.print)+"BarcodeType.UPC_E"+context.getResources().getString(R.string.str_show));
       				   mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
       				    Barcode barcode6 = new Barcode(BarcodeType.UPC_E, 2, 63, 2, "000000000000");
     				    mPrinter.printBarCode(barcode6);
     				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

     				   mPrinter.printText(context.getResources().getString(R.string.print)+"BarcodeType.JAN13"+context.getResources().getString(R.string.str_show));
       				   mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
       				    Barcode barcode7 = new Barcode(BarcodeType.JAN13, 2, 63, 2, "000000000000");
     				    mPrinter.printBarCode(barcode7);
     				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

     				   mPrinter.printText(context.getResources().getString(R.string.print)+"BarcodeType.JAN8"+context.getResources().getString(R.string.str_show));
       				   mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
       				    Barcode barcode8 = new Barcode(BarcodeType.JAN8, 2, 63, 2, "0000000");
     				    mPrinter.printBarCode(barcode8);
     				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);


                    }else
                    {
                    	mPrinter.printText(context.getResources().getString(R.string.print)+"BarcodeType.PDF417"+context.getResources().getString(R.string.str_show));
         				mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
         				Barcode barcode0 = new Barcode(BarcodeType.PDF417, 2,3, 6,  "123456wwww");
       				    mPrinter.printBarCode(barcode0);
       				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

       				    mPrinter.printText(context.getResources().getString(R.string.print)+" BarcodeType.QRCODE"+context.getResources().getString(R.string.str_show));
      				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
      				    Barcode barcode1 = new Barcode(BarcodeType.QRCODE, 2,3, 6,  "123456aaaa");
    				    mPrinter.printBarCode(barcode1);
    				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

    				    mPrinter.printText(context.getResources().getString(R.string.print)+"BarcodeType.DATAMATRIX"+context.getResources().getString(R.string.str_show));
      				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
      				    Barcode barcode2 = new Barcode(BarcodeType.DATAMATRIX, 2,3, 6,  "123456cccc");
    				    mPrinter.printBarCode(barcode2);
    				    mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);


                    }
 				}
 				else
 				{
  				   Barcode barcode = new Barcode(type, 2, num, num1, "123456ggg");
  				   mPrinter.printBarCode(barcode);
 				}

 				mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

 				try
 				{
 				    Field field = dialog.getClass()
 				            .getSuperclass().getDeclaredField(
 				                     "mShowing" );
 				    field.setAccessible( true );
 				     //   将mShowing变量设为false，表示对话框已关闭
 				    field.set(dialog, false );


 				}
 				catch (Exception e)
 				{

 				}


 			}});

         builder.setNegativeButton("返回",new DialogInterface.OnClickListener()
         {


 			@Override
 			public void onClick(DialogInterface dialog, int which)
 			{
 				  Field field;
				try {
					field = dialog.getClass()
					            .getSuperclass().getDeclaredField(
					                     "mShowing" );

					field.setAccessible( true );
				     //   将mShowing变量设为false，表示对话框已关闭
				    field.set(dialog, true );
				    dialog.dismiss();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

 			}
 		});

         builder.create();
         final AlertDialog dialog=builder.show();


     }

      public void spinnerFl(Spinner code_types,Context context)
      {
    	 if(bTypeid==1)
    	 {
    	  ArrayAdapter<CharSequence> codetypes =ArrayAdapter.createFromResource(context,R.array.barcode1, android.R.layout.simple_spinner_item);
          code_types.setAdapter(codetypes);
    	 }else
    	 {
    		 ArrayAdapter<CharSequence> codetypes =ArrayAdapter.createFromResource(context,R.array.barcode2, android.R.layout.simple_spinner_item);
             code_types.setAdapter(codetypes);
    	 }
      }



}
