package courseproject.huangyuming.wordsdividedreminder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import courseproject.huangyuming.bean.Reminder;

/**
 * Created by ym on 16-10-18.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "reminders.db";
    private static final int VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Reminder.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource, Reminder.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DatabaseHelper instance;
    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context)
    {
        if (instance == null)
        {
            synchronized (DatabaseHelper.class)
            {
                if (instance == null)

                    instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }

    private Dao<Reminder, Integer> homeworkDao;
    /**
     * 获得homeworkDao
     *
     * @return
     * @throws SQLException
     */
    public Dao<Reminder, Integer> getHomeworkDao() throws SQLException
    {
        if (homeworkDao == null)
        {
            homeworkDao = getDao(Reminder.class);
        }
        return homeworkDao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close()
    {
        super.close();
        homeworkDao = null;
    }

}
