package ua.nure.vkmessanger.model;

/**
 * Данный объект представляет собой одно сообщение в диалоге с выбранным собеседником.
 */
public class Message {

    private int messageId;
    private boolean fromMe;
    private String messageBody;

    public Message(int messageId, boolean fromMe, String messageBody) {
        this.messageId = messageId;
        this.fromMe = fromMe;
        this.messageBody = messageBody;
    }

    public int getMessageId() {
        return messageId;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
