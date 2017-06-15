package willsong.cn.commpark.activity.database;

import android.app.Activity;
//巡场车辆  最近记录的列表实体类
public class RecordSteps extends Activity {
	public int _id;
	public String plateCode;
	public String timeLonger;
	public String enterTime;

public RecordSteps(String plateCode, String timeLonger, String enterTime) {
		super();
		this.plateCode = plateCode;
		this.timeLonger = timeLonger;
		this.enterTime = enterTime;
	}
public RecordSteps(int _id, String plateCode, String timeLonger, String enterTime) {
		super();
		this._id = _id;
		this.plateCode = plateCode;
		this.timeLonger = timeLonger;
		this.enterTime = enterTime;
	}
public RecordSteps() {
	super();
}

	public String getPlateCode() {
		return plateCode;
	}

	public void setPlateCode(String plateCode) {
		this.plateCode = plateCode;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getTimeLonger() {
		return timeLonger;
	}

	public void setTimeLonger(String timeLonger) {
		this.timeLonger = timeLonger;
	}

	public String getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(String enterTime) {
		this.enterTime = enterTime;
	}
}
