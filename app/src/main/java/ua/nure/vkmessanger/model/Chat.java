package ua.nure.vkmessanger.model;

import java.util.List;

/**
 * Created by Nickitee on 15.05.2016.
 */
public class Chat {

    private int chatId;
    private String chatName;
    private List<User> usersInChat;
    private int chatAdminId;

    public Chat(int id, String name, List<User> users, int chatAdminId) {
        this.chatId = id;
        this.chatName = name;
        this.usersInChat = users;
        this.chatAdminId = chatAdminId;
    }

    public int getChatId() {
        return chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public List<User> getUsersInChat() {
        return usersInChat;
    }

    public int getChatAdminId() {
        return chatAdminId;
    }
}