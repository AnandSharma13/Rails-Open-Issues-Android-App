package vine.com.railsopenissues;

/**
 * Created by Anand on 4/6/2016.
 *
 * IssueData.java - This class holds one Rails organization issue data
 */
public class IssueData {
    private String mTitle;
    private String mBodyText;
    private String mCommentsURL;

    public IssueData(String title, String bodyText, String commentsURL) {
        mTitle = title;
        mBodyText = bodyText;
        mCommentsURL = commentsURL;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBodyText() {
        return mBodyText;
    }

    public String getCommentsURL() {
        return mCommentsURL;
    }
}

