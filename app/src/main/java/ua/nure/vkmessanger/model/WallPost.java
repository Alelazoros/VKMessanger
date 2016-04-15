package ua.nure.vkmessanger.model;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * Объект записи на стене.
 * https://vk.com/dev/post
 */
public class WallPost implements Serializable {

    private int id;

    /**
     * 'to_id'
     * Идентификатор владельца стены, на которой размещена запись.
     * В JSON приходит отрицательное число -> надо получить его модуль,
     * когда потребуется получить владельца стены (сообщество или пользователь).
     */
    private int mWallOwnerId;

    /**
     * 'from_id'
     * Идентификатор автора записи.
     * В JSON приходит отрицательное число -> надо получить его модуль,
     * когда потребуется получить автора записи (сообщество или пользователь).
     */
    private int mWallAuthorId;

    private Date mDate;

    private String mText;

    /**
     * Тип записи, может принимать следующие значения: post, copy, reply, postpone, suggest.
     */
    private String mPostType;

    /**
     * Идентификатор автора, если запись была опубликована от имени сообщества и подписана пользователем.
     */
    private int mSignerId;

    /**
     * Массив, содержащий историю репостов для записи. Возвращается только в том случае, если запись является репостом.
     * Каждый из объектов массива, в свою очередь, является объектом-записью стандартного формата.
     */
    @Nullable
    private WallPost[] mCopyHistory;

    @Nullable
    private Attachment[] mAttachments;

    //Возможно позже понадобятся поля post_source, comments, likes, reposts.

    public WallPost(int id, int wallOwnerId, int wallAuthorId, Date date, String text, String postType, int signerId,
                    @Nullable WallPost[] copyHistory, @Nullable Attachment[] attachments) {
        this.id = id;
        mWallOwnerId = wallOwnerId;
        mWallAuthorId = wallAuthorId;
        mDate = date;
        mText = text;
        mPostType = postType;
        mSignerId = signerId;
        mCopyHistory = copyHistory;
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

    public int getSignerId() {
        return mSignerId;
    }

    @Nullable
    public WallPost[] getCopyHistory() {
        return mCopyHistory;
    }

    @Nullable
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
                ", mSignerId=" + mSignerId +
                ", mCopyHistory=" + Arrays.toString(mCopyHistory) +
                ", mAttachments=" + Arrays.toString(mAttachments) +
                '}';
    }
}