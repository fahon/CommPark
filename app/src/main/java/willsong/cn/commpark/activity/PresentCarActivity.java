package willsong.cn.commpark.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.adapter.BillDetailAdapter;
import willsong.cn.commpark.activity.adapter.PresentCarAdapter;
import willsong.cn.commpark.activity.widget.xlistview.XListView;

/**
 * 在场车辆明细
 * Created by guof on 2016/12/14.
 */

public class PresentCarActivity extends OBaseActivity implements XListView.IXListViewListener, View.OnClickListener {
    private XListView present_car_list;
    private PresentCarAdapter adapter;
    private ArrayList<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
    private TextView tv_hint;
    private int page = 1, pageSize = 30;
    private int refresh = 0;//清除list：2
    private int carFlag = 0;//临时车：0 月租车：1
    String currTime = "";
    private TextView tv_temporary_car, tv_rental_car;//临时车，月租车
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_present_car);
        init();
        mBusinessManager.netPresentCar(true, getTodayDate(), page, pageSize, carFlag);
    }

    private void init() {
        setRightButtonVisible(false);
        setTitleText("在场车辆明细");
        present_car_list = (XListView) findViewById(R.id.present_car_list);
        tv_hint = (TextView) findViewById(R.id.tv_hint);
        tv_temporary_car = (TextView) findViewById(R.id.tv_temporary_car);
        tv_temporary_car.setOnClickListener(PresentCarActivity.this);
        tv_rental_car = (TextView) findViewById(R.id.tv_rental_car);
        tv_rental_car.setOnClickListener(PresentCarActivity.this);

        adapter = new PresentCarAdapter(listmap, PresentCarActivity.this);
        present_car_list.setPullLoadEnable(true);
        present_car_list.setPullRefreshEnable(true);
        present_car_list.setAdapter(adapter);
        present_car_list.setXListViewListener(this);
        present_car_list.setRefreshTime("刚刚");

        present_car_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String CarPlate = listmap.get(position-1).get("CarPlate").toString();
                String EnterTime = listmap.get(position-1).get("EnterTime").toString();
                String TimeLong = listmap.get(position-1).get("TimeLong").toString();
                String CarType = carFlag+"";
                Intent intent = new Intent(PresentCarActivity.this,PresentCarDetailActivity.class);
                intent.putExtra("CarPlate",CarPlate);
                intent.putExtra("EnterTime",EnterTime);
                intent.putExtra("TimeLong",TimeLong);
                intent.putExtra("CarType",CarType);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tv_temporary_car:
                carFlag = 0;
                page = 1;
                refresh = 2;
                mBusinessManager.netPresentCar(true, getTodayDate(), page, pageSize, carFlag);
                tv_temporary_car.setTextColor(Color.WHITE);
                tv_temporary_car.setBackgroundResource(R.drawable.left_click);
                tv_rental_car.setTextColor(this.getResources().getColor(R.color.cp_green));
                tv_rental_car.setBackgroundResource(R.drawable.right_default);
                break;
            case R.id.tv_rental_car:
                carFlag = 1;
                page = 1;
                refresh = 2;
                mBusinessManager.netPresentCar(true, getTodayDate(), page, pageSize, carFlag);
                tv_temporary_car.setTextColor(this.getResources().getColor(R.color.cp_green));
                tv_temporary_car.setBackgroundResource(R.drawable.left_default);
                tv_rental_car.setTextColor(Color.WHITE);
                tv_rental_car.setBackgroundResource(R.drawable.right_click);
                break;
        }
    }

    // Refresh下拉刷新
    @Override
    public void onRefresh() {
        present_car_list.setRefreshTime(currTime);
        page = 1;
        refresh = 2;
        mBusinessManager.netPresentCar(false, getTodayDate(), page, pageSize,carFlag);
    }

    // LoadMore 上拉加载更多
    @Override
    public void onLoadMore() {
        page++;
        refresh = 1;
        mBusinessManager.netPresentCar(false, getTodayDate(), page, pageSize,carFlag);
    }
    //stopLoad 停止加载
    private void stopLoad() {
        present_car_list.stopRefresh();
        present_car_list.stopLoadMore();
    }

    @Override
    public void refreshView(String type, Map<String, Object> map) {
        super.refreshView(type, map);
        stopLoad();
        if (type.equals(BusinessManager.NETPRESENTCAR)) {
            tv_hint.setVisibility(View.GONE);
            present_car_list.setVisibility(View.VISIBLE);
//            MyTools.showToastShort(true, map+"", mContext);
            if (map.get("CarThroughList").toString().equals("[]")) {
                if (refresh == 1) {
                    tv_hint.setVisibility(View.GONE);
                    Toast.makeText(this, "暂无更多数据", Toast.LENGTH_SHORT).show();
                } else {
                    present_car_list.setVisibility(View.GONE);
                    listmap.clear();
                    adapter.notifyDataSetChanged();
                    tv_hint.setVisibility(View.VISIBLE);
                }
            } else {
                if (refresh == 2) {
                    listmap.clear();
                }
                currTime = getNowDate();
                listmap.addAll((ArrayList<Map<String, Object>>) (map.get("CarThroughList")));
                adapter.notifyDataSetChanged();
            }

        } else if (type.equals(BusinessManager.GET_FAIL)) {
            if (refresh == 1) {
                tv_hint.setVisibility(View.GONE);
                Toast.makeText(this, "加载数据失败", Toast.LENGTH_SHORT).show();
            } else {
                present_car_list.setVisibility(View.GONE);
                tv_hint.setVisibility(View.VISIBLE);
            }
        }
    }
}
