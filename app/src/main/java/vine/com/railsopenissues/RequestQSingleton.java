package vine.com.railsopenissues;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Anand on 4/6/2016.
 */


public class RequestQSingleton {
    private static RequestQSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private RequestQSingleton(Context context) {
        mCtx = context;
        mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
    }

    public static synchronized RequestQSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestQSingleton(context);
        }
        return mInstance;
    }

    public void addToRequestQueue(Request<String> req) {
        req.setShouldCache(false);
        mRequestQueue.add(req);
    }

    public void cancelAllRequests() {
        mRequestQueue.cancelAll(this);
    }

}

