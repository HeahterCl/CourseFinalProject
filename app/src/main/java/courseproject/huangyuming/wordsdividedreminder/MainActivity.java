package courseproject.huangyuming.wordsdividedreminder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.query.In;
import com.j256.ormlite.table.DatabaseTable;
import com.sun.jna.IntegerType;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import courseproject.huangyuming.adapter.GroupListAdapter;
import courseproject.huangyuming.bean.Reminder;
import courseproject.huangyuming.bean.ReminderDao;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Filter;

public class MainActivity extends AppCompatActivity {

//    private DragListView mDragListView;
    private RecyclerView recyclerView;
    private FloatingActionButton mFab;

    private ArrayList<Pair<Integer, Object>> mGroupedData = new ArrayList<>();
    private GroupListAdapter mGroupListAdapter;
//    private ItemAdapter mListAdapter;

    private static final int REQUEST = 1;

    // sensor
    private SensorManager mSensorManager;
    private Sensor mMagneticSensor;
    private Sensor mAccelerometerSensor;

    //闹钟
    private AlarmReceiver alarmReceiver = new AlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivityForResult(intent, REQUEST);
            }
        });

        //注册闹钟广播接受器
        IntentFilter intentfileter = new IntentFilter();
        intentfileter.addAction("CLOCK");
        registerReceiver(alarmReceiver, intentfileter);

        setupListRecyclerView();
    }

    // 将备忘事项按时间分组
    private ArrayList<Pair<Integer, Object>> groupRemindersByDate(List<Reminder> reminders) {
        // TreeMap 自带默认排序
        Map<Date, List<Reminder>> map = new TreeMap<>();
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Reminder reminder : reminders) {
            Date d;
            try {
                d = fmt.parse(reminder.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                d = new Date(0);
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            if (!map.containsKey(cal.getTime())) {
                map.put(cal.getTime(), new ArrayList<Reminder>());
            }
            map.get(cal.getTime()).add(reminder);
        }
        // may need to sort

        ArrayList<Pair<Integer, Object>> result = new ArrayList<>();
        for (Date key : map.keySet()) {
            result.add(new Pair<Integer, Object>(GroupListAdapter.VIEW_TYPE_DIVIDER, key));
            for (Reminder r : map.get(key)) {
                result.add(new Pair<Integer, Object>(GroupListAdapter.VIEW_TYPE_NORMAL, r));
            }
        }

        return result;
    }

    private void setupListRecyclerView() {
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 手写数据库操作或使用框架其中选择一种
        try {
            List<Reminder> reminders = DatabaseHelper.getHelper(MainActivity.this).getRemindersDao().queryForAll();
            mGroupedData.addAll(groupRemindersByDate(reminders));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mGroupListAdapter = new GroupListAdapter(MainActivity.this, mGroupedData);
        recyclerView.setAdapter(mGroupListAdapter);

//        mReminderDao = new ReminderDao(MainActivity.this);
//        List<Reminder> reminders = new ArrayList<>();
//        try {
//            reminders.addAll(mReminderDao.queryForAll());
//            Log.i("Size", reminders.size()+"");
//            for (int i = 0; i < reminders.size(); ++i) {
//                mItemArray.add(new Pair<>((long) i, reminders.get(i)));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alarmReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorEventListener, mMagneticSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(mSensorEventListener, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == MainActivity.REQUEST) {
            Reminder h = (Reminder) data.getSerializableExtra("reminder");
            try {
                //数据库操作
                DatabaseHelper.getHelper(MainActivity.this).getRemindersDao().create(h);

                // 获取ID
                int id = DatabaseHelper.getHelper(MainActivity.this).getRemindersDao().extractId(h);

                List<Reminder> reminders = DatabaseHelper.getHelper(MainActivity.this).getRemindersDao().queryForAll();
                mGroupedData.clear();
                mGroupedData.addAll(groupRemindersByDate(reminders));
                mGroupListAdapter.notifyDataSetChanged();

                if (data.getExtras().getBoolean("clockEnable")) {
                    //添加闹钟
                    String[] time = h.getTime().split("-| |:");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, Integer.valueOf(time[0]));
                    calendar.set(Calendar.MONTH, Integer.valueOf(time[1])-1);
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(time[2])-1);
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[3]));
                    calendar.set(Calendar.MINUTE, Integer.valueOf(time[4]));
                    calendar.set(Calendar.SECOND, 0);

                    int Code = 0;//闹钟的唯一标示
                    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                    intent.setAction("CLOCK");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("clock", h);
                    intent.putExtras(bundle);
                    PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, Code, intent, 0);
                    //得到AlarmManager实例
                    AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                    //根据当前时间预设一个警报
                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                    Log.v("id", Integer.toString(h.getId()));
                }

                Snackbar.make(mFab, "创建成功", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeFinishedReminders() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this)
                .setTitle("(⊙ˍ⊙)")
                .setMessage("确定将所有已完成备忘删除？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<Pair<Integer, Object>> toRemove = new ArrayList<>();
                        for (Pair<Integer, Object> item : mGroupedData) {
                            if (item.first == GroupListAdapter.VIEW_TYPE_NORMAL) {
                                if (((Reminder) item.second).getFinished()) {
                                    toRemove.add(item);
                                }
                            }
                        }

                        try {
                            if (toRemove.size() == 0) {
                                Snackbar.make(mFab, "没有已完成的备忘", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                return;
                            }

                            for (Pair<Integer, Object> item : toRemove) {
                                DatabaseHelper.getHelper(MainActivity.this).getRemindersDao().delete((Reminder) item.second);
                            }
                            mGroupedData.removeAll(toRemove);
                            mGroupListAdapter.notifyDataSetChanged();

                            Snackbar.make(mFab, "删除成功", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("取消", null);

        builder.create().show();
    }

    private static class MyDragItem extends DragItem {

        private Context context;

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
            this.context = context;
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text;

//            text = ((TextView) clickedView.findViewById(R.id.time)).getText();
//            ((TextView) dragView.findViewById(R.id.time)).setText(text);

            text = ((TextView) clickedView.findViewById(R.id.position)).getText();
            ((TextView) dragView.findViewById(R.id.position)).setText(text);

            text = ((TextView) clickedView.findViewById(R.id.contents)).getText();
            ((TextView) dragView.findViewById(R.id.contents)).setText(text);

            dragView.setBackgroundColor(context.getResources().getColor(R.color.draggingBackground));
        }
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        float [] accValues;
        float [] magValues;
        long lastShakeTime = 0;

        float newRotationDegree = 0;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accValues = sensorEvent.values;

                    if (accValues[0] > 15) {
                        if (System.currentTimeMillis()-lastShakeTime >= 2000) {
                            lastShakeTime = System.currentTimeMillis();
//                            Toast.makeText(MainActivity.this, "shaken!", Toast.LENGTH_SHORT).show();

                            removeFinishedReminders();
                        }
                    }

                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magValues = sensorEvent.values;
                    break;
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER || sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

                newRotationDegree = 0;
                if (accValues != null && magValues != null) {
                    float[] R = new float[9];
                    float[] values = new float[3];

                    SensorManager.getRotationMatrix(R, null, accValues, magValues);
                    SensorManager.getOrientation(R, values);

                    newRotationDegree = (float) Math.toDegrees(values[0]);
                }

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

}
