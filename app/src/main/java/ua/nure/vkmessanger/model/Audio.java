package ua.nure.vkmessanger.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Объект 'audio', который является вложением в сообщение или запись на стене.
 * https://vk.com/dev/audio_object
 */
public class Audio implements Serializable {

    private int id;

    /**
     * Идентификатор владельца аудиозаписи.
     */
    private int mOwnerId;

    /**
     * Имя исполнителя.
     */
    private String mArtist;

    /**
     * Название композиции.
     */
    private String mTitle;

    /**
     * Длительность композиции в секундах.
     */
    private int mDuration;

    /**
     * Ссылка на .mp3 файл.
     */
    private String mUrl;

    /**
     * Дата добавления аудиозаписи.
     */
    private Date mDate;

    /**
     * Идентификатор текста аудиозаписи (если присвоен).
     */
    private int mLyricsId;

    /**
     * Идентификатор альбома, в котором находится аудиозапись (если присвоен).
     */
    private int mAlbumId;

    /**
     * Идентификатор жанра из списка аудио жанров (если присвоен).
     * Список жанров находится здесь: https://vk.com/dev/audio_genres.
     */
    private int mGenreId;

    /**
     * true - если включена опция "Не выводить при поиске". Если опция отключена, поле не возвращается.
     */
    private boolean mNoSearch;

    public Audio(int id, int ownerId, String artist, String title, int duration, String url, Date date,
                 int lyricsId, int albumId, int genreId, boolean noSearch) {
        this.id = id;
        mOwnerId = ownerId;
        mArtist = artist;
        mTitle = title;
        mDuration = duration;
        mUrl = url;
        mDate = date;
        mLyricsId = lyricsId;
        mAlbumId = albumId;
        mGenreId = genreId;
        mNoSearch = noSearch;
    }
}
