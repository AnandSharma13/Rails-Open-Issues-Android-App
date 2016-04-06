package vine.com.railsopenissues;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Anand on 4/6/2016.
 *
 * RequestQSingleton.java - A Singleton class for the Http volley request queue
 */


public class RequestQSingleton {
    private static RequestQSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    /**
     * Private constructor of the class
     * @param context
     */
    private RequestQSingleton(Context context) {
        mCtx = context;
        mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
    }

    /**
     * Thread safe method that return an object of the RequestQSingleton class.
     * @param context
     * @return
     */
    public static synchronized RequestQSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestQSingleton(context);
        }
        return mInstance;
    }

    /**
     * Adds a request to the request queue
     * @param req
     */
    public void addToRequestQueue(Request<String> req) {
        req.setShouldCache(false);
        mRequestQueue.add(req);
    }

    /**
     * cancels all requests in the request queue
     */
    public void cancelAllRequests() {
        mRequestQueue.cancelAll(this);
    }

}

