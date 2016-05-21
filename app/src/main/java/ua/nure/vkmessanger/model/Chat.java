package ua.nure.vkmessanger.model;

import java.util.List;

/**
 * Created by Nickitee on 15.05.2016.
 */
public class Chat {
    private int chatId;
    private  int chatAdminId;
    private String chatName;
    private List<User> usersInChat;

    public Chat(int chtid, String chtname, List<User> usersinchat, int chatadminid) {
        this.chatId = chtid;
        this.chatName = chtname;
        this.usersInChat = usersinchat;
        this.chatAdminId = chatadminid;
    }
    public int getChatID() { return chatId; }
    public String getChatName() { return chatName; }
    public List<User> getUserInChat() { return usersInChat; }
    public  int getChatAdminId() { return chatAdminId; }

}
