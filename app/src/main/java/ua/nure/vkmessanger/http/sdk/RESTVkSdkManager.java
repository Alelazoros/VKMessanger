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
import java.util.List;

import ua.nure.vkmessanger.http.RESTInterface;
import ua.nure.vkmessanger.http.ResponseCallback;
import ua.nure.vkmessanger.model.Message;
import ua.nure.vkmessanger.model.UserDialog;

/**
 * Класс обертка, для Http-запросов.
 */
public class RESTVkSdkManager implements RESTInterface {

    private static final String REST_MANAGER_LOG_TAG = "REST_VK_SDK_MANAGER_LOG";


    @Override
    public void loadUserDialogs(final ResponseCallback<UserDialog> responseCallback) {
        VKRequest currentRequest = VKApi.messages().getDialogs(
                VKParameters.from(VKApiConst.COUNT, USER_DIALOGS_DEFAULT_REQUEST_COUNT));
        currentRequest.attempts = 10;

        currentRequest.executeWithListener(new VKRequest.VKRequestListener() {
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
                                messageJSON.getString("body")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(REST_MANAGER_LOG_TAG, String.format("Dialogs count == %d", dialogs.size()));

                //И передаю результат на callback в активити или фрагмент.
                responseCallback.onResponse(dialogs);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                Log.d(REST_MANAGER_LOG_TAG, "attemptFailed " + request + " " + attemptNumber + " " + totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.d(REST_MANAGER_LOG_TAG, "onError: " + error);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
                Log.d(REST_MANAGER_LOG_TAG, "onProgress " + progressType + " " + bytesLoaded + " " + bytesTotal);
            }
        });
    }

    @Override
    public void loadSelectedDialogById(int dialogId, int offsetCount, final ResponseCallback<Message> responseCallback) {
        VKRequest currentRequest = new VKRequest("messages.getHistory",
                VKParameters.from(VKApiConst.USER_ID, dialogId,
                        VKApiConst.OFFSET, offsetCount,
                        VKApiConst.COUNT, DIALOG_MESSAGES_DEFAULT_REQUEST_COUNT));
        currentRequest.attempts = 10;

        currentRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d(REST_MANAGER_LOG_TAG, "onComplete " + response);

                List<Message> messages = new ArrayList<>();
                try {
                    JSONArray jsonMessagesArray = (JSONArray)response.json.getJSONObject("response").get("items");

                    for (int i = 0; i < jsonMessagesArray.length(); i++) {
                        JSONObject object = jsonMessagesArray.getJSONObject(i);

                        int messageId = object.getInt("id");
                        boolean isMessageFromMe = object.getInt("out") == MESSAGE_WAS_SEND_FROM_ME;
                        boolean isRead = object.getInt("read_state") == MESSAGE_WAS_READ;
                        String messageBody = object.getString("body");

                        messages.add(new Message(messageId, isMessageFromMe, isRead, messageBody));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(REST_MANAGER_LOG_TAG, String.format("Messages loaded count == %d", messages.size()));

                responseCallback.onResponse(messages);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                Log.d(REST_MANAGER_LOG_TAG, "attemptFailed " + request + " " + attemptNumber + " " + totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.d(REST_MANAGER_LOG_TAG, "onError: " + error);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
                Log.d(REST_MANAGER_LOG_TAG, "onProgress " + progressType + " " + bytesLoaded + " " + bytesTotal);
            }
        });
    }
}