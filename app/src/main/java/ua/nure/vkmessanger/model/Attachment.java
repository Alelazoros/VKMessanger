package ua.nure.vkmessanger.model;

import java.io.Serializable;

/**
 * Данный объект представляет собой вложение в сообщение (запись на стене и т.д.).
 */
public class Attachment<T> implements Serializable {

    /**
     * Значения констант совпадают с названием соответствующих объектов в Json.
     */
    public static final String TYPE_WALL_POST = "wall";

    public static final String TYPE_PHOTO = "photo";

    private String type;

    private T body;

    public Attachment(String type, T body) {
        this.type = type;
        this.body = body;
    }

    public String getType() {
        return type;
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

    @Override
    public String toString() {
        return "Attachment{" +
                "type='" + type + '\'' +
                ", body=" + body +
                '}';
    }
}