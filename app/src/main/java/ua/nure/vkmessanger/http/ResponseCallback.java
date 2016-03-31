package ua.nure.vkmessanger.http;

import java.util.List;

/**
 * Callback Http-запроса.
 */
public interface ResponseCallback<T> {

    void onResponse(List<T> data);
}