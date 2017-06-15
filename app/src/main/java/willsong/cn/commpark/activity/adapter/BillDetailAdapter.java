package willsong.cn.commpark.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.BillDetailActivity;

import java.util.ArrayList;
import java.util.Map;

@SuppressLint("SimpleDateFormat")
public class BillDetailAdapter extends BaseAdapter {
	private ArrayList<Map<String, Object>> map;
	private Context mcontext;

	public BillDetailAdapter(ArrayList<Map<String, Object>> map, BillDetailActivity mcontext) {
		this.map = map;
		this.mcontext = mcontext;
	}

	@Override
	public int getCount() {
		return map == null ? 0 : map.size();
	}

	@Override
	public Object getItem(int position) {
		return map == null ? 0 : map.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder") @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mcontext).inflate(
					R.layout.bill_detail_item, null);
			holder.tv_carPlate = (TextView) convertView.findViewById(R.id.tv_carPlate);
			holder.tv_enterTime = (TextView) convertView.findViewById(R.id.tv_enterTime);
			holder.tv_outTime = (TextView) convertView.findViewById(R.id.tv_outTime);
			holder.tv_payAmount = (TextView) convertView.findViewById(R.id.tv_payAmount);
			holder.tv_timeLong = (TextView) convertView.findViewById(R.id.tv_timeLong);
			holder.tv_payType = (TextView) convertView.findViewById(R.id.tv_payType);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try{
			holder.tv_carPlate.setText(map.get(position).get("CarPlate").toString().trim());
			String enterTime = map.get(position).get("EnterTime").toString().trim();
			holder.tv_enterTime.setText("进场时间:"+enterTime.substring(5));
			String outTime = map.get(position).get("OutTime").toString().trim();
			holder.tv_outTime.setText("出场时间:"+outTime.substring(5));
			holder.tv_payAmount.setText("+"+map.get(position).get("PayAmount").toString().trim());
			String timeLong = map.get(position).get("TimeLong").toString().trim();
			if(timeLong.contains("小时")){
				timeLong = timeLong.replace("小时","时");
			}
			if(timeLong.contains("分钟")){
				timeLong = timeLong.replace("分钟","分");
			}
			holder.tv_timeLong.setText("停车"+timeLong);
			String payType = map.get(position).get("PayType").toString().trim();
			if(payType.equals("免费出场")){
				holder.tv_payType.setText(payType);
			}else{
				holder.tv_payType.setText(payType+"");
			}
		}catch (Exception e){
		}

		return convertView;
	}

	public final class ViewHolder {
    TextView tv_carPlate;//车牌
	TextView tv_enterTime;//进场时间
	TextView tv_outTime;//出场时间
	TextView tv_payAmount;//金额
	TextView tv_timeLong;//时长
	TextView tv_payType;//支付方式
	}
}
