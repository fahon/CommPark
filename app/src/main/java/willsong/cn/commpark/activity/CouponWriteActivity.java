package willsong.cn.commpark.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.adapter.CouponNameAdapter;
import willsong.cn.commpark.activity.zxing.android.CaptureActivity;

/**
 * 手动输入优惠券码
 * Created by guof on 2017/2/13.
 */

public class CouponWriteActivity extends OBaseActivity{
    private EditText et_couponnum;
    private Button activity_coupon_sure;
    public static String couponNum = "";
    private TextView tv_couponName;//优惠券名称
    private PopupWindow popupWindow;
    private ListView lv_group;
    private View view;
    private List<String> groups;
    private RelativeLayout rl_coupon_select;

    public static int typeFlag = 0;//0：手动输入优惠券码  1：选择优惠券名称

    private TextView tv_txt_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_coupon_write);
        init();
        groups = new ArrayList<String>();
        if(("1").equals(mBusinessManager.getShowCouponType())){
            if(("2").equals(mBusinessManager.getIntentSystemModel())) {//自行车版
                tv_txt_title.setVisibility(View.GONE);
                rl_coupon_select.setVisibility(View.GONE);
            }else{
                tv_txt_title.setVisibility(View.VISIBLE);
                rl_coupon_select.setVisibility(View.VISIBLE);
                mBusinessManager.netCouponList(true, "");
            }
        }else{
            tv_txt_title.setVisibility(View.GONE);
            rl_coupon_select.setVisibility(View.GONE);
        }
    }
    private void init(){
        setRightButtonVisible(false);
        setTitleText("手动输入优惠券码");

        et_couponnum = (EditText) findViewById(R.id.et_couponnum);
        tv_txt_title = (TextView) findViewById(R.id.tv_txt_title);
        activity_coupon_sure = (Button) findViewById(R.id.activity_coupon_sure);
        activity_coupon_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_couponnum.getText().toString().trim().equals("")&&tv_couponName.getText().toString().trim().equals("")){
                    MyTools.showToastShort(true,"请输入优惠券码或选择优惠券种类",CouponWriteActivity.this);
                    return;
                }
                if(typeFlag==1){
                    couponNum = tv_couponName.getText().toString().trim();
                }else{
                    couponNum = et_couponnum.getText().toString().trim();
                }
                CaptureActivity.instance.finish();
                try{
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }catch (Exception e){
                }
                CouponWriteActivity.this.finish();
            }
        });

        tv_couponName = (TextView) findViewById(R.id.tv_couponName);
        rl_coupon_select = (RelativeLayout) findViewById(R.id.rl_coupon_select);
        rl_coupon_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et_couponnum.getText().toString().trim().equals("")){
                    MyTools.showToastShort(true,"您已经选择了手输优惠券码",CouponWriteActivity.this);
                    return;
                }
                if(groups.size()==0){
                    MyTools.showToastShort(true,"暂无可选择的优惠券名称",CouponWriteActivity.this);
                    return;
                }
                showWindow(view);
            }
        });
    }

    private void showWindow(View parent) {

        if (popupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = layoutInflater.inflate(R.layout.coupon_name_layout, null);
            lv_group = (ListView) view.findViewById(R.id.lvGroup);
            CouponNameAdapter groupAdapter = new CouponNameAdapter(this, groups);
            lv_group.setAdapter(groupAdapter);
            // 创建一个PopuWidow对象
            popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);

        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        popupWindow.showAsDropDown(parent);// 显示在控件的下方

        lv_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                tv_couponName.setTextColor(Color.BLACK);
                tv_couponName.setText(groups.get(position));
                et_couponnum.setFocusable(false);
                typeFlag = 1;
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
    }
    @Override
    public void refreshView(String type, final Map<String, Object> map) {
        super.refreshView(type, map);
        if (BusinessManager.NETCOUPONLIST.equals(type)) {
//           MyTools.showToastShort(true,map+"",mContext);
            groups = (List<String>) map.get("CouponNameList");
            for(int i=0;i<groups.size();i++){
              String couponNames = groups.get(i);
                if(couponNames.contains("晨鸟")){
                  if(!ExitParkActivity.isShowChenNiao){
                      groups.remove(i);
                  }
                }
            }
        }else{
            MyTools.showToastShort(true,"获取优惠券名称列表失败",CouponWriteActivity.this);
        }
    }
}
