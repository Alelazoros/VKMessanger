package ua.nure.vkmessanger.model;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.Date;

import ua.nure.vkmessanger.R;

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

    /**
     * @return строка, которая отображает размер файла, только не просто в виде количества байт
     * а с окончанием 'kб' или 'Мб'.
     */
    public String getStringViewOfSize(Context context) {
        if (mSize < 1000) {
            return String.valueOf(mSize);
        } else if (mSize < 1000_000) {
            return String.valueOf(mSize / 1000) + " " + context.getString(R.string.size_kilo_byte);
        } else if (mSize < 1000_000_000) {
            return String.valueOf(mSize / 1000_000) + " " + context.getString(R.string.size_mega_byte);
        } else {
            return String.valueOf(mSize / 1000_000_000) + " " + context.getString(R.string.size_giga_byte);
        }
    }
}