package ua.nure.vkmessanger.model;

import java.util.Date;

/**
 * Данный объект представляет собой одно сообщение в диалоге с выбранным собеседником.
 */
public class Message {

    private int mMessageId;

    /**
     * Когда отправляю свое сообщение, то userId ставлю -1 в качестве заглушки.
     */
    private int mUserId;

    private boolean mFromMe;

    private boolean mRead;

    private String mMessageBody;

    private Date mDate;

    private Attachment[] mAttachments;


    public Message(int messageId, int userId, boolean fromMe, boolean read, String messageBody, Date date, Attachment[] attachments) {
        mMessageId = messageId;
        mUserId = userId;
        mFromMe = fromMe;
        mRead = read;
        mMessageBody = messageBody;
        mDate = date;
        mAttachments = attachments;
    }

    public int getMessageId() {
        return mMessageId;
    }

    public int getUserId() {
        return mUserId;
    }

    public boolean isFromMe() {
        return mFromMe;
    }

    public String getMessageBody() {
        return mMessageBody;
    }

    public boolean isRead() {
        return mRead;
    }

    public Date getDate() {
        return mDate;
    }

    public Attachment[] getAttachments() {
        return mAttachments;
    }

    public boolean hasAttachments(){
        return mAttachments != null;
    }
}