package vine.com.railsopenissues;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CustomClickListener {

    private RecyclerView mIssuesRecyclerView;
    private ArrayList<IssueData> mIssueList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private CustomAdapter mCustomAdapter;
    final static String RAILS_ISSUE_URL = "https://api.github.com/repos/rails/rails/issues?sort=updated&direction=dsc";
    final static int REQUEST_TIMEOUT = 10000;

    enum RequestType {COMPLETE, PARTIAL}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIssueList = new ArrayList<>();

        mIssuesRecyclerView = (RecyclerView) findViewById(R.id.rv_issues);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mLayoutManager = new LinearLayoutManager(this);
        mIssuesRecyclerView.setLayoutManager(mLayoutManager);

        fetchIssueData(RAILS_ISSUE_URL, RequestType.COMPLETE, mIssueList);
        mCustomAdapter = new CustomAdapter(mIssueList, this, RequestType.COMPLETE);
        mIssuesRecyclerView.setLayoutManager(mLayoutManager);
        mIssuesRecyclerView.setAdapter(mCustomAdapter);
    }

    protected void fetchIssueData(String URL, final RequestType type, final ArrayList list) {
        Log.i("URL", URL);
        try {
            StringRequest requestString = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", response);
                            if (response != null) {
                                JSONArray resArr = null;
                                try {
                                    resArr = new JSONArray(response);
                                    parseIssueJSON(list, resArr, type);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NoConnectionError) {
                        // "No Internet Connection", "Please check your Internet connectivity"
                    } else {
                        //"Error", "Something went wrong. Please load again"
                    }
                }
            }
            );
            RequestQSingleton.getInstance(this).addToRequestQueue(requestString);
            requestString.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }


    }

    protected void parseIssueJSON(ArrayList mlist, JSONArray resArr, RequestType type) {
        mlist.clear();
        Object dataObj = null;
        switch (type) {
            case COMPLETE:
                for (int i = 0; i < resArr.length(); ++i) {
                    try {
                        dataObj = new IssueData(resArr.getJSONObject(i).getString("title"), resArr.getJSONObject(i).getString("body"), resArr.getJSONObject(i).getString("comments_url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mlist.add(dataObj);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCustomAdapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }

    @Override
    public void onClick(View v, int adapterPosition) {

    }

}
