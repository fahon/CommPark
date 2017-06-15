package willsong.cn.commpark.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.Print.BluetoothUtil;
import willsong.cn.commpark.activity.adapter.CheckDetailAdapter;
import willsong.cn.commpark.activity.database.DBManager;
import willsong.cn.commpark.activity.database.RecordSteps;

import static com.printer.sdk.PrinterInstance.mPrinter;

/**
 * 车辆盘点明细
 * Created by guof on 2017/3/1
 */

public class CheckDetailActivity extends OBaseActivity{
    private ListView present_car_list;
    private CheckDetailAdapter adapter;
    private ArrayList<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
    private TextView tv_hint;

    private DBManager dbManager;
    private BluetoothUtil bu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_check_detail);
        init();

        dbManager = new DBManager(this);
        ArrayList<RecordSteps> persons = (ArrayList<RecordSteps>) dbManager.query();
        Collections.reverse(persons);//将数据倒序排列
            for (int i=0;i<persons.size();i++) {//读取数据库里的数据
                RecordSteps person2=persons.get(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("_id", person2._id+"");
                map.put("CarPlate", person2.plateCode);
                map.put("TimeLong", person2.timeLonger);
                map.put("EnterTime", person2.enterTime);
                listmap.add(map);
            }
        adapter = new CheckDetailAdapter(listmap, CheckDetailActivity.this);
        present_car_list.setAdapter(adapter);
        bu = new BluetoothUtil(this);
    }

    private void init() {
        setRightButtonVisible(true);
        setTitleText("车辆盘点明细");
        setRightButtonText("打印");
        present_car_list = (ListView) findViewById(R.id.check_detail_list);
        tv_hint = (TextView) findViewById(R.id.tv_hint);
        setTitleRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listmap.size()>0){
                    if (mPrinter == null) {
                        bu.open();
                    } else {
                        if (bu.isConnected == true) {
                            mPrinter.printText("    盘点明细报表" + "\n");
                            for (int i = 0; i < listmap.size(); i++) {
                                mPrinter.printText("车牌号码:" + listmap.get(i).get("CarPlate") + "\n");
                                mPrinter.printText("进场时间:" + listmap.get(i).get("EnterTime") + "\n");
                                mPrinter.printText("已停时长:" + listmap.get(i).get("TimeLong") + "\n");
                                mPrinter.printText("-------------------------" + "\n");
                            }
                            mPrinter.printText("\n\n");
                        } else {
                            MyTools.showToastShort(true, "打印机未连接!", CheckDetailActivity.this);
                        }
                    }
                }else {
                    MyTools.showToastShort(true, "暂无可打印的明细内容", CheckDetailActivity.this);
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.closeDB();// 释放数据库资源
    }
}
