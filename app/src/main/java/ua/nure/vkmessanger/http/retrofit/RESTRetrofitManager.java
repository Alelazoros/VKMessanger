package ua.nure.vkmessanger.http.retrofit;

import android.content.Context;
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
                    JsonObject dialogJSON = jsonItemsArray.get(i).getAsJsonObject().get("message").getAsJsonObject();
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
                }catch (NullPointerException ignored) { }    //Не понимаю, как, но иногда вылетает.

                List<Message> messages = new ArrayList<>();

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
        Date date = new Date(messageJSON.get("date").getAsLong());

        //Читаю данные о вложениях сообщений.
        Attachment[] attachments = null;
        if (messageJSON.has("attachments")){
            JsonArray attachmentsJSONArray = messageJSON.get("attachments").getAsJsonArray();
            attachments = parseAttachments(attachmentsJSONArray);
        }
        return new Message(messageId, isMessageFromMe, isRead, messageBody, date, attachments);
    }

    private Attachment[] parseAttachments(JsonArray attachmentsJSONArray) {

        Attachment[] attachments = new Attachment[attachmentsJSONArray.size()];

        for (int j = 0; j < attachmentsJSONArray.size(); j++) {
            JsonObject attachmentItemJson = attachmentsJSONArray.get(j).getAsJsonObject();

            String attachmentItemType = attachmentItemJson.get("type").getAsString();
            if (attachmentItemType.equals(Attachment.TYPE_WALL_POST)) {
                JsonObject wallPostJSONObject = attachmentItemJson.get("wall").getAsJsonObject();

                WallPost wallPost = parseWallPost(wallPostJSONObject);
                attachments[j] = new Attachment(attachmentItemType, wallPost);
            }
            else if (attachmentItemType.equals(Attachment.TYPE_PHOTO)){
                JsonObject photoJSONObject = attachmentItemJson.get("photo").getAsJsonObject();

                Photo photo = parsePhoto(photoJSONObject);
                attachments[j] = new Attachment(attachmentItemType, photo);
            }
            //TODO: сделать парсинг не только записей на стене.
        }
        return attachments;
    }

    private Photo parsePhoto(JsonObject photoJSONObject) {

        int id = photoJSONObject.get("id").getAsInt();
        int albumId = photoJSONObject.get("album_id").getAsInt();
        int ownerId = photoJSONObject.get("owner_id").getAsInt();
        int userId = photoJSONObject.get("user_id").getAsInt();
        String text = photoJSONObject.get("text").getAsString();
        Date date = new Date(photoJSONObject.get("date").getAsLong());

        String photo75 = photoJSONObject.get("photo_75").getAsString();
        String photo130 = photoJSONObject.get("photo_130").getAsString();
        String photo604 = photoJSONObject.get("photo_604").getAsString();
        String photo807 = photoJSONObject.get("photo_807").getAsString();
        String photo1280 = photoJSONObject.get("photo_1280").getAsString();
        String photo2560 = photoJSONObject.get("photo_2560").getAsString();

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
        int wallOwnerId = wallPostJSONObject.get("to_id").getAsInt();
        Date postCreatedDate = new Date(wallPostJSONObject.get("date").getAsLong());
        String postText = wallPostJSONObject.get("text").getAsString();
        String postType = wallPostJSONObject.get("post_type").getAsString();


        Attachment[] attachments = null;
        if (wallPostJSONObject.has("attachments")){
            attachments = parseAttachments(wallPostJSONObject.get("attachments").getAsJsonArray());
        }

        return new WallPost(
                postId,
                authorId,
                wallOwnerId,
                postCreatedDate,
                postText,
                postType, attachments);
    }

    @Override
    public CustomResponse sendMessageTo(String message, int peerId) {
        RetrofitAPI api = getRetrofit();
        Call<JsonElement> retrofitCall = api.sendMessage(VK_API_VERSION, peerId, message, AccessTokenManager.getAccessToken(mContext));

        CustomResponse customResponseResult = new CustomResponse();
        try {
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
            else {
                if (responseObject.has("error")) {
                    JsonElement errorObject = responseObject.get("error");
                    customResponseResult.setAnswer(errorObject.getAsJsonObject().get("error_code").getAsInt());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customResponseResult;
    }
}