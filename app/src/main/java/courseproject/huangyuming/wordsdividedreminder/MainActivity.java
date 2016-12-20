package courseproject.huangyuming.wordsdividedreminder;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.DatabaseTable;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;
import courseproject.huangyuming.bean.Reminder;
import courseproject.huangyuming.bean.ReminderDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DragListView mDragListView;
    private FloatingActionButton mFab;

    private ArrayList<Pair<Long, Reminder>> mItemArray;
    private ItemAdapter mListAdapter;

    private static final int REQUEST = 1;

    // sensor
    private SensorManager mSensorManager;
    private Sensor mMagneticSensor;
    private Sensor mAccelerometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mDragListView = (DragListView)findViewById(R.id.drag_list_view);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivityForResult(intent, REQUEST);
            }
        });

        setupListRecyclerView();
    }

    private void setupListRecyclerView() {
        mDragListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mDragListView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragStarted(int position) {
                Toast.makeText(mDragListView.getContext(), "Start - position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {
                    Toast.makeText(mDragListView.getContext(), "End - position: " + toPosition, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemDragging(int itemPosition, float x, float y) {
                super.onItemDragging(itemPosition, x, y);
            }
        });

        mItemArray = new ArrayList<>();

        // 手写数据库操作或使用框架其中选择一种
        try {
            List<Reminder> reminders = DatabaseHelper.getHelper(MainActivity.this).getRemindersDao().queryForAll();
            for (int i = 0; i < reminders.size(); ++i) {
                mItemArray.add(new Pair<>((long) i, reminders.get(i)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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


        mDragListView.setLayoutManager(new LinearLayoutManager(this));
        mListAdapter = new ItemAdapter(MainActivity.this, mItemArray, R.layout.list_item, R.id.root, true);
        mDragListView.setAdapter(mListAdapter, true);
        mDragListView.setCanDragHorizontally(true);
        mDragListView.setCustomDragItem(new MyDragItem(this, R.layout.list_item));


    }

    @Override
    protected void onDestroy() {
//        DatabaseHelper.getHelper(MainActivity.this).close();
        super.onDestroy();
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
                DatabaseHelper.getHelper(MainActivity.this).getRemindersDao().create(h);
                mItemArray.add(new Pair<>((long) mItemArray.size(), h));
                mListAdapter.notifyDataSetChanged();

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

    private static class MyDragItem extends DragItem {

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.time)).getText();
            ((TextView) dragView.findViewById(R.id.time)).setText(text);

            text = ((TextView) clickedView.findViewById(R.id.position)).getText();
            ((TextView) dragView.findViewById(R.id.position)).setText(text);

            text = ((TextView) clickedView.findViewById(R.id.contents)).getText();
            ((TextView) dragView.findViewById(R.id.contents)).setText(text);

            dragView.setBackgroundColor(0xFF4876FF);
        }
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        float [] accValues;
        float [] magValues;
        long lastShakeTime = 0;

        float newRotationDegree = 0;

        private int shakeLock = 0;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accValues = sensorEvent.values;

                    if (accValues[0] > 15) {
                        if (shakeLock == 0) {
                            shakeLock = 100;
                            Toast.makeText(MainActivity.this, "shaken!", Toast.LENGTH_SHORT).show();

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this)
                                    .setTitle("(⊙ˍ⊙)")
                                    .setMessage("确定将所有已完成备忘删除？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            List<Pair<Long, Reminder>> toRemove = new ArrayList<>();
                                            for (Pair<Long, Reminder> item : mItemArray) {
                                                if (item.second.getFinished()) {
                                                    toRemove.add(item);
                                                }
                                            }

                                            try {
                                                for (Pair<Long, Reminder> item : toRemove) {
                                                    DatabaseHelper.getHelper(MainActivity.this).getRemindersDao().delete(item.second);
                                                }
                                                mItemArray.removeAll(toRemove);
                                                mListAdapter.notifyDataSetChanged();
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .setNegativeButton("取消", null);

                            builder.create().show();
                        }
                    }

                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magValues = sensorEvent.values;
                    break;
            }

            if (shakeLock > 0) {
                shakeLock--;
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
