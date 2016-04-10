package ua.nure.vkmessanger.model;

import java.util.Date;

/**
 * Объект записи на стене.
 */
public class WallPost {

    private int id;
    //to_id
    private int mWallOwnerId;
    //from_id
    private int mWallAuthorId;

    private Date mDate;

    private String mText;

    private String mPostType;

//    private Attachment mAttachment;


    public WallPost(int id, int wallOwnerId, int wallAuthorId, Date date, String text, String postType) {
        this.id = id;
        mWallOwnerId = wallOwnerId;
        mWallAuthorId = wallAuthorId;
        mDate = date;
        mText = text;
        mPostType = postType;
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
}