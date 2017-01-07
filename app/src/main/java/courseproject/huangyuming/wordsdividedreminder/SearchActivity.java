package courseproject.huangyuming.wordsdividedreminder;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import courseproject.huangyuming.adapter.SearchListAdapter;
import courseproject.huangyuming.bean.Reminder;

/**
 * Created by huangyuming on 17-1-6.
 */

public class SearchActivity extends AppCompatActivity {

    ListView listView;
    SearchView searchView;
    List<Reminder> rawList = new ArrayList<>();
    List<Reminder> filteredList = new ArrayList<>();
    SearchListAdapter searchListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView  = (ListView) findViewById(R.id.listView);
        searchView = (SearchView) findViewById(R.id.searchView);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        SearchView.SearchAutoComplete textView = ( SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        textView.setTextColor(Color.WHITE);
//        View v = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
//        v.setBackgroundColor(Color.WHITE);c
        searchView.onActionViewExpanded();
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredList.clear();
                for (Reminder item : rawList) {
                    if (item.getTime().contains(newText) || item.getPosition().contains(newText) || item.getTasks().contains(newText)) {
                        filteredList.add(item);
                    }
                }
                searchListAdapter.notifyDataSetChanged();

                return false;
            }
        });

        try {
            rawList = DatabaseHelper.getHelper(SearchActivity.this).getRemindersDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        filteredList.addAll(rawList);
        searchListAdapter = new SearchListAdapter(SearchActivity.this, filteredList);
        listView.setAdapter(searchListAdapter);

    }
}
