package vine.com.railsopenissues;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anand on 4/6/2016.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private ArrayList mIssueList;
    private CustomClickListener mClickListener;
    private boolean itemsEnabled;
    private MainActivity.RequestType recyclerViewType;
    final static int longMillis = 500;

    public CustomAdapter(ArrayList issueList, CustomClickListener clickListener, MainActivity.RequestType recyclerViewType) {
        mIssueList = issueList;
        mClickListener = clickListener;
        this.recyclerViewType = recyclerViewType;
        itemsEnabled = true;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_comments, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (recyclerViewType == MainActivity.RequestType.COMMENTS) return 0;
        else return 1;
    }

    @Override
    public void onViewAttachedToWindow(MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.setEnabled(itemsEnabled);
    }

    protected void setItemsEnabled(boolean val) {
        itemsEnabled = val;
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Object obj = mIssueList.get(position);
        if (obj instanceof IssueData) {
            holder.mIssueTitle.setText("Tittle: " + ((IssueData) obj).getTitle());
            holder.mIssueBody.setText(((IssueData) obj).getBodyText());
        } else {

                holder.mIssueTitle.setText(((CommentsData) obj).getUserName()+": ");
                holder.mIssueBody.setText(((CommentsData) obj).getComments());
        }
    }

    @Override
    public int getItemCount() {
        return mIssueList.size();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mIssueTitle;
        public TextView mIssueBody;

        public MyViewHolder(View view) {
            super(view);
            mIssueTitle = (TextView) view.findViewById(R.id.tv_issue_title);
            mIssueBody = (TextView) view.findViewById(R.id.tv_issue_body);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (!itemsEnabled) {
                return;
            }
            setItemsEnabled(false);
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setItemsEnabled(true);
                }
            }, longMillis);
            mClickListener.onClick(v, getAdapterPosition());
        }
    }
}
