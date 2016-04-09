package ua.nure.vkmessanger.http;

import ua.nure.vkmessanger.http.model.CustomResponse;
import ua.nure.vkmessanger.model.Message;
import ua.nure.vkmessanger.model.UserDialog;

/**
 * Интерфейс, который должен быть реализован классом, который работает с Http-запросами.
 */
public interface RESTInterface {

    /**
     * Количество диалогов, которые загружаются по умолчанию в MainActivity.
     */
    int USER_DIALOGS_DEFAULT_REQUEST_COUNT = 100;

    /**
     * Колочество сообщений, которые загружаются или подгружаются в выбранный пользователем диалог.
     */
    int DIALOG_MESSAGES_DEFAULT_REQUEST_COUNT = 50;

    /**
     * Сообщение было отправлено текущий пользователем приложения.
     */
    int MESSAGE_WAS_SEND_FROM_ME = 1;

    /**
     * Сообщений прочитано.
     */
    int MESSAGE_WAS_READ = 1;


    void loadUserDialogs(ResponseCallback<UserDialog> responseCallback);

    /**
     * @param offsetCount используется для подгрузки сообщений из истории диалога.
     */
    CustomResponse loadSelectedDialogById(int dialogId, int offsetCount);
}