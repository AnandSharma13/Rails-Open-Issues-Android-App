package vine.com.railsopenissues;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Anand on 4/6/2016.
 */
public class MyDialogFragment extends DialogFragment implements  CustomClickListener{

    private RecyclerView mCommentsRecyclerView;
    private ArrayList<IssueData> mList;
    private Button btnClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("COMMENTS");
        View rootView = inflater.inflate(R.layout.fragment_dialog, container, false);
        mCommentsRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_comments);
        btnClose = (Button) rootView.findViewById(R.id.btn_close);
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCommentsRecyclerView.setAdapter(new CustomAdapter(mList, this, MainActivity.RequestType.COMMENTS));

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }

    public static MyDialogFragment newInstance(ArrayList<CommentsData> list) {
        MyDialogFragment myFragment = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("COMMENTS_LIST", list);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
             mList = (ArrayList<IssueData>)getArguments().getSerializable("COMMENTS_LIST");
        }
    }


}
