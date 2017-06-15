package willsong.cn.commpark.activity.database;

import android.app.Activity;

//计步器  最近记录的列表实体类
public class ExitCarEntity extends Activity {
	public int _id;
	public String plateCode;
	public String enterTime;
	public String outTime;
	public String shouldPay;
	public String realPay;
	public String payType;

public ExitCarEntity(String plateCode, String enterTime,String outTime, String shouldPay, String realPay, String payType ) {
		super();
		this.plateCode = plateCode;
		this.enterTime = enterTime;
	    this.outTime = outTime;
	    this.shouldPay = shouldPay;
	    this.realPay = realPay;
	    this.payType = payType;
	}
public ExitCarEntity(int _id, String plateCode, String enterTime,String outTime, String shouldPay, String realPay, String payType ) {
		super();
		this._id = _id;
	    this.plateCode = plateCode;
	    this.enterTime = enterTime;
	    this.outTime = outTime;
	    this.shouldPay = shouldPay;
	    this.realPay = realPay;
	    this.payType = payType;
	}
public ExitCarEntity() {
	super();
}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getPlateCode() {
		return plateCode;
	}

	public void setPlateCode(String plateCode) {
		this.plateCode = plateCode;
	}

	public String getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(String enterTime) {
		this.enterTime = enterTime;
	}

	public String getOutTime() {
		return outTime;
	}

	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}

	public String getShoulePay() {
		return shouldPay;
	}

	public void setShoulePay(String shouldPay) {
		this.shouldPay = shouldPay;
	}

	public String getRealPay() {
		return realPay;
	}

	public void setRealPay(String realPay) {
		realPay = realPay;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}
}
