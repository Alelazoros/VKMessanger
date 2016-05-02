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
    public static final String TYPE_PHOTO = "photo";

    public static final String TYPE_VIDEO = "video";

    public static final String TYPE_AUDIO = "audio";

    public static final String TYPE_DOC = "doc";

    public static final String TYPE_LINK = "link";

    public static final String TYPE_WALL_POST = "wall";

    private String type;

    private T body;

    public Attachment(String type, T body) {
        this.type = type;
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public String getType() {
        return type;
    }

    public boolean isPhoto() {
        return type.equals(TYPE_PHOTO);
    }

    public boolean isVideo() {
        return type.equals(TYPE_VIDEO);
    }

    public boolean isAudio() {
        return type.equals(TYPE_AUDIO);
    }

    public boolean isDocument(){
        return type.equals(TYPE_DOC);
    }

    public boolean isLink() {
        return type.equals(TYPE_LINK);
    }

    public boolean isWallPost() {
        return type.equals(TYPE_WALL_POST);
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "type='" + type + '\'' +
                ", body=" + body +
                '}';
    }
}