package courseproject.huangyuming.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import courseproject.huangyuming.CustomView.ClockView;
import courseproject.huangyuming.bean.Reminder;
import courseproject.huangyuming.wordsdividedreminder.DatabaseHelper;
import courseproject.huangyuming.wordsdividedreminder.R;

/**
 * Created by huangyuming on 17-1-6.
 */

public class SearchListAdapter extends BaseAdapter {

    private Context context;
    private List<Reminder> dataList;

    public SearchListAdapter(Context context, List<Reminder> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_search, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        // render
        Reminder r = dataList.get(position);

        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat zhFmt = new SimpleDateFormat("yyyy/M/d");
        String dateString;
        try {
            Date d = fmt.parse(r.getTime());
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            holder.time.setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            dateString = zhFmt.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            holder.time.setTime(0, 0);
            dateString = "未设置时间";
        }
        holder.position.setText(r.getPosition());
        holder.contents.setText(r.getTasks());
        holder.date.setText(dateString);

        if (r.getFinished()) {
            holder.toggleButton.setChecked(true);
//            holder.root.setBackground(context.getResources().getDrawable(R.drawable.listitem_style_complete));
        } else {
            holder.toggleButton.setChecked(false);
//            holder.root.setBackground(context.getResources().getDrawable(R.drawable.listitem_style_incomplete));
        }

        holder.toggleDetector.setTag(position);
        holder.toggleDetector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int position = (int) view.getTag();
                final Reminder r = dataList.get(position);

                try {
                    r.setFinished(!r.getFinished());
                    DatabaseHelper.getHelper(context).getRemindersDao().update(r);
                    SearchListAdapter.super.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        return view;
    }

    private class ViewHolder {
        public ClockView time;
        public TextView position;
        public TextView contents;
        public TextView date;
        public LinearLayout root;
        public ToggleButton toggleButton;
        public LinearLayout toggleDetector;
        public ViewHolder(View itemView) {
            time = (ClockView) itemView.findViewById(R.id.time);
            position = (TextView) itemView.findViewById(R.id.position);
            contents = (TextView) itemView.findViewById(R.id.contents);
            date = (TextView) itemView.findViewById(R.id.date);
            root = (LinearLayout) itemView.findViewById(R.id.root);
            toggleButton = (ToggleButton) itemView.findViewById(R.id.toggleButton);
            toggleDetector = (LinearLayout) itemView.findViewById(R.id.toggleDetector);
        }
    }

}
