package ua.nure.vkmessanger.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Объект, описывающий фотографию.
 * https://vk.com/dev/photo
 */
public class Photo implements Serializable {

    private int id;

    /**
     * Альбом, в котором находится фото. Отрицательное число.
     */
    private int mAlbumId;

    /**
     * Владелец фото. Отрицательное число.
     */
    private int mOwnerId;

    /**
     * Пользователь, которые загрузил фото (если фотография размещена в сообществе,
     * а если не в сообществе, то поле отсутствует в ответе API).
     * Для фотографий, размещенных от имени сообщества, user_id=100.
     */
    private int mUserId;

    /**
     * Текст описания фотографии.
     */
    private String mText;

    private Date mDate;

    /**
     * URL на фотографии в различных размерах.
     * В ответе не обязательно должны быть все размеры!
     */
    private String mPhoto75;
    private String mPhoto130;
    private String mPhoto604;
    private String mPhoto807;
    private String mPhoto1280;
    private String mPhoto2560;

    /**
     * Ширина и высота оригинальной фотографии.
     * Но могут быть недоступны для фотографий, загруженных на сайт до 2012 года
     */
    private int mWidth;

    private int mHeight;

    public Photo(int id, int albumId, int ownerId, int userId, String text, Date date,
                 String photo75, String photo130, String photo604, String photo807, String photo1280, String photo2560,
                 int width, int height) {
        this.id = id;
        mAlbumId = albumId;
        mOwnerId = ownerId;
        mUserId = userId;
        mText = text;
        mDate = date;
        mPhoto75 = photo75;
        mPhoto130 = photo130;
        mPhoto604 = photo604;
        mPhoto807 = photo807;
        mPhoto1280 = photo1280;
        mPhoto2560 = photo2560;
        mWidth = width;
        mHeight = height;
    }

    public int getId() {
        return id;
    }

    public int getAlbumId() {
        return mAlbumId;
    }

    public int getOwnerId() {
        return mOwnerId;
    }

    public int getUserId() {
        return mUserId;
    }

    public String getText() {
        return mText;
    }

    public Date getDate() {
        return mDate;
    }

    public String getPhotoURL(){
        //TODO: придумать логику, когда какой размер изображения использовать.
        return mPhoto604;
    }
}
