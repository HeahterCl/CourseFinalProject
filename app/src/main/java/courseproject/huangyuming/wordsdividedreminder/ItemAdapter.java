package courseproject.huangyuming.wordsdividedreminder;

import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

import courseproject.huangyuming.bean.Reminder;

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

//        String shortWrite = "";

        holder.mTitle.setText(h.getLessonName());
        holder.mContent.setText(h.getContent());
        holder.mDdl.setText(h.getDdl());
//        holder.itemView.setTag(text);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    public class ViewHolder extends DragItemAdapter.ViewHolder {
        public TextView mTitle;
        public TextView mContent;
        public TextView mDdl;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mContent = (TextView) itemView.findViewById(R.id.content);
            mDdl = (TextView) itemView.findViewById(R.id.ddl);
        }

        @Override
        public boolean onItemTouch(View view, MotionEvent event) {

            view.setBackgroundColor(0xFF000000);

            Toast.makeText(view.getContext(), "Item touched", Toast.LENGTH_SHORT).show();

            return true;
        }

        @Override
        public void onItemClicked(View view) {
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}