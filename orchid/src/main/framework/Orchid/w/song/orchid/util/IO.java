package w.song.orchid.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class IO {
	private String TAG = "IO";

	@SuppressWarnings("finally")
	public boolean saveObject(File file, Object obj) {
		boolean state = false;
		FileOutputStream fout = null;
		ObjectOutputStream out = null;
		try {
			fout = new FileOutputStream(file);
			out = new ObjectOutputStream(fout);
			out.writeObject(obj);
			state = true;
		} catch (FileNotFoundException e) {
			state = false;
			e.printStackTrace();
		} catch (IOException e) {
			state = false;
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
				if (fout != null)
					fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return state;
		}
	}
	
	/**
	 * 保存文件
	 * 保存时无需判断文件夹的存在已否，此过程该方法已封装
	 * @param fileP //文件夹
	 * @param fileT //文件
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("finally")
	public boolean saveObject(File fileP,File fileT, Object obj) {
		boolean state = false;
		FileOutputStream fout = null;
		ObjectOutputStream out = null;
		try {
			if(!fileP.exists()) {
				fileP.mkdirs();
			}
			fout = new FileOutputStream(fileT);
			out = new ObjectOutputStream(fout);
			out.writeObject(obj);
			state = true;
		} catch (FileNotFoundException e) {
			state = false;
			e.printStackTrace();
		} catch (IOException e) {
			state = false;
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
				if (fout != null)
					fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return state;
		}
	}

	/**
	 * 
	 * @param file
	 * @return null or Object
	 * @throws Exception
	 */
	public Object readObject(File file) {
		FileInputStream fin = null;
		ObjectInputStream in = null;
		Object obj = null;
		try {
			fin = new FileInputStream(file);
			in = new ObjectInputStream(fin);
			obj = in.readObject(); 
			in.close();
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (fin != null) {
					fin.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return obj;

	}

	/**
	 * 指定位置读取
	 * 
	 * @param file
	 * @param start
	 *            起始读取位置
	 * @param length
	 *            读取长度
	 * @return 返回文件字节数组
	 * @throws Exception
	 */
	public byte[] selectRead(File file, int start, int length) throws Exception {
		RandomAccessFile raf = null;
		byte[] bytes = null;
		try {
			raf = new RandomAccessFile(file, "r");
			raf.seek(start);
			bytes = new byte[length];
			raf.read(bytes, start, length);
			raf.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		} finally {
			try {
				if (raf != null)
					raf.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bytes;
	}

	public byte[] readFile(File file) throws Exception {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		byte[] bytes = null;
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			bytes = new byte[bis.available()];
			bis.read(bytes);
			bis.close();
			fis.close();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (fis != null)
					fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

	/**
	 * 对象转换成字节数组
	 * 
	 * @param obj
	 * @return
	 */
	public byte[] objectToByte(Object obj) {
		byte[] bytes = new byte[1024];
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			bytes = bo.toByteArray();
			bo.close();
			oo.close();
		} catch (Exception e) {
			System.out.println("translation" + e.getMessage());
			e.printStackTrace();
		}
		return (bytes);
	}

	/**
	 * 字节数组转换成对象
	 * 
	 * @param bytes
	 * @return
	 */
	public Object bytesToObject(byte[] bytes) {
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	/**
	 * 加载本地图片
	 * 传值可以为null
	 * 
	 * @return Bitmap 可以为null即此路径的图片不存在或者文件异常
	 */
	public Bitmap getLocalBitmap(String path) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
