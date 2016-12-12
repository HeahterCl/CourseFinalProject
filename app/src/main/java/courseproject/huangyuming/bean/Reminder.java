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

    @DatabaseField(columnName = "lessonName")
    private String lessonName;
    @DatabaseField(columnName = "ddl")
    private String ddl;
    @DatabaseField(columnName = "content")
    private String content;
    @DatabaseField(columnName = "photoUrl")
    private String photoUrl;

    public Reminder() {

    }
    public Reminder(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getDdl() {
        return ddl;
    }

    public void setDdl(String ddl) {
        this.ddl = ddl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
