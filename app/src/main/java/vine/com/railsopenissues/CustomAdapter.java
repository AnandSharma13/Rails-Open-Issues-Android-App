package vine.com.railsopenissues;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anand on 4/6/2016.
 *
 * CustomAdapter.java - This class is a custom adapter for the Recycler view.
 *
 * This Adapter is used to display both, Issues and Comments data, in recycler views.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private ArrayList mIssueList;
    private CustomClickListener mClickListener;
    private boolean itemsEnabled;
    private MainActivity.RequestType recyclerViewType;
    final static int longMillis = 1000;

    /**
     * Constructor of CustomAdapter class
     * @param issueList list of objects to be populated in the Recycler view
     * @param clickListener Click listener of recycler view
     * @param recyclerViewType Type of the view to be populated
     */
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

    /**
     * Returns the view type of the item at position for the purposes of view recycling.
     * @param position
     * @return return 1 if the recycler view is for comments and returns 0 if the recycler view is being used to display issues
     */
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

    /**
     * Enables/Disables the on click event of recycler view items
     * @param val
     */
    protected void setItemsEnabled(boolean val) {
        itemsEnabled = val;
        notifyItemRangeChanged(0, getItemCount());
    }

    /**
     * Bind the holder with appropriate object (Comments/Issue)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Object obj = mIssueList.get(position);

        if (obj instanceof IssueData) {
            holder.mTitle.setText("Tittle: " + ((IssueData) obj).getTitle());
            holder.mBody.setText(((IssueData) obj).getBodyText());
        } else {

                holder.mTitle.setText(((CommentsData) obj).getUserName() + ": ");
                holder.mBody.setText(((CommentsData) obj).getComments());
        }
    }

    @Override
    public int getItemCount() {
        return mIssueList.size();
    }

    /**
     * MyViewHolder - Inner class that defines the data inside the Recycler view
     */
    protected class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTitle;
        public TextView mBody;

        public MyViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.tv_issue_title);
            mBody = (TextView) view.findViewById(R.id.tv_issue_body);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (!itemsEnabled) {
                return;
            }
            //disable the items to handle fast multiple click events
            setItemsEnabled(false);
            mClickListener.onClick(v, getAdapterPosition());
        }
    }
}
