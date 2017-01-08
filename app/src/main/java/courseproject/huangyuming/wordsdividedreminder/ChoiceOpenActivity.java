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
    public static boolean CLIP_FLAG = false;
    public static long clip_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypedValue outValue = new TypedValue();
        ChoiceOpenActivity.this.getTheme().resolveAttribute(R.attr.alertDialogTheme, outValue, true);

        if(System.currentTimeMillis() - clip_time > 1000) {

            new AlertDialog.Builder(ChoiceOpenActivity.this)
                    .setIcon(R.mipmap.logo)
                    .setCancelable(false)
                    .setTitle("复制").setMessage("现在打开备忘鹿吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clip_time = System.currentTimeMillis();
                    Intent intent = new Intent(ChoiceOpenActivity.this, MainActivity.class);
//                        intent.putExtra(getResources().getString(R.string.clip_flag), true);
                    CLIP_FLAG = true;
                    startActivity(intent);
                    finish();
                }
            }).setNegativeButton("暂时不了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clip_time = System.currentTimeMillis();
                    finish();
                }
            }).show();
        } else {
            finish();
        }
    }
}
