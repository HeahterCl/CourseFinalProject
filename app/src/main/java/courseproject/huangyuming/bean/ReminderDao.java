package courseproject.huangyuming.bean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.query.In;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import courseproject.huangyuming.wordsdividedreminder.DatabaseHelper;
import courseproject.huangyuming.wordsdividedreminder.R;

/**
 * Created by win10 on 2016/12/14.
 */

public class ReminderDao {
    private Context context;
    private DatabaseHelper helper;

    public ReminderDao(Context context) {
        this.context = context;
        try {
            helper = DatabaseHelper.getHelper(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Dao<Reminder, Integer> getReminders() throws SQLException {
        return helper.getRemindersDao();
    }

    private String getTableName() {
        List<String> tableNames = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select name from sqlite_master where type='table' order by name", null);
        while(cursor.moveToNext()){
            String name = cursor.getString(0);
            tableNames.add(name);
        }
        return tableNames.get(1);
    }

    public List<Reminder> queryForAll() throws SQLException {
        return getReminders().queryForAll();
    }

    public void insert(Reminder reminder) throws SQLException {
        ContentValues contentValues = new ContentValues();
        contentValues.put("time", reminder.getTime());
        contentValues.put("position", reminder.getPosition());
        contentValues.put("tasks", reminder.getTasks());
        helper.getWritableDatabase().insert(getTableName(), null, contentValues);
        // 不能关闭数据库，否则重启时报错: unable to re open already closed database
        // helper.getWritableDatabase().close();
    }

    public void update(Reminder reminder, String kindname, String kindvalue) throws SQLException {
        ContentValues contentValues = new ContentValues();
        contentValues.put("time", reminder.getTime());
        contentValues.put("position", reminder.getPosition());
        contentValues.put("tasks", reminder.getTasks());
        String whereClause = kindname+"=?";
        String[] whereArgs = {kindvalue};
        helper.getWritableDatabase().update(getTableName(), contentValues, whereClause, whereArgs);
        // helper.getWritableDatabase().close();
    }

    public Cursor query(String kindname, String kindvalue) throws SQLException {
        Cursor cursor = helper.getReadableDatabase().query(getTableName(), new String[]{"*"}, kindname+"=?", new String[]{kindvalue},
                null, null, null, null);
        return cursor;
    }

    public void delete(String kindname, String kindvalue) throws SQLException {
        helper.getWritableDatabase().delete(getTableName(), kindname+"=?", new String[] {kindvalue});
    }
}
