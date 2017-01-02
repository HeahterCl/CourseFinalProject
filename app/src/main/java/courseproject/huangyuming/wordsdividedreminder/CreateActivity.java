package courseproject.huangyuming.wordsdividedreminder;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import courseproject.huangyuming.bean.Reminder;
import courseproject.huangyuming.utility.SpeechRecognitionHelper;

public class CreateActivity extends Activity {
    private static final String dividerurl = "http://api.ltp-cloud.com/analysis/";
    private static final String timeurl = "http://osp.voicecloud.cn/index.php/ajax/generaldic/getresult";
    private static final String bosonTimeUrl = "http://api.bosonnlp.com/time/analysis?pattern=";
    private static final String appid = "5847e061";
    private static final String token = "hMmHWWid.11283._tGryZoQ5HpS";
    private static final int UPDATE_CONTENT = 0;
    private EditText before;
    private Button startSRBtn;
    private Button divider;
    private LinearLayout mainlayout;
    private LinearLayout after;
    private EditText details;
    private ImageView logo;
    private TextView type;
    private Button complete;
    private Dialog wait;
    private String time;
    private String date;
    private Reminder reminder;
    private boolean clockEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        before = (EditText)findViewById(R.id.before);
        startSRBtn = (Button)findViewById(R.id.startSRBtn);
        divider = (Button)findViewById(R.id.divider);
        mainlayout = (LinearLayout)findViewById(R.id.activity_main);
        after = (LinearLayout)findViewById(R.id.after);
        details = (EditText) findViewById(R.id.details);
        logo = (ImageView)findViewById(R.id.logo);
        type = (TextView)findViewById(R.id.type);
        complete = (Button)findViewById(R.id.complete);

        reminder = new Reminder();


        startSRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpeechRecognitionHelper.getInstance().setOnResultListener(new SpeechRecognitionHelper.OnResultListener() {
                    @Override
                    public void onResult(String fileId, String result) {
                        before.setText(result);
                    }

                    @Override
                    public void onError(String errorMsg) {
                        Toast.makeText(CreateActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
                SpeechRecognitionHelper.getInstance().startRecognize(CreateActivity.this);
            }
        });

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (before.getText().toString().equals("")) {
                    Dialog dialog = new AlertDialog.Builder(CreateActivity.this).setTitle("注意")
                            .setPositiveButton("知道了", null).setMessage("时间、地点、事件不能为空哦o(*￣▽￣*)ブ").create();
                    dialog.show();
                } else {
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new Thread(networkTask).start();
                        wait = ProgressDialog.show(CreateActivity.this, "", "正在加载中，请耐心等待......");
                        wait.show();
                    } else {
                        Toast.makeText(CreateActivity.this, "当前没有可用网络哦_(:з)∠)_请检查你的网络连接", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.getText().toString().equals("时间")) {
                    new AlertDialog.Builder(CreateActivity.this).setIcon(R.mipmap.clock).setTitle("闹钟")
                            .setMessage("是否添加闹钟提醒？").setNegativeButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clockEnable = true;
                        }
                    }).setPositiveButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clockEnable = false;
                        }
                    }).setCancelable(false).show();
                    logo.setImageResource(R.mipmap.location);
                    type.setText("地点");
                    reminder.setTime(details.getText().toString());
                    details.setText("");
                } else if (type.getText().toString().equals("地点")) {
                    logo.setImageResource(R.mipmap.thing);
                    type.setText("任务");
                    reminder.setPosition(details.getText().toString());
                    details.setText("");
                    complete.setText("完成");
                } else {
                    reminder.setTasks(details.getText().toString());
                    Intent intent = new Intent(CreateActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("clockEnable", clockEnable);
                    bundle.putSerializable("reminder", reminder);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.getText().toString().equals("时间")) {
                    details.setEnabled(false);
                    LayoutInflater layoutInflater = LayoutInflater.from(CreateActivity.this);
                    View newView = layoutInflater.inflate(R.layout.dialog_time, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
                    builder.setView(newView);

                    final DatePicker datePicker = (DatePicker) newView.findViewById(R.id.datePicker);
                    final TimePicker timePicker = (TimePicker) newView.findViewById(R.id.timePicker);
                    timePicker.setIs24HourView(true);

                    if (!details.getText().toString().equals("")) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = null;
                        try {
                            date = simpleDateFormat.parse(details.getText().toString());
                            simpleDateFormat.applyPattern("yyyy-MM-dd-HH-mm-ss");
                            String string = simpleDateFormat.format(date);
                            String[] dates = string.split("-");
                            Log.v("date", dates[0]+" "+dates[1]+" "+dates[2]);
                            datePicker.updateDate(Integer.valueOf(dates[0]), Integer.valueOf(dates[1])-1, Integer.valueOf(dates[2]));
                            timePicker.setCurrentHour(Integer.valueOf(dates[3]));
                            timePicker.setCurrentMinute(Integer.valueOf(dates[4]));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    Dialog dialog = builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int year = datePicker.getYear();
                            int month = datePicker.getMonth()+1;
                            int date = datePicker.getDayOfMonth();
                            int hour = timePicker.getCurrentHour();
                            int minute = timePicker.getCurrentMinute();
                            String minuteStr = minute < 10 ? "0"+Integer.toString(minute) :Integer.toString(minute);
                            details.setText(Integer.toString(year)+"-"+Integer.toString(month)+"-"
                                    +Integer.toString(date)+" "+Integer.toString(hour)+":"+minuteStr+":00");
                        }
                    }).create();
                    dialog.show();
                } else {
                    details.setEnabled(true);
                }
            }
        });
    }

    private Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = UPDATE_CONTENT;
            message.obj = setUpConnection();
            handler.sendMessage(message);
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == UPDATE_CONTENT) {
                List<String> response = (List<String>) message.obj;
                JSONArray array;
                try {
                    // 解析分词JSON
                    array = new JSONArray(response.get(0));
                } catch (Exception e) {
                    wait.dismiss();
                    e.printStackTrace();
                    Toast.makeText(CreateActivity.this, "矮油服务器出错了呢(；′⌒`)要不要多试几次，或者手动输入试试？", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    LinearLayout linearLayout = new LinearLayout(CreateActivity.this);
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1));
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    array = array.getJSONArray(0);
                    array = array.getJSONArray(0);
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int rowNum = 5;
                    for (int i = 0; i < (array.length()/rowNum)+1; i++) {
                        int max = 0;
                        if (i == array.length()/rowNum) {
                            max = array.length()-i*rowNum;
                        } else {
                            max = rowNum;
                        }
                        TableRow tableRow = new TableRow(CreateActivity.this);
                        tableRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        for (int j = 0; j < max; j++) {
                            JSONObject object = array.getJSONObject(i*rowNum+j);
                            String word = object.getString("cont");
                            final Button button = new Button(CreateActivity.this);
                            button.setId(i*rowNum+j);
                            button.setText(word);
                            button.setTextSize(18);
                            button.setLayoutParams(new TableRow.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, metrics)*word.length()+(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, metrics), ViewGroup.LayoutParams.WRAP_CONTENT));
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addTextToDetails(button.getText().toString());
                                }
                            });
                            tableRow.addView(button);
                        }
                        linearLayout.addView(tableRow);
                    }

                    // JSONArray转化失败后不应改变视图
                    mainlayout.removeAllViews();
                    mainlayout.addView(linearLayout);
                    mainlayout.addView(after);
                    mainlayout.addView(complete);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateActivity.this, "啊(；′⌒`)出现了意想不到的错误", Toast.LENGTH_SHORT).show();
                }

                JSONObject timeobject;
                try {
                    timeobject = new JSONObject(response.get(1));
                    String[] timestamp = timeobject.getString("timestamp").split(" ");
                    date = timestamp[0];
                    time = timestamp[1];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                wait.dismiss();
            }
        }
    };

    public List<String> setUpConnection() {
        HttpURLConnection dividerConnection = null;
        final List<String> response = new ArrayList<>();
        try {
            String request = before.getText().toString();
            request = URLEncoder.encode(request, "UTF-8");
            dividerConnection = (HttpURLConnection)((new URL(dividerurl+"?api_key=q9R131y6HtyFTMCe3ukqNXXeHWGO2IWk6FRCaq2X&pattern=all&format=json&text="+request)).openConnection());
            dividerConnection.setRequestMethod("GET");
            dividerConnection.setReadTimeout(15000);
            dividerConnection.setConnectTimeout(15000);
            dividerConnection.setDoInput(true);
            InputStream inputStream = dividerConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder dividerResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                dividerResponse.append(line+"\n");
            }
            response.add(dividerResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dividerConnection != null) {
                dividerConnection.disconnect();
            }
        }

        HttpClient httpClient = new DefaultHttpClient();
        try {
            String request = before.getText().toString();
            request = URLEncoder.encode(request, "UTF-8");
            HttpPost httpPost = new HttpPost(bosonTimeUrl+request);

            //set Request header
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("X-Token", token);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            InputStream inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder timeResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = URLDecoder.decode(line, "UTF-8");
                timeResponse.append(line+"\n");
            }
            response.add(timeResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public void addTextToDetails(String text) {
        if (type.getText().toString().equals("时间")) {
            if (date.equals("") || time.equals("")) {
                Toast.makeText(CreateActivity.this, "时间解析失败了(；′⌒`)要不要手动输入试试？", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CreateActivity.this, "自动解析已完成，点击编辑框可更改", Toast.LENGTH_LONG).show();
                details.setText(date+" "+time);
            }
        } else {
            details.setText(details.getText()+text);
        }
    }

    @Override
    public void onBackPressed(){
        if ((type.getText().toString().equals("时间") && details.getText().toString().equals("")) && before.getText().toString().equals("")) {
            super.onBackPressed();
        } else {
             new AlertDialog.Builder(CreateActivity.this).setTitle("(；′⌒`)").setMessage("当前页面信息还没有保存哦，确定退出吗？")
                    .setPositiveButton("狠心退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CreateActivity.super.onBackPressed();
                        }
                    }).setNegativeButton("不了", null).setCancelable(false).show();
        }
    }
}
