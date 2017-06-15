package willsong.cn.commpark.activity.widget;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by zhenqiang on 2016/12/9.
 */

public class SpeechUtils {
    //-------------------
    private static final String UNIT[] = { "万", "千", "佰", "拾", "亿", "千", "佰",
            "拾", "万", "千", "佰", "拾", "元", "角", "分" };

    private static final String NUM[] = { "零", "一", "二", "三", "四", "五", "六",
            "七", "八", "九" };

    private static final double MAX_VALUE = 9999999999999.99D;


    private Context context;


    private static final String TAG = "SpeechUtils";
    private static SpeechUtils singleton;

    private TextToSpeech textToSpeech; // TTS对象

    public static SpeechUtils getInstance(Context context) {
        if (singleton == null) {
            synchronized (SpeechUtils.class) {
                if (singleton == null) {
                    singleton = new SpeechUtils(context);
                }
            }
        }
        return singleton;
    }

    public SpeechUtils(Context context) {
        this.context = context;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setPitch(1.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                    textToSpeech.setSpeechRate(1.0f);
                }
            }
        });
    }

    public void speakText(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text,
                    TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    /**
     * 将金额小数转换成中文大写金额
     * @param money
     * @return result
     */
    public static String convertMoney(double money) {
        if (money < 0 || money > MAX_VALUE)
//            return "参数非法!";
            return "零元";
        long money1 = Math.round(money * 100); // 四舍五入到分
        if (money1 == 0)
            return "零元";
        String strMoney = String.valueOf(money1);
        int numIndex = 0; // numIndex用于选择金额数值
        int unitIndex = UNIT.length - strMoney.length(); // unitIndex用于选择金额单位
        boolean isZero = false; // 用于判断当前为是否为零
        String result = "";
        for (; numIndex < strMoney.length(); numIndex++, unitIndex++) {
            char num = strMoney.charAt(numIndex);
            if (num == '0') {
                isZero = true;
                if (UNIT[unitIndex] == "亿" || UNIT[unitIndex] == "万"
                        || UNIT[unitIndex] == "元") { // 如果当前位是亿、万、元，且数值为零
                    result = result + UNIT[unitIndex]; //补单位亿、万、元
                    isZero = false;
                }
            }else {
                if (isZero) {
                    result = result + "零";
                    isZero = false;
                }
                result = result + NUM[Integer.parseInt(String.valueOf(num))] + UNIT[unitIndex];
            }
        }
        //不是角分结尾就加"整"字
        if (!result.endsWith("角")&&!result.endsWith("分")) {
            result = result;
//            result = result + "整";
        }
        //例如没有这行代码，数值"400000001101.2"，输出就是"肆千亿万壹千壹佰零壹元贰角"
        result = result.replaceAll("亿万", "亿");
        return result;
    }

}
