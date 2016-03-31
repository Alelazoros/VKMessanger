package ua.nure.vkmessanger.http;

import ua.nure.vkmessanger.model.Message;
import ua.nure.vkmessanger.model.UserDialog;

/**
 * Интерфейс, который должен быть реализован классом, который работает с Http-запросами.
 */
public interface RESTInterface {

    void loadUserDialogs(ResponseCallback<UserDialog> responseCallback);

    void loadSelectedDialog(int dialogId, ResponseCallback<Message> responseCallback);
}