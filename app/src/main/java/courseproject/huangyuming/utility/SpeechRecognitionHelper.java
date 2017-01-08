package courseproject.huangyuming.utility;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Created by Administrator on 2016/11/24.
 */
public class SpeechRecognitionHelper {

    // 静态方法
    public static SpeechRecognitionHelper instance;
    public static SpeechRecognitionHelper getInstance() {
        if (instance == null) {
            instance = new SpeechRecognitionHelper();
        }
        return instance;
    }

    private SpeechRecognitionHelper() {}

    public void init(Context context) {
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=582b3f8f");
    }

    // async
    public void startRecognize(final Context context) {
        RecognizerDialog mDialog = new RecognizerDialog(context, null);
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");

        mDialog.setListener(new RecognizerDialogListener() {

            private String finalResults = "";
            @Override
            public void onResult(RecognizerResult results, boolean isLast) {

                //语音听写的结果 需要拼接
                finalResults += parseResult(results);
                if (isLast) {
                    if (onResultListener != null) {
                        onResultListener.onResult(UUID.randomUUID().toString(), finalResults);
                    }
                    finalResults = "";
                }
            }

            @Override
            public void onError(SpeechError error) {
                onResultListener.onError(error.getErrorDescription());
            }
        });

        mDialog.show();
    }

    //因为listen接收数据异步，所以定义一个监听回调形式接口
    public interface OnResultListener {
        void onResult(String fileId, String result);
        void onError(String errorMsg);
    }

    private OnResultListener onResultListener;

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    //解析结果
    private String parseResult(RecognizerResult results) {
        // 用HashMap存储听写结果
        HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {

            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        return resultBuffer.toString();
    }

}
