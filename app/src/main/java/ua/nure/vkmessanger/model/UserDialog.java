package ua.nure.vkmessanger.model;

/**
 * Объект, представляющий диалог в списке диалогов пользователя.
 */
public class UserDialog {

    /**
     * Префикс нужен для передачи аргумента в метод messages.getHistory из VK API.
     */
    public static final int CHAT_PREFIX = 2_000_000_000;

    /**
     * Если данный диалог - это не групповая беседа, то chat_id == 0.
     */
    private int chatId;

    private int userId;

    private String lastMessage;

    public UserDialog(int chatId, int userId, String lastMessage) {
        this.chatId = chatId;
        this.userId = userId;
        this.lastMessage = lastMessage;
    }

    public int getChatId() {
        return chatId;
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
