package courseproject.huangyuming.wordsdividedreminder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.widget.Toast;

import com.j256.ormlite.stmt.query.In;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

public class LaunchActivity extends Activity {
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        RxPermissions rxPermissions = new RxPermissions(LaunchActivity.this);
        rxPermissions.request(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            Toast.makeText(LaunchActivity.this, "欢迎♪(^∇^*)", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LaunchActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LaunchActivity.this, "小鹿能力不够呢(；′⌒`)请主人检查权限以后再来好吗", Toast.LENGTH_LONG).show();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 3000);
                        }
                    }
                });
    }
}
