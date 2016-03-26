package ua.nure.vkmessanger.model;

/**
 * Объект, представляющий диалог в списке диалогов пользователя.
 */
public class UserDialog {

    private int userId;
    private String lastMessage;

    public UserDialog(int userId, String lastMessage) {
        this.userId = userId;
        this.lastMessage = lastMessage;
    }

    public int getUserId() {
        return userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    @Override
    public String toString() {
        return "UserDialog{" +
                "userId=" + userId +
                ", lastMessage='" + lastMessage + '\'' +
                '}';
    }
}
