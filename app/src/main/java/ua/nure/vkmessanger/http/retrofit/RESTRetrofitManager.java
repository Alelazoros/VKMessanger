package ua.nure.vkmessanger.http.retrofit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.nure.vkmessanger.AccessTokenManager;
import ua.nure.vkmessanger.http.RESTInterface;
import ua.nure.vkmessanger.http.model.CustomResponse;
import ua.nure.vkmessanger.http.model.RequestResult;
import ua.nure.vkmessanger.model.Attachment;
import ua.nure.vkmessanger.model.Group;
import ua.nure.vkmessanger.model.Message;
import ua.nure.vkmessanger.model.Photo;
import ua.nure.vkmessanger.model.UserDialog;
import ua.nure.vkmessanger.model.WallPost;

/**
 * Класс-обертка для выполнения Http-запросов с помощью Retrofit.
 */
public class RESTRetrofitManager implements RESTInterface {

    private static final String RETROFIT_MANAGER_LOG_TAG = "RETROFIT_MANAGER_LOG";

    private static final String VK_API_VERSION = "5.38";

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int WRITE_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .build();

    public static RetrofitAPI getRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.vk.com/method/")
                .client(CLIENT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RetrofitAPI.class);
    }

    private Context mContext;

    public RESTRetrofitManager(Context context) {
        this.mContext = context;
    }

    @Override
    public CustomResponse loadUserDialogs() {
        RetrofitAPI api = getRetrofit();
        Call<JsonElement> retrofitCall = api.userDialogs(VK_API_VERSION,
                USER_DIALOGS_DEFAULT_REQUEST_COUNT,
                AccessTokenManager.getAccessToken(mContext));

        CustomResponse customResponseResult = new CustomResponse();
        try {
            Response<JsonElement> retrofitResponse = retrofitCall.execute();
            if (retrofitResponse.isSuccessful()) {
                Log.d(RETROFIT_MANAGER_LOG_TAG, "request SUCCESSFUL");

                List<UserDialog> dialogs = new ArrayList<>();

                JsonArray jsonItemsArray = retrofitResponse.body().getAsJsonObject()
                        .getAsJsonObject("response")
                        .getAsJsonArray("items");

                for (int i = 0; i < jsonItemsArray.size(); i++) {
                    JsonObject dialogJSON = jsonItemsArray.get(i).getAsJsonObject().getAsJsonObject("message");
                    JsonElement chatIdJsonElement = dialogJSON.get("chat_id");
                    int chatId = chatIdJsonElement == null ? 0 : chatIdJsonElement.getAsInt();

                    dialogs.add(new UserDialog(
                            chatId,
                            dialogJSON.get("user_id").getAsInt(),
                            dialogJSON.get("body").getAsString()
                    ));
                }
                Log.d(RETROFIT_MANAGER_LOG_TAG, String.format("Dialogs count == %d", dialogs.size()));

                customResponseResult.setRequestResult(RequestResult.SUCCESS)
                        .setAnswer(dialogs);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return customResponseResult;
    }


    @Override
    public CustomResponse loadSelectedDialogById(int dialogId, int offsetCount) {
        RetrofitAPI api = getRetrofit();
        Call<JsonElement> retrofitCall = api.dialogHistory(
                VK_API_VERSION,
                dialogId,
                offsetCount,
                DIALOG_MESSAGES_DEFAULT_REQUEST_COUNT,
                AccessTokenManager.getAccessToken(mContext));

        CustomResponse customResponseResult = new CustomResponse();
        try {
            //Sync request.
            Response<JsonElement> retrofitResponse = retrofitCall.execute();

            if (retrofitResponse.isSuccessful()) {
                JsonObject responseBody = retrofitResponse.body().getAsJsonObject();
                try {
                    if (!responseBody.has("response")) {
                        return new CustomResponse();    //ResultResponse.ERROR by default.
                    }
                } catch (NullPointerException ignored) { }    //Не понимаю, как, но иногда вылетает.

                List<Message> messages = new ArrayList<>();

                //Получаю массив сообщений.
                JsonArray jsonItemsArray = responseBody.getAsJsonObject("response").getAsJsonArray("items");
                for (int i = 0; i < jsonItemsArray.size(); i++) {
                    JsonObject messageJSON = jsonItemsArray.get(i).getAsJsonObject();

                    //Читаю данные о сообщении.
                    Message message = parseMessage(messageJSON);
                    messages.add(message);
                }
                Log.d(RETROFIT_MANAGER_LOG_TAG, String.format("Messages count == %d", messages.size()));

                customResponseResult.setRequestResult(RequestResult.SUCCESS)
                        .setAnswer(messages);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customResponseResult;
    }

    private Message parseMessage(JsonObject messageJSON) {

        int messageId = messageJSON.get("id").getAsInt();
        boolean isMessageFromMe = messageJSON.get("out").getAsInt() == MESSAGE_WAS_SEND_FROM_ME;
        boolean isRead = messageJSON.get("read_state").getAsInt() == MESSAGE_WAS_READ;
        String messageBody = messageJSON.get("body").getAsString();

        //Полученную из Json дату домножаю на 1000, так как в Json дата хранится в формате UNIXTIME.
        Date date = new Date(messageJSON.get("date").getAsLong() * 1000);

        //Читаю данные о вложениях сообщений.
        Attachment[] attachments = null;
        if (messageJSON.has("attachments")){
            attachments = parseAttachments(messageJSON.getAsJsonArray("attachments"));
        }
        return new Message(messageId, isMessageFromMe, isRead, messageBody, date, attachments);
    }

    private Attachment[] parseAttachments(JsonArray attachmentsJSONArray) {

        Attachment[] attachments = new Attachment[attachmentsJSONArray.size()];

        for (int j = 0; j < attachmentsJSONArray.size(); j++) {
            JsonObject attachmentItemJson = attachmentsJSONArray.get(j).getAsJsonObject();
            String attachmentItemType = attachmentItemJson.get("type").getAsString();

            switch (attachmentItemType){
                case Attachment.TYPE_WALL_POST:
                    WallPost wallPost = parseWallPost(attachmentItemJson.getAsJsonObject("wall"));
                    attachments[j] = new Attachment(attachmentItemType, wallPost);
                    break;
                case Attachment.TYPE_PHOTO:
                    Photo photo = parsePhoto(attachmentItemJson.getAsJsonObject("photo"));
                    attachments[j] = new Attachment(attachmentItemType, photo);
                    break;
            }
            //TODO: сделать парсинг не только записей на стене.
        }
        return attachments;
    }

    private Photo parsePhoto(JsonObject photoJSONObject) {

        int id = photoJSONObject.get("id").getAsInt();
        int albumId = photoJSONObject.get("album_id").getAsInt();
        int ownerId = photoJSONObject.get("owner_id").getAsInt();
        int userId = photoJSONObject.has("user_id") ? photoJSONObject.get("user_id").getAsInt() : -1;

        String text = photoJSONObject.get("text").getAsString();
        //Полученную из Json дату домножаю на 1000, так как в Json дата хранится в формате UNIXTIME.
        Date date = new Date(photoJSONObject.get("date").getAsLong() * 1000);

        String photo75 = photoJSONObject.has("photo_75") ? photoJSONObject.get("photo_75").getAsString() : null;
        String photo130 = photoJSONObject.has("photo_130") ? photoJSONObject.get("photo_130").getAsString() : null;
        String photo604 = photoJSONObject.has("photo_604") ? photoJSONObject.get("photo_604").getAsString() : null;
        String photo807 = photoJSONObject.has("photo_807") ? photoJSONObject.get("photo_807").getAsString() : null;
        String photo1280 = photoJSONObject.has("photo_1280") ? photoJSONObject.get("photo_1280").getAsString() : null;
        String photo2560 = photoJSONObject.has("photo_2560") ? photoJSONObject.get("photo_2560").getAsString() : null;

        int width = 0;
        int height = 0;
        if (photoJSONObject.has("width")){
            width = photoJSONObject.get("width").getAsInt();
            height = photoJSONObject.get("height").getAsInt();
        }

        return new Photo(
                id, albumId, ownerId, userId,
                text, date,
                photo75, photo130, photo604, photo807, photo1280, photo2560,
                width, height
        );
    }

    private WallPost parseWallPost(JsonObject wallPostJSONObject) {

        int postId = wallPostJSONObject.get("id").getAsInt();
        int authorId = wallPostJSONObject.get("from_id").getAsInt();
        int wallOwnerId = wallPostJSONObject.has("to_id") ?
                wallPostJSONObject.get("to_id").getAsInt() : wallPostJSONObject.get("owner_id").getAsInt();

        //Полученную из Json дату домножаю на 1000, так как в Json дата хранится в формате UNIXTIME.
        Date postCreatedDate = new Date(wallPostJSONObject.get("date").getAsLong() * 1000);
        String postText = wallPostJSONObject.get("text").getAsString();
        String postType = wallPostJSONObject.get("post_type").getAsString();

        int signerId = wallPostJSONObject.has("signer_id") ? wallPostJSONObject.get("signer_id").getAsInt() : -1;

        //Если запись является репостом.
        WallPost[] copyHistory = null;
        if (wallPostJSONObject.has("copy_history")){

            JsonArray copyHistoryArray = wallPostJSONObject.getAsJsonArray("copy_history");
            copyHistory = new WallPost[copyHistoryArray.size()];

            for (int i = 0; i < copyHistoryArray.size(); i++) {
                JsonObject repostedWallPostJSON = copyHistoryArray.get(i).getAsJsonObject();
                copyHistory[i] = parseWallPost(repostedWallPostJSON);
            }
        }

        Attachment[] attachments = null;
        if (wallPostJSONObject.has("attachments")){
            attachments = parseAttachments(wallPostJSONObject.getAsJsonArray("attachments"));
        }

        return new WallPost(
                postId,
                authorId,
                wallOwnerId,
                postCreatedDate,
                postText,
                postType, signerId, copyHistory, attachments);
    }


    @Override
    public CustomResponse sendMessageTo(String message, int peerId) {
        RetrofitAPI api = getRetrofit();
        Call<JsonElement> retrofitCall = api.sendMessage(VK_API_VERSION, peerId, message, AccessTokenManager.getAccessToken(mContext));

        CustomResponse customResponseResult = new CustomResponse();
        try {
            //Sync request.
            Response<JsonElement> retrofitResponse = retrofitCall.execute();
            JsonObject responseObject = retrofitResponse.body().getAsJsonObject();

            if (responseObject.has("response")) {
                int messageId = responseObject.get("response").getAsInt();
                Date date = new Date();
                Attachment[] attachments = null;

                //TODO: 3-й параметр(isRead) false - под вопросом.
                customResponseResult.setRequestResult(RequestResult.SUCCESS)
                        .setAnswer(new Message(messageId, true, false, message, date, attachments));
            }
            else if (responseObject.has("error")) {
                JsonElement errorObject = responseObject.get("error");
                customResponseResult.setAnswer(errorObject.getAsJsonObject().get("error_code").getAsInt());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customResponseResult;
    }


    @Override
    public CustomResponse getGroupsInfoByIds(String[] groupIds) {
        RetrofitAPI api = getRetrofit();
        Call<JsonElement> retrofitCall = api.getGroupsByIds(VK_API_VERSION, generateGroupsIdsStringParamFromArray(groupIds));

        CustomResponse customResponse = new CustomResponse();
        try {
            //Sync request.
            Response<JsonElement> response = retrofitCall.execute();

            if (response.isSuccessful()) {
                JsonObject responseObject = response.body().getAsJsonObject();

                if (responseObject.has("response")) {
                    List<Group> groupsList = new ArrayList<>();

                    JsonArray groupsJSONArray = responseObject.getAsJsonArray("response");
                    for (JsonElement groupElement : groupsJSONArray) {
                        Group group = parseGroup(groupElement.getAsJsonObject());
                        groupsList.add(group);
                    }
                    customResponse.setRequestResult(RequestResult.SUCCESS).setAnswer(groupsList);

                } else if (responseObject.has("error")) {
                    JsonElement errorObject = responseObject.get("error");
                    customResponse.setAnswer(errorObject.getAsJsonObject().get("error_msg").getAsString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customResponse;
    }

    /**
     * @param groupIds массив id или screen_name групп, для которых необходимо получить информацию.
     * @return строка, содержащая все элементы массива, разделенные запятыми.
     *
     * (во входящем массиве обязательно должен быть хотя бы один элемент).
     */
    private String generateGroupsIdsStringParamFromArray(@NonNull String[] groupIds){
        if (groupIds.length == 1){
            return groupIds[0];
        }
        StringBuilder sb = new StringBuilder();
        for (String groupId : groupIds) {
            sb.append(groupId).append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private Group parseGroup(JsonObject groupJSONObject) {
        int id = groupJSONObject.get("id").getAsInt();

        String name = groupJSONObject.get("name").getAsString();
        String screenName = groupJSONObject.get("screen_name").getAsString();
        String type = groupJSONObject.get("type").getAsString();

        String photo50 = groupJSONObject.has("photo_50") ? groupJSONObject.get("photo_50").getAsString() : null;
        String photo100 = groupJSONObject.has("photo_100") ? groupJSONObject.get("photo_100").getAsString() : null;
        String photo200 = groupJSONObject.has("photo_200") ? groupJSONObject.get("photo_200").getAsString() : null;

        return new Group(id, name, screenName, type, photo50, photo100, photo200);
    }
}