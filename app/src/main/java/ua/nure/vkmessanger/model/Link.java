package ua.nure.vkmessanger.model;

import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Объект, описывающий ссылку в вложениях сообщений и записей на стене.
 * https://vk.com/dev/attachments_w?f=%D0%A1%D1%81%D1%8B%D0%BB%D0%BA%D0%B0%20(type%20%3D%20link).
 */
public class Link implements Serializable {

    private String mURL;

    private String mTitle;

    private String mDescription;

    @Nullable
    private Photo mPhoto;

//    private String mPreviewPage;

    /*
     * URL страницы для предпросмотра содержимого страницы.
     */
//    private String mPreviewURL;


    public Link(String url, String title, String description, @Nullable Photo photo) {
        mURL = url;
        mTitle = title;
        mDescription = description;
        mPhoto = photo;
    }

    public String getURL() {
        return mURL;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    @Nullable
    public Photo getPhoto() {
        return mPhoto;
    }
}