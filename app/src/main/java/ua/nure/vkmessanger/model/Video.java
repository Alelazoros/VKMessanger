package ua.nure.vkmessanger.model;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

/**
 * Объект 'video', который является вложением в сообщение или запись на стене.
 * https://vk.com/dev/video_object
 */
public class Video implements Serializable {

    private int id;

    /**
     * Идентификатор владельца видеозаписи.
     * Может быть отрицательное число.
     */
    private int mOwnerId;

    /**
     * Название видеозаписи.
     */
    private String mTitle;

    /**
     * Текст описания видеозаписи.
     */
    private String mDescription;

    /**
     * Длительность ролика в секундах.
     */
    private int mDuration;

    /**
     * URL изображения-обложки ролика с размерами соответственно:
     * 130x98px, 320x240px, 640x480px (если размер есть), 800x450px (если размер есть).
     */
    private String mPhoto130;

    private String mPhoto320;
    @Nullable
    private String mPhoto640;
    @Nullable
    private String mPhoto800;

    /**
     * Дата создания видеозаписи в формате unixtime
     */
    private Date mCreatedDate;

    /**
     * Дата добавления видеозаписи пользователем или группой в формате unixtime.
     */
    private Date mAddingDate;

    /**
     * Количество просмотров видеозаписи.
     */
    private int mViewsCount;

    /**
     * Количество комментариев к видеозаписи
     */
    private int mCommentsCount;

    /**
     * Адрес страницы с плеером, который можно использовать для воспроизведения ролика в браузере.
     * Поддерживается flash и html5, плеер всегда масштабируется по размеру окна.
     */
    @Nullable
    private String mPlayerUrl;

    /**
     * Ключ доступа к объекту.
     * https://vk.com/dev/access_key
     */
    private String mAccessKey;

    /**
     * Поле возвращается в том случае, если видеоролик находится в процессе обработки, всегда содержит true.
     */
    private boolean mIsProcessing;

    /**
     * Поле возвращается в том случае, если видеозапись является прямой трансляцией, всегда содержит 1.
     * Обратите внимание, в этом случае в поле duration содержится значение 0.
     */
    private boolean mIsLiveStream;

    public Video(int id, int ownerId, String title, String description, int duration,
                 String photo130, String photo320, @Nullable String photo640, @Nullable String photo800,
                 Date createdDate, Date addingDate, int viewsCount, int commentsCount,
                 @Nullable String playerUrl, String accessKey, boolean isProcessing, boolean isLiveStream) {

        this.id = id;
        mOwnerId = ownerId;
        mTitle = title;
        mDescription = description;
        mDuration = duration;
        mPhoto130 = photo130;
        mPhoto320 = photo320;
        mPhoto640 = photo640;
        mPhoto800 = photo800;
        mCreatedDate = createdDate;
        mAddingDate = addingDate;
        mViewsCount = viewsCount;
        mCommentsCount = commentsCount;
        mPlayerUrl = playerUrl;
        mAccessKey = accessKey;
        mIsProcessing = isProcessing;
        mIsLiveStream = isLiveStream;
    }

    public int getOwnerId() {
        return Math.abs(mOwnerId);
    }

    public String getPhoto320() {
        return mPhoto320;
    }
}