package ua.nure.vkmessanger.http.sdk;

import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ua.nure.vkmessanger.http.RESTInterface;
import ua.nure.vkmessanger.http.model.CustomResponse;
import ua.nure.vkmessanger.http.model.RequestResult;
import ua.nure.vkmessanger.model.Attachment;
import ua.nure.vkmessanger.model.Message;
import ua.nure.vkmessanger.model.UserDialog;

/**
 * Класс обертка, для Http-запросов.
 */
public class RESTVkSdkManager implements RESTInterface {

    private static final String REST_MANAGER_LOG_TAG = "REST_VK_SDK_MANAGER_LOG";


    @Override
    public CustomResponse loadUserDialogs() {
        VKRequest currentRequest = VKApi.messages().getDialogs(
                VKParameters.from(VKApiConst.COUNT, USER_DIALOGS_DEFAULT_REQUEST_COUNT));
        currentRequest.attempts = 10;

        final CustomResponse customResponseResult = new CustomResponse();
        //sync request.
        currentRequest.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                List<UserDialog> dialogs = new ArrayList<>();
                try {
                    JSONArray responseMessagesArrayJSON = response.json.getJSONObject("response").getJSONArray("items");

                    for (int i = 0; i < responseMessagesArrayJSON.length(); i++) {
                        JSONObject messageJSON = responseMessagesArrayJSON.getJSONObject(i).getJSONObject("message");
                        dialogs.add(new UserDialog(
                                messageJSON.optInt("chat_id"),
                                messageJSON.getInt("user_id"),
                                messageJSON.getString("body"),
                                messageJSON.optBoolean("out")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(REST_MANAGER_LOG_TAG, String.format("Dialogs count == %d", dialogs.size()));

                customResponseResult.setRequestResult(RequestResult.SUCCESS)
                        .setAnswer(dialogs);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                Log.d(REST_MANAGER_LOG_TAG, "attemptFailed " + request + " " + attemptNumber + " " + totalAttempts);
                customResponseResult.setRequestResult(RequestResult.ERROR);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.d(REST_MANAGER_LOG_TAG, "onError: " + error);
                customResponseResult.setRequestResult(RequestResult.ERROR);
            }
        });

        return customResponseResult;
    }

    @Override
    public CustomResponse loadSelectedDialogById(int dialogId, int offsetCount) {
        VKRequest currentRequest = new VKRequest("messages.getHistory",
                VKParameters.from(VKApiConst.USER_ID, dialogId,
                        VKApiConst.OFFSET, offsetCount,
                        VKApiConst.COUNT, DIALOG_MESSAGES_DEFAULT_REQUEST_COUNT));
        currentRequest.attempts = 10;

        final CustomResponse customResponseResult = new CustomResponse();
        //Sync request.
        currentRequest.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                Log.d(REST_MANAGER_LOG_TAG, "onComplete " + response);

                List<Message> messages = new ArrayList<>();
                try {
                    JSONArray jsonMessagesArray = (JSONArray) response.json.getJSONObject("response").get("items");

                    for (int i = 0; i < jsonMessagesArray.length(); i++) {
                        JSONObject object = jsonMessagesArray.getJSONObject(i);

                        int messageId = object.getInt("id");
                        int userId = object.getInt("user_id");
                        boolean isMessageFromMe = object.getInt("out") == MESSAGE_WAS_SEND_FROM_ME;
                        boolean isRead = object.getInt("read_state") == MESSAGE_WAS_READ;
                        String messageBody = object.getString("body");
                        Date date = new Date(object.getLong("date") * 1000);
                        Attachment[] attachments = null;

                        //TODO: сделать парсинг вложений (attachments).
                        messages.add(new Message(messageId, userId, isMessageFromMe, isRead, messageBody, date, attachments));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(REST_MANAGER_LOG_TAG, String.format("Messages loaded count == %d", messages.size()));

                customResponseResult.setRequestResult(RequestResult.SUCCESS)
                        .setAnswer(messages);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                Log.d(REST_MANAGER_LOG_TAG, "attemptFailed " + request + " " + attemptNumber + " " + totalAttempts);
                customResponseResult.setRequestResult(RequestResult.ERROR);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.d(REST_MANAGER_LOG_TAG, "onError: " + error);
                customResponseResult.setRequestResult(RequestResult.ERROR);
            }
        });

        return customResponseResult;
    }

    @Override
    public CustomResponse loadFriends() {
        return new CustomResponse();
    }

    public CustomResponse sendMessageTo(String message, int peerId) {
        return new CustomResponse();
    }

    @Override
    public CustomResponse getGroupsInfoByIds(String[] groupIds) {
        return new CustomResponse();
    }

    @Override
    public CustomResponse setOnline(boolean voip) {
        return new CustomResponse();
    }

    @Override
    public CustomResponse setOffline() {
        return new CustomResponse();
    }

    @Override
    public CustomResponse markMessagesAsRead(int peerId) {
        return new CustomResponse();
    }

}