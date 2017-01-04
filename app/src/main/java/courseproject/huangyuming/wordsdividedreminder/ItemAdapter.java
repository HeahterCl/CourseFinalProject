package courseproject.huangyuming.wordsdividedreminder;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.woxthebox.draglistview.DragItemAdapter;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import courseproject.huangyuming.CustomView.ClockView;
import courseproject.huangyuming.bean.Reminder;
import courseproject.huangyuming.bean.ReminderDao;

/**
 * Created by ym on 16-10-16.
 */

public class ItemAdapter extends DragItemAdapter<Pair<Long, Reminder>, ItemAdapter.ViewHolder> {

    private Context mContext;
    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;

    public ItemAdapter(Context context, ArrayList<Pair<Long, Reminder>> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        mContext = context;
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        setHasStableIds(true);
        setItemList(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Reminder h = mItemList.get(position).second;

        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = fmt.parse(h.getTime());
            holder.time.setTime(d.getHours(), d.getMinutes());
        } catch (ParseException e) {
            e.printStackTrace();
            holder.time.setTime(0, 0);
        }
        holder.position.setText(h.getPosition());
        holder.contents.setText(h.getTasks());

        if (h.getFinished()) {
            holder.toggleButton.setChecked(true);
            holder.root.setBackground(mContext.getResources().getDrawable(R.drawable.listitem_style_complete));
        } else {
            holder.root.setBackground(mContext.getResources().getDrawable(R.drawable.listitem_style_incomplete));
        }

    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    public class ViewHolder extends DragItemAdapter.ViewHolder {

        public ClockView time;
        public TextView position;
        public TextView contents;
        public LinearLayout root;
        public ToggleButton toggleButton;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);

            time = (ClockView) itemView.findViewById(R.id.time);
            position = (TextView) itemView.findViewById(R.id.position);
            contents = (TextView) itemView.findViewById(R.id.contents);
            root = (LinearLayout) itemView.findViewById(R.id.root);
            toggleButton = (ToggleButton) itemView.findViewById(R.id.toggleButton);
        }

        @Override
        public boolean onItemTouch(View view, MotionEvent event) {

            view.setBackgroundColor(0xFF000000);

            Toast.makeText(view.getContext(), "Item touched", Toast.LENGTH_SHORT).show();

            return true;
        }

        @Override
        public void onItemClicked(final View view) {
            final Reminder r = getItemList().get(getAdapterPosition()).second;
            if (!r.getFinished()) {
                Dialog dialog = new AlertDialog.Builder(view.getContext()).setTitle("(⊙ˍ⊙)").setMessage("确定将其设置为已完成？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        r.setFinished(true);
                        try {
                            DatabaseHelper.getHelper(mContext).getRemindersDao().update(r);
                            notifyItemChanged(getAdapterPosition());
//                        ReminderDao reminderDao = new ReminderDao(view.getContext());
//                        reminderDao.delete(Reminder.UPDATE_TIME, time.getText().toString());
//                        getItemList().remove(getItemId());
                            // TODO 更新UI
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("取消", null).create();
                dialog.show();
            }
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}