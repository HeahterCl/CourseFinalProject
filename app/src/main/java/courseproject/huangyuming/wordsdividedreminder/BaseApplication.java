package courseproject.huangyuming.wordsdividedreminder;

import android.app.Application;
import android.content.Context;

import courseproject.huangyuming.utility.SpeechRecognitionHelper;

/**
 * Created by huangyuming on 16-12-20.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();

        // 科大讯飞整体初始化
        SpeechRecognitionHelper.getInstance().init(context);

    }
}
