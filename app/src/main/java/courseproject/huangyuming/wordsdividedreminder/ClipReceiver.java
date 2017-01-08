package courseproject.huangyuming.wordsdividedreminder;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

/**
 * Created by huangchenling on 2017/1/7.
 */

public class ClipReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("clip", intent.getStringExtra(context.getResources().getString(R.string.clipboard_value)));

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> cn = am.getRunningTasks(1);
        ActivityManager.RunningTaskInfo taskInfo=cn.get(0);
        ComponentName name=taskInfo.topActivity;
//        System.out.println(name.getClassName());

        // 在CreateActivity在前台的时候不弹出对话框
        if(!name.getClassName().equals("courseproject.huangyuming.wordsdividedreminder.CreateActivity")) {
            Intent clipIntent = new Intent(context, ChoiceOpenActivity.class);
            clipIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(clipIntent);
        }
    }
}
