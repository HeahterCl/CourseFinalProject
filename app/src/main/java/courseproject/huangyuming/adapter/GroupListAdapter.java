package courseproject.huangyuming.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import courseproject.huangyuming.CustomView.ClockView;
import courseproject.huangyuming.bean.Reminder;
import courseproject.huangyuming.utility.TimeParser;
import courseproject.huangyuming.wordsdividedreminder.DatabaseHelper;
import courseproject.huangyuming.wordsdividedreminder.MainActivity;
import courseproject.huangyuming.wordsdividedreminder.R;

/**
 * Created by huangyuming on 17-1-4.
 */

public class GroupListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Pair<Integer, Object>> mGroupedList;

    static final public int VIEW_TYPE_NORMAL = 0;
    static final public int VIEW_TYPE_DIVIDER = 1;

    private OnItemClickListener mOnNormalItemClickListener;
    private OnItemClickListener mOnDividerItemClickListener;
    public void setOnNormalItemClickListener(OnItemClickListener mOnNormalItemClickListener)
    {
        this.mOnNormalItemClickListener = mOnNormalItemClickListener;
    }
    public void setOnDividerItemClickListener(OnItemClickListener mOnDividerItemClickListener)
    {
        this.mOnDividerItemClickListener = mOnDividerItemClickListener;
    }

    public GroupListAdapter(Context mContext, ArrayList<Pair<Integer, Object>> mGroupedList) {
        this.mContext = mContext;
        this.mGroupedList = mGroupedList;
    }

    public Pair<Integer, Object> getItem(int position) {
        return mGroupedList.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_NORMAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new NormalViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_divider, parent, false);
            return new DividerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (getItemViewType(position) == VIEW_TYPE_NORMAL) {

            Reminder r = (Reminder) mGroupedList.get(position).second;
            final NormalViewHolder holder = (NormalViewHolder)viewHolder;

            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date d = fmt.parse(r.getTime());
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                holder.time.setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.time.setTime(0, 0);
            }
            holder.position.setText(r.getPosition());
            holder.contents.setText(r.getTasks());

            if (r.getFinished()) {
                holder.toggleButton.setChecked(true);
                holder.root.setBackground(mContext.getResources().getDrawable(R.drawable.listitem_style_complete));
            } else {
                holder.toggleButton.setChecked(false);
                holder.root.setBackground(mContext.getResources().getDrawable(R.drawable.listitem_style_incomplete));
            }

            holder.toggleDetector.setTag(position);
            holder.toggleDetector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final int position = (int) view.getTag();
                    final Reminder r = (Reminder) getItem(position).second;

                    if (!r.getFinished()) {
                        Dialog dialog = new AlertDialog.Builder(view.getContext()).setTitle("(⊙ˍ⊙)").setMessage("确定将其设置为已完成？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                r.setFinished(true);
                                try {
                                    DatabaseHelper.getHelper(mContext).getRemindersDao().update(r);
                                    notifyItemChanged(position);
//                                    ReminderDao reminderDao = new ReminderDao(view.getContext());
//                                    reminderDao.delete(Reminder.UPDATE_TIME, time.getText().toString());
//                                    getItemList().remove(getItemId());
                                    // TODO 更新UI
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("取消", null).create();
                        dialog.show();
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnNormalItemClickListener != null) {
                        mOnNormalItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
                    }
                }
            });

        }
        else {

            final DividerViewHolder holder = (DividerViewHolder)viewHolder;

            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) mGroupedList.get(position).second);

            holder.year.setText(cal.get(Calendar.YEAR)+"");
            holder.month.setText(TimeParser.month2English(cal.get(Calendar.MONTH)+1));
            holder.day.setText(TimeParser.zeroPadding(cal.get(Calendar.DAY_OF_MONTH), 2));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnDividerItemClickListener != null) {
                        mOnDividerItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mGroupedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mGroupedList.get(position).first;
    }

    public class DividerViewHolder extends RecyclerView.ViewHolder {
        public TextView year;
        public TextView month;
        public TextView day;

        public DividerViewHolder ( View itemView ) {
            super(itemView);

            year = (TextView) itemView.findViewById(R.id.year);
            month = (TextView) itemView.findViewById(R.id.month);
            day = (TextView) itemView.findViewById(R.id.day);
        }

    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        public ClockView time;
        public TextView position;
        public TextView contents;
        public LinearLayout root;
        public ToggleButton toggleButton;
        public LinearLayout toggleDetector;

        public NormalViewHolder(final View itemView) {
            super(itemView);
            time = (ClockView) itemView.findViewById(R.id.time);
            position = (TextView) itemView.findViewById(R.id.position);
            contents = (TextView) itemView.findViewById(R.id.contents);
            root = (LinearLayout) itemView.findViewById(R.id.root);
            toggleButton = (ToggleButton) itemView.findViewById(R.id.toggleButton);
            toggleDetector = (LinearLayout) itemView.findViewById(R.id.toggleDetector);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
