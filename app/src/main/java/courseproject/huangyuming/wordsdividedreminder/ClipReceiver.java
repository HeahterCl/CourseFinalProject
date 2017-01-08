package courseproject.huangyuming.wordsdividedreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by huangchenling on 2017/1/7.
 */

public class ClipReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("clip", intent.getStringExtra(context.getResources().getString(R.string.clipboard_value)));
        Intent clipIntent = new Intent(context, ChoiceOpenActivity.class);
        clipIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(clipIntent);
    }
}
