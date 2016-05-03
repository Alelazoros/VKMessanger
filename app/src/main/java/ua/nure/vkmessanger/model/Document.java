package ua.nure.vkmessanger.model;

import android.support.annotation.Nullable;

import java.util.Date;

/**
 *  Объект 'doc', описывающий документ в VK API.
 *  https://vk.com/dev/doc
 */
public class Document {

    private int id;

    /**
     * Идентификатор пользователя, загрузившего документ.
     */
    private int mOwnerId;

    /**
     * Название документа.
     */
    private String mTitle;

    /**
     * Размер документа в байтах.
     */
    private int mSize;

    /**
     * Расширение документа.
     */
    private String mExtension;

    /**
     * Адрес документа, по которому его можно загрузить
     */
    private String mUrl;

    /**
     * Адрес изображения с размером 100x75px (если файл графический).
     */
    @Nullable
    private String mPhoto100;

    /**
     * Адрес изображения с размером 130x100px (если файл графический).
     */
    @Nullable
    private String mPhoto130;

    /**
     * Дата добавления в формате unixtime.
     */
    private Date mDate;

    /**
     * Тип документа.
     */
    private int mDocumentType;

    /*
       Также приходит и поле access_key, но в данный момент мне оно не необходимо.
     */


    public Document(int id, int ownerId, String title, int size, String extension, String url,
                    @Nullable String photo100, @Nullable String photo130, Date date, int documentType) {
        this.id = id;
        mOwnerId = ownerId;
        mTitle = title;
        mSize = size;
        mExtension = extension;
        mUrl = url;
        mPhoto100 = photo100;
        mPhoto130 = photo130;
        mDate = date;
        mDocumentType = documentType;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getSize() {
        return mSize;
    }
}