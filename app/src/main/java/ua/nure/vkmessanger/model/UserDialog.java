package ua.nure.vkmessanger.model;

/**
 * Объект, представляющий диалог в списке диалогов пользователя.
 */
public class UserDialog {

    /**
     * Префикс нужен для передачи аргумента в метод messages.getHistory из VK API.
     */
    private static final int CHAT_PREFIX = 2_000_000_000;

    /**
     * Если данный диалог - это не групповая беседа, то chat_id == 0.
     */
    private int chatId;

    private int userId;

    private String lastMessage;

    /**
     * Объект User или Chat.
     */
    private Object body;

    /**
     * @param chatId id чата, если это групповая беседа. Но если это ЛС, то chatId = 0
     * @param userId id пользователя, с которым идет переписка в ЛС.
     *               В случае, если данный диалог - групповая беседа,
     *               то userId обозначает пользователя, который написал последнее сообщеие в беседе.
     */
    public UserDialog(int chatId, int userId, String lastMessage) {
        this.chatId = chatId;
        this.userId = userId;
        this.lastMessage = lastMessage;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Object getBody(){
        return body;
    }

    public int getUserId() {
        return userId;
    }

    public int getChatId() {
        return chatId;
    }

    /**
     * @return id пользователя, если это ЛС, или id общего чата, если это групповой диалог.
     */
    public int getDialogId() {
        return chatId > 0 ? CHAT_PREFIX + chatId : userId;
    }

    public boolean isChat() {
        return chatId > 0;
    }

    /**
     * @return true, если это ЛС.
     */
    public boolean isSingle() {
        return chatId == 0;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    @Override
    public String toString() {
        return "UserDialog{" +
                "chatId=" + chatId +
                ", userId=" + userId +
                ", lastMessage='" + lastMessage + '\'' +
                '}';
    }
}
