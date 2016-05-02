package ua.nure.vkmessanger.model;

import java.io.Serializable;

/**
 * Данный объект представляет собой вложение в сообщение (запись на стене и т.д.).
 * https://vk.com/dev/attachments_m
 */
public class Attachment<T> implements Serializable {

    /**
     * Значения констант совпадают с названием соответствующих объектов в Json.
     */
    public static final String TYPE_WALL_POST = "wall";

    public static final String TYPE_PHOTO = "photo";

    public static final String TYPE_LINK = "link";

    public static final String TYPE_AUDIO = "audio";

    public static final String TYPE_VIDEO = "video";

    private String type;

    private T body;

    public Attachment(String type, T body) {
        this.type = type;
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public boolean isWallPost() {
        return type.equals(TYPE_WALL_POST);
    }

    public boolean isPhoto() {
        return type.equals(TYPE_PHOTO);
    }

    public boolean isLink() {
        return type.equals(TYPE_LINK);
    }

    public boolean isAudio() {
        return type.equals(TYPE_AUDIO);
    }

    public boolean isVideo() {
        return type.equals(TYPE_VIDEO);
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "type='" + type + '\'' +
                ", body=" + body +
                '}';
    }
}