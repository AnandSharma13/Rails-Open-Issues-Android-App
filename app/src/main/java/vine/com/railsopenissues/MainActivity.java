package vine.com.railsopenissues;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Anand on 4/6/2016.
 *
 * MainActivity.java - This class launches the application and displays a list of Issues.
 *
 */

public class MainActivity extends AppCompatActivity implements CustomClickListener {

    private RecyclerView mIssuesRecyclerView;
    private ArrayList<IssueData> mIssueList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private CustomAdapter mCustomAdapter;
    private ProgressBar mProgressBar;
    final static String RAILS_ISSUE_URL = "https://api.github.com/repos/rails/rails/issues?sort=updated&direction=dsc";
    final static int REQUEST_TIMEOUT = 10000;

    enum RequestType {ISSUES, COMMENTS}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIssueList = new ArrayList<>();
        mIssuesRecyclerView = (RecyclerView) findViewById(R.id.rv_issues);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mLayoutManager = new LinearLayoutManager(this);
        mIssuesRecyclerView.setLayoutManager(mLayoutManager);

        fetchIssueData(RAILS_ISSUE_URL, RequestType.ISSUES, mIssueList);
        mCustomAdapter = new CustomAdapter(mIssueList, this, RequestType.ISSUES);
        mIssuesRecyclerView.setLayoutManager(mLayoutManager);
        mIssuesRecyclerView.setAdapter(mCustomAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchIssueData(RAILS_ISSUE_URL, RequestType.ISSUES, mIssueList);
            }
        });
    }

    /**
     * This functions retries data from a specified URL and parses the JSON file
     * @param URL Request URL
     * @param type This defines the type of request. The request type can be for Comments or Issues
     * @param list A list that holds the items of Recycler view
     */
    protected void fetchIssueData(String URL, final RequestType type, final ArrayList list) {
        Log.i("URL", URL);
        try {
            StringRequest requestString = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                mProgressBar.setVisibility(View.GONE);
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
                        showAlert("No Internet Connection", "Please check your Internet connectivity");
                    } else {
                        showAlert("Error", "Something went wrong. Please load again");
                    }
                }
            }
            );
            RequestQSingleton.getInstance(this).addToRequestQueue(requestString);
            requestString.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancels all http volley requests when the application is closed.
        RequestQSingleton.getInstance(this).cancelAllRequests();
    }

    /**
     * This functions parses the response JSON according to the request type
     * @param mlist Reference of ArrayList to be populated in Recycler view
     * @param resArr JSON response from HTTP volley
     * @param type  Type of request (ISSUES, COMMENTS)
     */
    protected void parseIssueJSON(ArrayList mlist, JSONArray resArr, RequestType type) {
        mlist.clear();
        Object dataObj = null;
        switch (type) {
            case ISSUES:
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
            case COMMENTS:
                for (int i = 0; i < resArr.length(); ++i) {
                    try {
                        dataObj = new CommentsData(resArr.getJSONObject(i).getString("body"), new JSONObject(resArr.getJSONObject(i).getString("user")).getString("login"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mlist.add(dataObj);
                }
                displayComments(mlist);
                break;
        }
    }

    /**
     * Displays the comments in a Recycler view in a Fragment dialog
     * @param list Items to be populated in RecyclerView
     */
    protected void displayComments(ArrayList<CommentsData> list) {
        try {
            FragmentManager fm = getFragmentManager();
            MyDialogFragment dialogFragment = MyDialogFragment.newInstance(list);
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
            dialogFragment.show(fm, "DialogFragment");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            mCustomAdapter.setItemsEnabled(true);
        }

    }

    /**
     * Displays an alert dialog
     * @param title Title of the dialog
     * @param message Message to be displayed
     * @return return a AlertDialog.Builder
     */
    protected AlertDialog.Builder showAlert(String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
        return alertDialog;
    }

    /**
     * Inflates the menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Defines the click handlers of different menu items
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                fetchIssueData(RAILS_ISSUE_URL, RequestType.ISSUES, mIssueList);
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This function handles the RecyclerView click events
     * @param v The view which is clicked
     * @param adapterPosition Position of the item clicked
     */
    @Override
    public void onClick(View v, int adapterPosition) {
        mProgressBar.setVisibility(View.VISIBLE);
        fetchIssueData(mIssueList.get(adapterPosition).getCommentsURL(), RequestType.COMMENTS, new ArrayList());
    }

}
