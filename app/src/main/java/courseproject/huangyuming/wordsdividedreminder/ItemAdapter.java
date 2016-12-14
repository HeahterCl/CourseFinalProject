package courseproject.huangyuming.wordsdividedreminder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import java.sql.SQLException;
import java.util.ArrayList;

import courseproject.huangyuming.bean.Reminder;
import courseproject.huangyuming.bean.ReminderDao;

/**
 * Created by ym on 16-10-16.
 */

public class ItemAdapter extends DragItemAdapter<Pair<Long, Reminder>, ItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;

    public ItemAdapter(ArrayList<Pair<Long, Reminder>> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
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

        holder.time.setText(h.getTime());
        holder.position.setText(h.getPosition());
        holder.contents.setText(h.getTasks());
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    public class ViewHolder extends DragItemAdapter.ViewHolder {
        public TextView time;
        public TextView position;
        public TextView contents;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);

            time = (TextView) itemView.findViewById(R.id.time);
            position = (TextView) itemView.findViewById(R.id.position);
            contents = (TextView) itemView.findViewById(R.id.contents);
        }

        @Override
        public boolean onItemTouch(View view, MotionEvent event) {

            view.setBackgroundColor(0xFF000000);

            Toast.makeText(view.getContext(), "Item touched", Toast.LENGTH_SHORT).show();

            return true;
        }

        @Override
        public void onItemClicked(final View view) {
            Dialog dialog = new AlertDialog.Builder(view.getContext()).setTitle("(⊙ˍ⊙)").setMessage("确定要删除吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        ReminderDao reminderDao = new ReminderDao(view.getContext());
                        reminderDao.delete(Reminder.UPDATE_TIME, time.getText().toString());
                        getItemList().remove(getItemId());
                        // TODO 更新UI
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }).setNegativeButton("取消", null).create();
            dialog.show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}