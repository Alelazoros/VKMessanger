package ua.nure.vkmessanger.model;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by Nickitee on 15.05.2016.
 */
public class Chat {

    private int chatId;

    private String chatName;

    private List<User> usersList;

    private int chatAdminId;

    @Nullable
    private String chatAvatar100Url;

    @Nullable
    private String chatAvatar200Url;


    /**
     * Поле usersList данного объекта Chat будет инициализированно не в констркуторе,
     * а cеттером setUsersList(List<User> list);
     * <p/>
     * После загрузки всех чатов будет сделан один общий запрос, из которого
     * будет получен список всех пользователей для всех чатов. А затем из этого
     * списка будет заполняться usersList для каждого диалога.
     */
    public Chat(int id, String title, int adminId, @Nullable String avatar100, @Nullable String avatar200) {
        chatId = id;
        chatName = title;
        chatAdminId = adminId;
        chatAvatar100Url = avatar100;
        chatAvatar200Url = avatar200;
    }

    public int getChatId() {
        return chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public List<User> getUsersList() {
        return usersList;
    }

    public int getChatAdminId() {
        return chatAdminId;
    }

    @Nullable
    public String getChatAvatar100Url() {
        return chatAvatar100Url;
    }

    @Nullable
    public String getChatAvatar200Url() {
        return chatAvatar200Url;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }
}