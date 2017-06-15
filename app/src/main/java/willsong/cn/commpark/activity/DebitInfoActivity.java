package willsong.cn.commpark.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.activity.OrchidActivity;
import willsong.cn.commpark.R;

public class DebitInfoActivity extends OBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_debit_info);
        TextView infoTextView= (TextView) findViewById(R.id.activity_debit_info_TextView_info);
        setRightButtonVisible(false);
        setTitleText("支付成功");
        infoTextView.setText(getIntent().getStringExtra("debit_info"));
    }
}
