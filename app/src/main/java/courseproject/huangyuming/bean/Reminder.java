package courseproject.huangyuming.bean;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.query.In;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by ym on 16-10-18.
 */

@DatabaseTable(tableName = "reminder")
public class Reminder implements Serializable {
    public static final String UPDATE_TIME = "time";
    public static final String UPDATE_POSITION = "position";
    public static final String UPDATE_TASKS = "tasks";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "time")
    private String time;
    @DatabaseField(columnName = "position")
    private String position;
    @DatabaseField(columnName = "tasks")
    private String tasks;
    @DatabaseField(columnName = "finished")
    private boolean finished;

    public Reminder() {
        this.time = Integer.toString(Calendar.getInstance().get(Calendar.YEAR))+"-"
                +Integer.toString(Calendar.getInstance().get(Calendar.MONTH))+"-"
                +Integer.toString(Calendar.getInstance().get(Calendar.DATE))+"-"
                +Integer.toString(Calendar.getInstance().get(Calendar.HOUR))+"-"
                +Integer.toString(Calendar.getInstance().get(Calendar.MINUTE))+"-"
                +Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
        this.position = "";
        this.tasks = "";
        this.finished = false;
    }

    public Reminder(String time, String position, String task) {
        this.time = time;
        this.position = position;
        this.tasks = task;
        this.finished = false;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTasks() {
        return tasks;
    }

    public void setTasks(String tasks) {
        this.tasks = tasks;
    }

    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getId() {
        return this.id;
    }
}
