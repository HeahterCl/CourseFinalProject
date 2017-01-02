package courseproject.huangyuming.wordsdividedreminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import courseproject.huangyuming.bean.Reminder;

/**
 * Created by huangchenling on 2016/12/24.
 */

public class AlarmActivity extends Activity {
    private PowerManager.WakeLock myWakeLock;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        TypedValue outValue = new TypedValue();
        AlarmActivity.this.getTheme().resolveAttribute(R.attr.alertDialogTheme, outValue, true);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON |
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        startVibrator();
        createDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        acquireWakeLock();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseWakeLock();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        vibrator.cancel();
    }

    private void acquireWakeLock() {
        if (myWakeLock == null) {
            PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
            myWakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, AlarmActivity.this.getClass().getCanonicalName());
            myWakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (myWakeLock != null && myWakeLock.isHeld()) {
            myWakeLock.release();
            myWakeLock = null;
        }
    }

    private void startVibrator() {
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long [] pattern = {100, 10, 100, 100};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, 2);
    }

    private void createDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(AlarmActivity.this);
        View newView = layoutInflater.inflate(R.layout.dialog_alarm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);
        builder.setView(newView);

        Bundle bundle = getIntent().getExtras();
        Reminder reminder = (Reminder) bundle.get("clock");

        TextView alarmTime = (TextView)newView.findViewById(R.id.alarm_time);
        alarmTime.setText(reminder.getTime());
        TextView alarmLocation = (TextView)newView.findViewById(R.id.alarm_location);
        alarmLocation.setText(reminder.getPosition());
        TextView alarmThing = (TextView)newView.findViewById(R.id.alarm_thing);
        alarmThing.setText(reminder.getTasks());

        builder.setIcon(R.mipmap.clock)
                .setTitle("这个事情可不能忘哦").setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vibrator.cancel();
                        finish();
                    }
                }).show();
    }
}
