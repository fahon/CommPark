package willsong.cn.commpark.activity.Bike;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.BillDetailActivity;
import willsong.cn.commpark.activity.PresentCarActivity;

/**
 * 数据统计(自行车)
 * Created by guof on 2016/12/14.
 */

public class BikeDataStatisticActivity extends OBaseActivity implements View.OnClickListener{
    private FrameLayout fl_leaveNum,fl_totalMoney;
    private TextView tv_remainingnum,tv_exitcar_num,tv_exitcar_money;//在场车辆，已离场，已收费
    private RelativeLayout rl_presnetcar;//在场车辆
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_bike_data_statistic);
        init();
        mBusinessManager.netDataStatistic(true,getTodayDate());
    }
    private void init(){
        setRightButtonVisible(true);
        setTitleText("数据统计");
        setRightButtonText("打印");
        fl_leaveNum = (FrameLayout) findViewById(R.id.fl_leaveNum);
        fl_totalMoney = (FrameLayout) findViewById(R.id.fl_totalMoney);
        fl_leaveNum.setOnClickListener(this);
        fl_totalMoney.setOnClickListener(this);
        tv_remainingnum = (TextView) findViewById(R.id.tv_remainingnum);
        tv_exitcar_num = (TextView) findViewById(R.id.tv_exitcar_num);
        tv_exitcar_money = (TextView) findViewById(R.id.tv_exitcar_money);
        rl_presnetcar = (RelativeLayout) findViewById(R.id.rl_presnetcar);
        rl_presnetcar.setOnClickListener(this);
        setTitleRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTools.showToastShort(true,"您点击了打印按钮",BikeDataStatisticActivity.this);
            }
        });
    }
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.fl_leaveNum:
                final Intent intent = new Intent(getApplicationContext(), BillDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.fl_totalMoney:
                final Intent intent2 = new Intent(getApplicationContext(), BillDetailActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_presnetcar:
                final Intent intent3 = new Intent(getApplicationContext(), PresentBikeActivity.class);
                startActivity(intent3);
                break;
        }
    }

    @Override
    public void refreshView(String type, Map<String, Object> map) {
        super.refreshView(type, map);
        if (type.equals(BusinessManager.NETDATASTATISTIC)) {
//            MyTools.showToastShort(true, map+"", mContext);
            String remainingCar = map.get("Remaining").toString().trim();
            if(remainingCar.contains(".")){
                String totalNumber = remainingCar.substring(0,remainingCar.indexOf("."));
                tv_remainingnum.setText(totalNumber);
            }else{
                tv_remainingnum.setText(remainingCar);
            }
            String totalOut = map.get("TotalOut").toString().trim();
            if(totalOut.contains(".")){
                String carNumber = totalOut.substring(0,totalOut.indexOf("."));
                tv_exitcar_num.setText(carNumber);
            }else{
                tv_exitcar_num.setText(totalOut);
            }
            String totalAmount = map.get("TotalAmount").toString().trim();
            tv_exitcar_money.setText(totalAmount);
        }else if(type.equals(BusinessManager.GET_FAIL)){
            MyTools.showToastShort(true, "获取数据失败", mContext);
        }
    }
}
