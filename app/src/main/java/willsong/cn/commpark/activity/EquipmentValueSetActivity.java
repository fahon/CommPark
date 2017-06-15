package willsong.cn.commpark.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import w.song.orchid.activity.OBaseActivity;
import willsong.cn.commpark.R;

public class EquipmentValueSetActivity extends OBaseActivity {
    private Button _sureButton;
    private EditText _compnayCodeEditText;
    private EditText _devCodeEditText, et_park_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_equipment_value_set);

        _sureButton=(Button)findViewById(R.id.activity_equipment_value_set_Button_sure);
        _compnayCodeEditText=(EditText)findViewById(R.id.activity_equipment_value_set_EditText_compnayCode);
        _devCodeEditText=(EditText)findViewById(R.id.activity_equipment_value_set_EditText_devCode);
        et_park_code=(EditText)findViewById(R.id.et_park_code);

        setTitleText("系统参数设置");
        setRightButtonVisible(false);
        _compnayCodeEditText.setText(mBusinessManager.getCompnayCode());
        _devCodeEditText.setText(mBusinessManager.getDevCode());
        et_park_code.setText(mBusinessManager.getParkCode());

        _sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBusinessManager.saveCompnayCode(""+_compnayCodeEditText.getText());
                mBusinessManager.saveDevCode(""+_devCodeEditText.getText());
                mBusinessManager.saveParkCode(""+et_park_code.getText());
                finish();
            }
        });

    }
}
