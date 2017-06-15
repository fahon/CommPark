package willsong.cn.commpark.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import willsong.cn.commpark.R;

import static com.example.fxpsam.CartTools.getCart;

/**
 * Created by Administrator on 2016/11/25 0025.
 */

public class BusCardBlanceActivity extends OBaseActivity {

    TextView cardNumber,blance,select_Tx;
    LinearLayout blanceInfo;
    Button activity_bus_card_Button_blance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_buscardblance);
        init();

    }

    private void init() {
        setRightButtonVisible(false);
        setTitleText("查询余额");
        cardNumber = (TextView) findViewById(R.id.cardNumber);
        blance = (TextView) findViewById(R.id.blance);
        select_Tx = (TextView) findViewById(R.id.select_Tx);

        blanceInfo = (LinearLayout) findViewById(R.id.blanceInfo);
        activity_bus_card_Button_blance = (Button) findViewById(R.id.activity_bus_card_Button_blance);

        activity_bus_card_Button_blance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> map = getCart();
                if(map.size() < 2){
                    select_Tx.setText("请重新读取");
                }else {
                    select_Tx.setVisibility(View.GONE);
                    blanceInfo.setVisibility(View.VISIBLE);
                    cardNumber.setText(map.get("SurfaceCart"));
                    blance.setText(map.get("cardTradeMoney") + "元");
                }
            }
        });
    }
}
