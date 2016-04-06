package vine.com.railsopenissues;

import android.view.View;

/**
 * Created by Anand on 4/6/2016.
 *
 * CustomClickListener.java - This Interface is used to handle the click events of Recycler view.
 */
public interface CustomClickListener {
    void onClick(View v, int adapterPosition);
}
