package courseproject.huangyuming.bean;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by ym on 16-10-18.
 */

@DatabaseTable(tableName = "reminder")
public class Reminder implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "time")
    public String time;
    @DatabaseField(columnName = "position")
    public String position;
    @DatabaseField(columnName = "contents")
    public String contents;

    public Reminder() {}

    public Reminder(String time, String position) {
        this.time = time;
        this.position = position;
    }

}
