package ua.nure.vkmessanger.model;

import java.util.Date;

/**
 * Данный объект представляет собой одно сообщение в диалоге с выбранным собеседником.
 */
public class Message {

    private int mMessageId;

    private boolean mFromMe;

    private boolean mRead;

    private String mMessageBody;

    private Date mDate;

    private Attachment[] mAttachments;

    //TODO: удалить этот конструктор.
    public Message(int messageId, boolean fromMe, boolean read, String messageBody) {
        mMessageId = messageId;
        mFromMe = fromMe;
        mRead = read;
        mMessageBody = messageBody;
    }

    public Message(int messageId, boolean fromMe, boolean read, String messageBody, Date date, Attachment[] attachments) {
        this.mMessageId = messageId;
        this.mFromMe = fromMe;
        this.mRead = read;
        this.mMessageBody = messageBody;
        this.mDate = date;
        this.mAttachments = attachments;
    }

    public int getMessageId() {
        return mMessageId;
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
}