package courseproject.huangyuming.wordsdividedreminder;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * Created by lusiwei on 2017/1/8.
 */

public class AddOnDataManager implements DatePicker.OnDateChangedListener,
        TimePicker.OnTimeChangedListener {
    private int year;
    private int month;
    private int day;
    private String time;
    private String location = "";
    private String keyword =  "";

    private Button dateButton = null;
    private Button timeButton = null;
    private DatePicker datePicker = null;
    private TimePicker timePicker = null;
    private EditText locationEditText = null;
    private EditText keywordEditText = null;

    private List<String> keywords = new ArrayList<>();
    private List<ListView> listViews = new ArrayList<>();
    private AddOnActivity.ListAdapter listViewAdapter = null;

    AddOnDataManager() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        setDate(dateFormat.format(date));
        setTime(date.getHours(), date.getMinutes());
    }

    int getYear() {
        return year;
    }
    int getMonth() {
        return month;
    }
    int getDay() {
        return day;
    }
    String getTime() {
        return time;
    }
    String getKeywordByPosition(int position) { return keywords.get(position); }
    void addKeyword(List<String> words) {
        keywords = words;
        listViewAdapter.setDataSource(keywords);
        if (listViewAdapter != null) {
            for (ListView listView : listViews) {
                listView.setAdapter(listViewAdapter);
            }
            listViewAdapter.notifyDataSetChanged();
        }
    }

    void setDate(String date) {
        String[] arr = date.split("-");
        setDate(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
    }
    void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;

        if (dateButton != null) {
            dateButton.setText(String.format("%04d-%02d-%02d", year, month, day));
        }
    }
    String getDate() {
        return String.format("%04d-%02d-%02d %s:00", year, month, day, time);
    }

    void setTime(String time) {
        String[] arr = time.split(":");
        setTime(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
    }
    void setTime(int hour, int minute) {
        time = String.format("%02d:%02d", hour, minute);

        if (timeButton != null) {
            timeButton.setText(time);
        }
    }

    void addLocation(String l) {
        location += l;
        if (locationEditText != null) { locationEditText.setText(location); }
    }
    void setLocation(String l) {
        location = l;
        if (locationEditText != null) { locationEditText.setText(location); }
    }
    String getLocation() {
        return location;
    }

    void addKeyword(String k) {
        keyword += k;
        if (keywordEditText != null) { keywordEditText.setText(keyword); }
    }
    void setKeyword(String k) {
        keyword = k;
        if (keywordEditText != null) { keywordEditText.setText(k); }
    }
    String getKeyword() {
        return keyword;
    }

    void setDateButton(Button dateButton) {
        this.dateButton = dateButton;
    }
    void setTimeButton(Button timeButton) {
        this.timeButton = timeButton;
    }
    void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
        this.datePicker.init(year, month, day, this);
        setDate(year, month, day);
    }
    void setTimePicker(TimePicker timePicker) {
        this.timePicker = timePicker;
        this.timePicker.setOnTimeChangedListener(this);
        setTime(time);
    }
    void setLocationEditText(EditText editText) {
        locationEditText = editText;
        locationEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { location = ((EditText) v).getText().toString(); }
        });
    }
    void setKeywordEditText(EditText editText) {
        keywordEditText = editText;
        keywordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { keyword = ((EditText) v).getText().toString(); }
        });
    }

    void setListViewAdapter(AddOnActivity.ListAdapter adapter) {
        listViewAdapter = adapter;
        listViewAdapter.setDataSource(keywords);
    }
    void addListView(ListView listView) {
        listViews.add(listView);
        listView.setAdapter(listViewAdapter);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        setDate(year, monthOfYear, dayOfMonth);
    }
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        setTime(hourOfDay, minute);
    }
}
