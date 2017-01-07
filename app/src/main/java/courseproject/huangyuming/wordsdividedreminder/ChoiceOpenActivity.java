package courseproject.huangyuming.wordsdividedreminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;

/**
 * Created by huangchenling on 2017/1/7.
 */

public class ChoiceOpenActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypedValue outValue = new TypedValue();
        ChoiceOpenActivity.this.getTheme().resolveAttribute(R.attr.alertDialogTheme, outValue, true);

        AlertDialog builder = new AlertDialog.Builder(ChoiceOpenActivity.this).setIcon(R.mipmap.logo)
                .setTitle("复制").setMessage("现在打开备忘鹿吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ChoiceOpenActivity.this, CreateActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("暂时不了", null).show();
    }
}
