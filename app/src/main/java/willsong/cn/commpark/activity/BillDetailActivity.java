package willsong.cn.commpark.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.adapter.BillDetailAdapter;
import willsong.cn.commpark.activity.widget.xlistview.XListView;

/**
 * 订单明细
 * Created by guof on 2016/12/14.
 */

public class BillDetailActivity extends OBaseActivity implements XListView.IXListViewListener, View.OnClickListener {
    private XListView billlist;
    private BillDetailAdapter adapter;
    private ArrayList<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
    String currTime = "";
    private TextView tv_exitcar_today, tv_exitcar_yesterday;//今日出场，昨日出场
    private TextView tv_totalCar, tv_totalMoney;//总数量，总金额
    private TextView tv_hint;
    private int page = 1, pageSize = 30;
    private int refresh = 0;//清除list：2
    private int dayFlag = 0;//今天：0 昨天：1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_bill_detail);
        init();
        mBusinessManager.netBillDetail(true, getTodayDate(), page, pageSize);
    }

    private void init() {
        setRightButtonVisible(false);
        setTitleText("出场明细");
        billlist = (XListView) findViewById(R.id.billlist);
        tv_exitcar_today = (TextView) findViewById(R.id.tv_exitcar_today);
        tv_exitcar_today.setOnClickListener(BillDetailActivity.this);
        tv_exitcar_yesterday = (TextView) findViewById(R.id.tv_exitcar_yesterday);
        tv_exitcar_yesterday.setOnClickListener(BillDetailActivity.this);
        tv_totalCar = (TextView) findViewById(R.id.tv_totalCar);
        tv_totalMoney = (TextView) findViewById(R.id.tv_totalMoney);
        tv_hint = (TextView) findViewById(R.id.tv_hints);

        adapter = new BillDetailAdapter(listmap, BillDetailActivity.this);
        billlist.setPullLoadEnable(true);
        billlist.setPullRefreshEnable(true);
        billlist.setAdapter(adapter);
        billlist.setXListViewListener(this);
        billlist.setRefreshTime("刚刚");

        billlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String CarPlate = listmap.get(position - 1).get("CarPlate").toString();
                String EnterTime = listmap.get(position - 1).get("EnterTime").toString();
                String OutTime = listmap.get(position - 1).get("OutTime").toString();
                String TimeLong = listmap.get(position - 1).get("TimeLong").toString();
                String PayType = listmap.get(position - 1).get("PayType").toString();
                String PayAmount = listmap.get(position - 1).get("PayAmount") + "";//实收
                String Amount = listmap.get(position - 1).get("Amount") + "";//应收
                String SerialNo = listmap.get(position - 1).get("SerialNo") + "";//流水号
                Intent intent = new Intent(BillDetailActivity.this, OutCarDetailActivity.class);
                intent.putExtra("CarPlate", CarPlate);
                intent.putExtra("EnterTime", EnterTime);
                intent.putExtra("OutTime", OutTime);
                intent.putExtra("TimeLong", TimeLong);
                intent.putExtra("PayType", PayType);
                intent.putExtra("PayAmount", PayAmount);
                intent.putExtra("Amount", Amount);
                intent.putExtra("SerialNo", SerialNo);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tv_exitcar_today:
                dayFlag = 0;
                page = 1;
                refresh = 2;
                mBusinessManager.netBillDetail(true, getTodayDate(), page, pageSize);
                tv_exitcar_today.setTextColor(Color.WHITE);
                tv_exitcar_today.setBackgroundResource(R.drawable.left_click);
                tv_exitcar_yesterday.setTextColor(this.getResources().getColor(R.color.cp_green));
                tv_exitcar_yesterday.setBackgroundResource(R.drawable.right_default);
                break;
            case R.id.tv_exitcar_yesterday:
                dayFlag = 1;
                page = 1;
                refresh = 2;
                mBusinessManager.netBillDetail(true, getYesterdayDate(), page, pageSize);
                tv_exitcar_today.setTextColor(this.getResources().getColor(R.color.cp_green));
                tv_exitcar_today.setBackgroundResource(R.drawable.left_default);
                tv_exitcar_yesterday.setTextColor(Color.WHITE);
                tv_exitcar_yesterday.setBackgroundResource(R.drawable.right_click);
                break;
        }
    }

    // Refresh下拉刷新
    @Override
    public void onRefresh() {
        billlist.setRefreshTime(currTime);
        page = 1;
        refresh = 2;
        if (dayFlag == 1) {
            mBusinessManager.netBillDetail(false, getYesterdayDate(), page, pageSize);
        } else {
            mBusinessManager.netBillDetail(false, getTodayDate(), page, pageSize);
        }
    }

    // LoadMore 上拉加载更多
    @Override
    public void onLoadMore() {
        page++;
        refresh = 1;
        if (dayFlag == 1) {
            mBusinessManager.netBillDetail(false, getYesterdayDate(), page, pageSize);
        } else {
            mBusinessManager.netBillDetail(false, getTodayDate(), page, pageSize);
        }
    }

    //stopLoad 停止加载
    private void stopLoad() {
        billlist.stopRefresh();
        billlist.stopLoadMore();
    }

    @Override
    public void refreshView(String type, Map<String, Object> map) {
        super.refreshView(type, map);
        stopLoad();
        if (type.equals(BusinessManager.NETBILLDETAIL)) {
            tv_hint.setVisibility(View.GONE);
            billlist.setVisibility(View.VISIBLE);
//            MyTools.showToastShort(true, map+"", mContext);
            if (map.get("CarThroughList").toString().equals("[]")) {
                if (refresh == 1) {
                    tv_hint.setVisibility(View.GONE);
                    Toast.makeText(this, "暂无更多数据", Toast.LENGTH_SHORT).show();
                } else {
                    billlist.setVisibility(View.GONE);
                    listmap.clear();
                    adapter.notifyDataSetChanged();
                    tv_totalCar.setText("总数量: 0");
                    tv_totalMoney.setText("总金额: 0.0");
                    tv_hint.setVisibility(View.VISIBLE);
                }
            } else {
                if (refresh == 2) {
                    listmap.clear();
                }
                currTime = getNowDate();
                try {
                    String records = map.get("Records").toString().trim();
                    if (records.contains(".")) {
                        tv_totalCar.setText("总数量:" + records.substring(0, records.indexOf(".")));
                    } else {
                        tv_totalCar.setText("总数量:" + records);
                    }
                    String amount = map.get("Amount").toString().trim();
                    tv_totalMoney.setText("总金额:" + amount);
                } catch (Exception e) {
                }
                listmap.addAll((ArrayList<Map<String, Object>>) (map.get("CarThroughList")));
//                adapter = new BillDetailAdapter(listmap,BillDetailActivity.this);
//                billlist.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

        } else if (type.equals(BusinessManager.GET_FAIL)) {
            if (refresh == 1) {
                tv_hint.setVisibility(View.GONE);
                Toast.makeText(this, "加载数据失败", Toast.LENGTH_SHORT).show();
            } else {
                billlist.setVisibility(View.GONE);
                tv_totalCar.setText("总数量: 0");
                tv_totalMoney.setText("总金额: 0.0");
                tv_hint.setVisibility(View.VISIBLE);
            }
        }
    }
}
