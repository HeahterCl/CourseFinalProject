package courseproject.huangyuming.wordsdividedreminder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;

import java.util.Calendar;
import java.util.List;

import courseproject.huangyuming.bean.Reminder;

import static courseproject.huangyuming.wordsdividedreminder.MainActivity.REQUEST;

/**
 * Created by huangchenling on 2017/1/7.
 */

public class ChoiceOpenActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypedValue outValue = new TypedValue();
        ChoiceOpenActivity.this.getTheme().resolveAttribute(R.attr.alertDialogTheme, outValue, true);

        new AlertDialog.Builder(ChoiceOpenActivity.this)
                .setIcon(R.mipmap.logo)
                .setCancelable(false)
                .setTitle("复制").setMessage("现在打开备忘鹿吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ChoiceOpenActivity.this, CreateActivity.class);
                        startActivityForResult(intent, REQUEST);
                    }
                }).setNegativeButton("暂时不了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Reminder h = (Reminder) data.getSerializableExtra(getResources().getString(R.string.reminder));
        try {
            //数据库操作
            DatabaseHelper.getHelper(ChoiceOpenActivity.this).getRemindersDao().create(h);

            if (data.getExtras().getBoolean(getResources().getString(R.string.clock_enable))) {
                //添加闹钟
                String[] time = h.getTime().split("-| |:");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.valueOf(time[0]));
                calendar.set(Calendar.MONTH, Integer.valueOf(time[1])-1);
                calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(time[2]));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[3]));
                calendar.set(Calendar.MINUTE, Integer.valueOf(time[4]));
                calendar.set(Calendar.SECOND, 0);

                // 获取ID
                int id = DatabaseHelper.getHelper(this).getRemindersDao().extractId(h);
                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.setAction(getResources().getString(R.string.clock_action));
                Bundle bundle = new Bundle();
                bundle.putSerializable(getResources().getString(R.string.set_clock), h);
                intent.putExtras(bundle);
                PendingIntent pi = PendingIntent.getBroadcast(this, id, intent, 0);
                //得到AlarmManager实例
                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                //根据当前时间预设一个警报
                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
            }

            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
