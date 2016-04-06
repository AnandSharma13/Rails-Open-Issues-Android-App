package vine.com.railsopenissues;

import java.io.Serializable;

/**
 * Created by Anand on 4/6/2016.
 *
 * CommentsData.java- This class holds the data of Comment on a issue.
 *
 * Class implements the Serializable to serialize its objects.
 */
public class CommentsData implements Serializable {

    private String mUserName;
    private String mComments;

    public CommentsData(String commentsURL, String bodyText) {
        mComments = commentsURL;
        mUserName = bodyText;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getComments() {
        return mComments;
    }

}
