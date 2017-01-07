package courseproject.huangyuming.wordsdividedreminder;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by huangchenling on 2017/1/7.
 */

public class ClipBoardService extends Service {
    private final IBinder binder = new ClipBoardBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ClipBoardBinder extends Binder {
        ClipBoardService getService() {
            return ClipBoardService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                Intent intent = new Intent();
                intent.setAction(getResources().getString(R.string.clipboardservice_action));
                intent.putExtra(getResources().getString(R.string.clipboard_value), clipboardManager.getText().toString());
                sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
