package courseproject.huangyuming.wordsdividedreminder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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
    private ArrayList<Pair<Long, Reminder>> mItemArray;
    private ItemAdapter mListAdapter;
    private ReminderDao mReminderDao;

    private static final int REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivityForResult(intent, REQUEST);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDragListView = (DragListView)findViewById(R.id.drag_list_view);

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

//                    for (int i = 0; i < mItemArray.size(); ++i) {
//                        mItemArray.get(i).second.listIndex = i;
//                    }
                }
            }

            @Override
            public void onItemDragging(int itemPosition, float x, float y) {
                super.onItemDragging(itemPosition, x, y);
            }
        });

        mItemArray = new ArrayList<>();
        mReminderDao = new ReminderDao(MainActivity.this);
        Dao<Reminder, Integer> remindersDao;
        try {
            remindersDao = DatabaseHelper.getHelper(MainActivity.this).getRemindersDao();
            List<Reminder> reminders = remindersDao.queryForAll();
            for (int i = 0; i < reminders.size(); ++i) {
                mItemArray.add(new Pair<>((long) i, reminders.get(i)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        mListAdapter = new ItemAdapter(mItemArray, R.layout.list_item, R.id.root, true);
        mDragListView.setAdapter(mListAdapter, true);
        mDragListView.setCanDragHorizontally(true);
        mDragListView.setCustomDragItem(new MyDragItem(this, R.layout.list_item));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == MainActivity.REQUEST) {
            Reminder h = (Reminder) data.getSerializableExtra("reminder");
            try {
                mReminderDao.insert(h);
                mItemArray.add(new Pair<>((long) mItemArray.size(), h));
                mListAdapter.notifyDataSetChanged();
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
}
