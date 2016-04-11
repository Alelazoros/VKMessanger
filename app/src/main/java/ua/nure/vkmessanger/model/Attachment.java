package ua.nure.vkmessanger.model;

/**
 * Данный объект представляет собой вложение в сообщение (запись на стене и т.д.).
 */
public class Attachment<T> {

    public static final String TYPE_WALL_POST = "wall";

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
}