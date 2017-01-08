package courseproject.huangyuming.wordsdividedreminder;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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
import java.util.zip.Inflater;

import courseproject.huangyuming.bean.Reminder;
import courseproject.huangyuming.utility.SpeechRecognitionHelper;
import courseproject.huangyuming.utility.TimeParser;
import courseproject.huangyuming.utility.WordProcessor;

import static courseproject.huangyuming.wordsdividedreminder.R.id.details;

public class AddOnActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private View[] views;
    private LayoutInflater inflater;
    private EditText editText;
    private ImageButton nextBtn;
    private Dialog wait;

    private static final int UPDATE_CONTENT = 0;
    private WordProcessor wordProcessor = new WordProcessor();
    private AddOnDataManager dataManager = new AddOnDataManager();

    private ListAdapter listViewAdapter = new ListAdapter();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_on);

        inflater = getLayoutInflater();
        views = new View[]{getView1(), getView2(), getView3(), getView4()};

        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        if (clipboardManager.getText() != null && !clipboardManager.getText().toString().equals("")) {
            editText.setText(clipboardManager.getText().toString());
            clipboardManager.setText("");
        }

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(views[position]);
                return views[position];
            }

//            @Override
//            public void destroyItem(ViewGroup container, int position, Object object) {
//                super.destroyItem();
//            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        dataManager.setListViewAdapter(listViewAdapter);
    }

//    private Runnable networkTask = new Runnable() {
//        @Override
//        public void run() {
//            Message message = new Message();
//            message.what = UPDATE_CONTENT;
//            message.obj = setUpConnection();
//            handler.sendMessage(message);
//        }
//    };

//    public List<String> setUpConnection() {
//        HttpURLConnection dividerConnection = null;
//        final List<String> response = new ArrayList<>();
//        try {
//            String request = editText.getText().toString();
//            request = URLEncoder.encode(request, "UTF-8");
//            dividerConnection = (HttpURLConnection)((new URL(dividerurl+"?api_key=q9R131y6HtyFTMCe3ukqNXXeHWGO2IWk6FRCaq2X&pattern=all&format=json&text="+request)).openConnection());
//            dividerConnection.setRequestMethod("GET");
//            dividerConnection.setReadTimeout(15000);
//            dividerConnection.setConnectTimeout(15000);
//            dividerConnection.setDoInput(true);
//            InputStream inputStream = dividerConnection.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder dividerResponse = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                dividerResponse.append(line+"\n");
//            }
//            response.add(dividerResponse.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (dividerConnection != null) {
//                dividerConnection.disconnect();
//            }
//        }
//
//        HttpClient httpClient = new DefaultHttpClient();
//        try {
//            String request = editText.getText().toString();
//            request = URLEncoder.encode(request, "UTF-8");
//            HttpPost httpPost = new HttpPost(bosonTimeUrl+request);
//
//            //set Request header
//            httpPost.addHeader("Content-Type", "application/json");
//            httpPost.addHeader("Accept", "application/json");
//            httpPost.addHeader("X-Token", token);
//
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            HttpEntity entity = httpResponse.getEntity();
//            InputStream inputStream = entity.getContent();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder timeResponse = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                line = URLDecoder.decode(line, "UTF-8");
//                timeResponse.append(line+"\n");
//            }
//            response.add(timeResponse.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return response;
//    }

    private View getView1() {
        View view = inflater.inflate(R.layout.layout_input_content, viewPager);

        editText = (EditText) view.findViewById(R.id.edit_text);
        nextBtn = (ImageButton) view.findViewById(R.id.nextBtn);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    submit();
                }
                return false;
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        ImageButton startSRButton = (ImageButton) view.findViewById(R.id.startSRButton);
        startSRButton.setOnClickListener(view1 -> {
            SpeechRecognitionHelper.getInstance().setOnResultListener(new SpeechRecognitionHelper.OnResultListener() {
                @Override
                public void onResult(String fileId, String result) {
                    editText.setText(result);
                }

                @Override
                public void onError(String errorMsg) {
                    Toast.makeText(AddOnActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
            SpeechRecognitionHelper.getInstance().startRecognize(AddOnActivity.this);
        });

        return view;
    }
    private View getView2() {
        View view = inflater.inflate(R.layout.layout_select_time, viewPager);
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        Button dateButton = (Button) view.findViewById(R.id.dateButton);
        Button timeButton = (Button) view.findViewById(R.id.timeButton);

        dateButton.setOnClickListener(v -> {
            datePicker.setVisibility(View.VISIBLE);
            timePicker.setVisibility(View.GONE);
        });
        timeButton.setOnClickListener(v -> {
            timePicker.setVisibility(View.VISIBLE);
            datePicker.setVisibility(View.GONE);
        });

        dataManager.setDateButton(dateButton);
        dataManager.setDatePicker(datePicker);
        dataManager.setTimeButton(timeButton);
        dataManager.setTimePicker(timePicker);

        return view;
    }
    private View getView3() {
        View view = inflater.inflate(R.layout.layout_select_location, viewPager);
        ListView listView = (ListView) view.findViewById(R.id.addressListView);
        EditText editText = (EditText) view.findViewById(R.id.editText);
        ImageButton mapImageButton = (ImageButton) view.findViewById(R.id.mapImageButton);

        dataManager.addListView(listView);
        dataManager.setLocationEditText(editText);

        mapImageButton.setOnClickListener(v -> {
            startActivity(new Intent(AddOnActivity.this, MapActivity.class));
        });

        listView.setOnItemClickListener((parent, view1, position, id) ->
                dataManager.addLocation(dataManager.getKeywordByPosition(position)));

        return view;
    }
    private View getView4() {
        View view = inflater.inflate(R.layout.layout_select_keyword, viewPager);
        ListView listView = (ListView) view.findViewById(R.id.keywordListView);
        EditText editText = (EditText) view.findViewById(R.id.keywordEditText);
        ImageButton doneButton = (ImageButton) view.findViewById(R.id.doneImageButton);

        dataManager.addListView(listView);
        dataManager.setKeywordEditText(editText);

        listView.setOnItemClickListener((parent, view1, position, id) ->
                dataManager.addKeyword(dataManager.getKeywordByPosition(position)));

        doneButton.setOnClickListener(v -> {
            Reminder reminder = new Reminder(dataManager.getDate(),
                    dataManager.getLocation(),
                    dataManager.getKeyword());
            Intent intent = new Intent(AddOnActivity.this, MainActivity.class);
            Bundle bundle = new Bundle();

            new AlertDialog.Builder(AddOnActivity.this)
                    .setIcon(R.mipmap.clock).setTitle("闹钟").setMessage("是否添加闹钟提醒？")
                    .setCancelable(false)
                    .setNegativeButton("是", (dialog, which) -> {
                        bundle.putBoolean(getResources().getString(R.string.clock_enable), true);
                        bundle.putSerializable(getResources().getString(R.string.reminder), reminder);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    })
                    .setPositiveButton("否", (dialog, which) -> {
                        bundle.putBoolean(getResources().getString(R.string.clock_enable), false);
                        bundle.putSerializable(getResources().getString(R.string.reminder), reminder);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    })
                    .show();
        });

        return view;
    }

    private void submit() {
        if (editText.getText().toString().equals("")) {
            Dialog dialog = new AlertDialog.Builder(AddOnActivity.this).setTitle("注意")
                    .setPositiveButton("知道了", null).setMessage("时间、地点、事件不能为空哦o(*￣▽￣*)ブ").create();
            dialog.show();
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new Thread(() -> {
                    List<String> words = wordProcessor.divider(editText.getText().toString());
                    String[] timeResult = wordProcessor.bosonTime(editText.getText().toString());

                    runOnUiThread(() -> {
                        if (timeResult != null) {
                            dataManager.setDate(timeResult[0]);
                            dataManager.setTime(timeResult[1]);
                        }
                        dataManager.addKeyword(words);
                        wait.dismiss();
                    });
                }).start();
                wait = ProgressDialog.show(AddOnActivity.this, "", "正在加载中，请耐心等待......");
                wait.show();
            } else {
                Toast.makeText(AddOnActivity.this, "当前没有可用网络哦_(:з)∠)_请检查你的网络连接", Toast.LENGTH_LONG).show();
            }
        }
        viewPager.setCurrentItem(1, true);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AddOn Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class ListAdapter extends BaseAdapter {

        private List<String> dataSource = new ArrayList<>();

        public void setDataSource(List<String> source) {
            dataSource.addAll(source);
        }

        @Override
        public int getCount() {
            return dataSource.size();
        }

        @Override
        public Object getItem(int i) {
            return dataSource.get(i);
        }

        @Override
        public long getItemId(int i) {
            return (long) i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = view == null ? inflater.inflate(android.R.layout.simple_list_item_1, null) : view;
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(24);
            textView.setText(dataSource.get(i));
            return view;
        }

    }

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message message) {
//            super.handleMessage(message);
//            if (message.what == UPDATE_CONTENT) {
//                List<String> response = (List<String>) message.obj;
//                JSONArray array;
//                try {
//                    // 解析分词JSON
//                    array = new JSONArray(response.get(0));
//                } catch (Exception e) {
//                    wait.dismiss();
//                    e.printStackTrace();
//                    Toast.makeText(AddOnActivity.this, "矮油服务器出错了呢(；′⌒`)要不要多试几次，或者手动输入试试？", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                try {
//                    LinearLayout linearLayout = new LinearLayout(AddOnActivity.this);
//                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1));
//                    linearLayout.setOrientation(LinearLayout.VERTICAL);
//
//                    array = array.getJSONArray(0);
//                    array = array.getJSONArray(0);
//                    DisplayMetrics metrics = new DisplayMetrics();
//                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                    int rowNum = 5;
//                    for (int i = 0; i < (array.length()/rowNum)+1; i++) {
//                        int max = 0;
//                        if (i == array.length()/rowNum) {
//                            max = array.length()-i*rowNum;
//                        } else {
//                            max = rowNum;
//                        }
//                        TableRow tableRow = new TableRow(AddOnActivity.this);
//                        tableRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                        for (int j = 0; j < max; j++) {
//                            JSONObject object = array.getJSONObject(i*rowNum+j);
//                            String word = object.getString("cont");
//                            final Button button = new Button(AddOnActivity.this);
//                            button.setId(i*rowNum+j);
//                            button.setText(word);
//                            button.setTextSize(18);
//                            button.setLayoutParams(new TableRow.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, metrics)*word.length()+(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, metrics), ViewGroup.LayoutParams.WRAP_CONTENT));
////                            button.setOnClickListener(new View.OnClickListener() {
////                                @Override
////                                public void onClick(View view) {
////                                    addTextToDetails(button.getText().toString());
////                                }
////                            });
//                            tableRow.addView(button);
//                        }
//                        linearLayout.addView(tableRow);
//                    }
//
//                    // JSONArray转化失败后不应改变视图
////                    mainlayout.removeAllViews();
////                    mainlayout.addView(linearLayout);
////                    mainlayout.addView(after);
////                    mainlayout.addView(complete);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(AddOnActivity.this, "啊(；′⌒`)出现了意想不到的错误", Toast.LENGTH_SHORT).show();
//                }
//
//                JSONObject timeobject;
////                try {
////                    timeobject = new JSONObject(response.get(1));
////                    String[] timestamp = timeobject.getString("timestamp").split(" ");
////                    date = timestamp[0];
////                    time = timestamp[1];
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//
//                wait.dismiss();
//            }
//        }
//    };
}
