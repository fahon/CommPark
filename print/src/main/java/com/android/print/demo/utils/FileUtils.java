package com.android.print.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.print.demo.MyAdapter;
import com.android.print.demo.R;
import com.android.print.sdk.PrinterInstance;
import com.android.print.sdk.PrinterConstants.Command;

public class FileUtils
{
	private TextView mPath;
	private String rootPath = "/";
	private String defaultPath = "/sdcard/download";
	private String dir;
	private String filePath;
	private File parentFile;
	private static List<String> items = null;
	private static List<String> paths = null;

      public void selectFile(final ListActivity ma,final View v1)
      {
			ma.setContentView(R.layout.file_explorer);
			mPath = (TextView) ma.findViewById(R.id.mPath);
			Button fileBack=(Button) ma.findViewById(R.id.button_bace);
			fileBack.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					ma.setContentView(v1);

				}

			});

			try {
				if (mPath != null) {
					filePath = mPath.getText().toString();
					if (!filePath.isEmpty()) {
						parentFile = new File(filePath).getParentFile();

						if (parentFile.exists()) {
							dir = parentFile.getAbsolutePath();
						}
					} else {
						dir = defaultPath;
					}
				} else {
					dir = defaultPath;
				}
			} catch (Exception ignore) {
			}
			getFileDir(dir,ma);
	   }


      private void getFileDir(String filePath,ListActivity ma)
      {
 		mPath.setText(filePath);
 		items = new ArrayList<String>();
 		paths = new ArrayList<String>();
 		File f = new File(filePath);
 		File[] files = f.listFiles();
 		if (!filePath.equals(rootPath)) {
 			items.add("back2root");
 			paths.add(rootPath);
 			items.add("back2up");
 			paths.add(f.getParent());
 		}
 		for (int i = 0; i < files.length; i++) {
 			File file = files[i];
 			items.add(file.getName());
 			paths.add(file.getPath());
 		}
 		 ma.setListAdapter(new MyAdapter(ma, items, paths));
 	}

      public void printFile(int position,ListActivity ma,PrinterInstance mPrinter)
      {
    	   	File file = new File(paths.get(position));
			String fName = file.getName();
			if (file.isDirectory())
				getFileDir(paths.get(position),ma);
			else if (fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase().equals("txt"))

				printFile(ma,file, mPrinter);
			else
				;
      }


      public static void printFile(Context context,File mFile, PrinterInstance mPrinter) {
		mPrinter.init();

		mPrinter.printText(context.getResources().getString(R.string.str_file));
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

		BufferedReader reader = null;

		try {
			InputStreamReader isr=new InputStreamReader(new FileInputStream(mFile),"gbk");
            reader = new BufferedReader(isr);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
        		mPrinter.printText(tempString+"\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
		mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2); // 换2行
	}




    //向SD卡写入文件
      public void createFile(Activity ma)
      {
    	  String fileDirPath = android.os.Environment
    	            .getExternalStorageDirectory().getAbsolutePath()// 得到外部存储卡的数据库的路径名
    	            + "/download";// 我要存储的目录
    	  String fileName = "filetest.txt";// 要存储的文件名

          String filePath = fileDirPath + "/" + fileName;// 文件路径


          File dir = new File(fileDirPath);
          if (!dir.exists())
          {
               dir.mkdirs();
          }

          try {

              File file = new File(filePath);
              if (!file.exists())
              {
                  InputStream ins = ma.getResources().openRawResource(
                          R.raw.filetest);

                  FileOutputStream fos = new FileOutputStream(file);

                  byte[] buffer = new byte[8192];
                  int count = 0;
                  while ((count = ins.read(buffer)) > 0) {
                      fos.write(buffer, 0, count);
                  }

                  fos.close();
                  ins.close();
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
      }


}
