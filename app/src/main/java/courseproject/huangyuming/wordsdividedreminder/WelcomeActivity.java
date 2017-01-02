package courseproject.huangyuming.wordsdividedreminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.j256.ormlite.stmt.query.In;

import java.sql.SQLException;

import courseproject.huangyuming.bean.ReminderDao;

/**
 * Created by huangchenling on 2016/12/22.
 */

public class WelcomeActivity extends Activity {
    Button welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcome = (Button)findViewById(R.id.welcome);

        try {
            if (DatabaseHelper.getHelper(WelcomeActivity.this).getRemindersDao().queryForAll().size() != 0) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        welcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
