package ua.nure.vkmessanger.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * Объект записи на стене.
 */
public class WallPost implements Serializable {

    private int id;
    //to_id
    private int mWallOwnerId;
    //from_id
    private int mWallAuthorId;

    private Date mDate;

    private String mText;

    private String mPostType;

    private Attachment[] mAttachments;

    public WallPost(int id, int wallOwnerId, int wallAuthorId, Date date, String text, String postType, Attachment[] attachments) {
        this.id = id;
        mWallOwnerId = wallOwnerId;
        mWallAuthorId = wallAuthorId;
        mDate = date;
        mText = text;
        mPostType = postType;
        mAttachments = attachments;
    }

    public int getId() {
        return id;
    }

    public int getWallOwnerId() {
        return mWallOwnerId;
    }

    public int getWallAuthorId() {
        return mWallAuthorId;
    }

    public Date getDate() {
        return mDate;
    }

    public String getText() {
        return mText;
    }

    public String getPostType() {
        return mPostType;
    }

    public Attachment[] getAttachments() {
        return mAttachments;
    }

    @Override
    public String toString() {
        return "WallPost{" +
                "id=" + id +
                ", mWallOwnerId=" + mWallOwnerId +
                ", mWallAuthorId=" + mWallAuthorId +
                ", mDate=" + mDate +
                ", mText='" + mText + '\'' +
                ", mPostType='" + mPostType + '\'' +
                ", mAttachments=" + Arrays.toString(mAttachments) +
                '}';
    }
}