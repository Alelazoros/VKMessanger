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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
import ua.nure.vkmessanger.model.Audio;
import ua.nure.vkmessanger.model.Chat;
import ua.nure.vkmessanger.model.Document;
import ua.nure.vkmessanger.model.Group;
import ua.nure.vkmessanger.model.Link;
import ua.nure.vkmessanger.model.Message;
import ua.nure.vkmessanger.model.Photo;
import ua.nure.vkmessanger.model.User;
import ua.nure.vkmessanger.model.UserDialog;
import ua.nure.vkmessanger.model.Video;
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
    public CustomResponse loadUsers(List<UserDialog> input) {

        CustomResponse customResponseResult = new CustomResponse();
        RetrofitAPI api = getRetrofit();
        List<User> users = new ArrayList<>();

        StringBuilder idsBuilder = new StringBuilder();
        for (int i = 0; i < input.size(); i++) {
            UserDialog current = input.get(i);
            if (current.isSingle()) {
                idsBuilder.append(idsBuilder.length() > 0 ? "," : "").append(current.getUserId());
            }
        }
        Call<JsonElement> retrofitCall = api.getUsers(VK_API_VERSION,
                idsBuilder.toString(),
                "photo_100,photo_200,photo_max_orig,bdate",
                "Nom",
                AccessTokenManager.getAccessToken(mContext));
        try {
            Response<JsonElement> retrofitResponse = retrofitCall.execute();
            if (retrofitResponse.isSuccessful()) {

                JsonArray jsonItemsArray = retrofitResponse.body().getAsJsonObject().getAsJsonArray("response");

                for (int i = 0; i < jsonItemsArray.size(); i++) {
                    JsonObject currentElement = jsonItemsArray.get(i).getAsJsonObject();
                    users.add(parseUser(currentElement));
                }
                customResponseResult.setRequestResult(RequestResult.SUCCESS).setAnswer(users);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customResponseResult;
    }

    @Override
    public CustomResponse loadChats(List<UserDialog> input) {

        CustomResponse customResponseResult = new CustomResponse();
        RetrofitAPI api = getRetrofit();
        List<Chat> chats = new ArrayList<>();

        StringBuilder idsBuilder = new StringBuilder();
        for (int i = 0; i < input.size(); i++) {
            UserDialog current = input.get(i);
            if (current.isChat()) {
                idsBuilder.append(idsBuilder.length() > 0 ? "," : "").append(current.getChatId());
            }
        }

        Call<JsonElement> retrofitCall = api.getChats(
                VK_API_VERSION, idsBuilder.toString(), AccessTokenManager.getAccessToken(mContext));

        try {
            Response<JsonElement> retrofitResponse = retrofitCall.execute();
            if (retrofitResponse.isSuccessful()) {

                JsonObject responseBody = retrofitResponse.body().getAsJsonObject();
                if (!responseBody.has("response")) {
                    return customResponseResult;
                }

                //Временные контейнеры для хранения id пользователей-собеседников чата.
                Map<Integer, List<Integer>> chatsUsersTempMap = new HashMap<>();
                Set<Integer> chatUserIdsSet = new TreeSet<>();

                JsonArray chatsResponseJsonArray = responseBody.getAsJsonArray("response");
                for (int i = 0; i < chatsResponseJsonArray.size(); i++) {
                    JsonObject currentChatJsonObject = chatsResponseJsonArray.get(i).getAsJsonObject();
                    if (!currentChatJsonObject.has("users")) {
                        continue;
                    }
                    chats.add(parseChat(currentChatJsonObject, chatsUsersTempMap, chatUserIdsSet, i));
                }

                //Подгружаю всех пользователей-собеседников для всех чатов одним запросом.
                CustomResponse chatUsersResponse = loadChatsUsers(api, chatUserIdsSet, chatsUsersTempMap);
                if (chatUsersResponse.getRequestResult() == RequestResult.SUCCESS) {

                    //Получил список всех пользователей для каждого чата.
                    Map<Integer, List<User>> chatUsers = chatUsersResponse.getTypedAnswer();
                    for (int i = 0; i < chats.size(); i++) {
                        chats.get(i).setUsersList(chatUsers.get(i));
                    }
                    customResponseResult.setRequestResult(RequestResult.SUCCESS).setAnswer(chats);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customResponseResult;
    }

    private CustomResponse loadChatsUsers(RetrofitAPI api, Set<Integer> usersSet, Map<Integer, List<Integer>> chatsUsersMap) {
        CustomResponse response = new CustomResponse();

        StringBuilder idsBuilder = new StringBuilder();
        for (Integer userId : usersSet) {
            idsBuilder.append(idsBuilder.length() > 0 ? "," : "").append(userId);
        }

        Call<JsonElement> retrofitCall = api.getUsers(VK_API_VERSION,
                idsBuilder.toString(),
                "photo_100,photo_200,photo_max_orig,bdate",
                "Nom",
                AccessTokenManager.getAccessToken(mContext));
        try {
            Response<JsonElement> retrofitResponse = retrofitCall.execute();
            if (retrofitResponse.isSuccessful()) {

                JsonArray jsonItemsArray = retrofitResponse.body().getAsJsonObject().getAsJsonArray("response");

                Map<Integer, List<User>> usersMap = new HashMap<>();
                for (int i = 0; i < jsonItemsArray.size(); i++) {
                    JsonObject currentElement = jsonItemsArray.get(i).getAsJsonObject();
                    User user = parseUser(currentElement);

                    for (Map.Entry<Integer, List<Integer>> entry : chatsUsersMap.entrySet()){

                        if (entry.getValue().contains(user.getId())){
                            List<User> list = usersMap.get(entry.getKey());
                            if (list == null){
                                list = new ArrayList<>();
                            }
                            list.add(user);
                            usersMap.put(entry.getKey(), list);
                        }
                    }
                }
                response.setRequestResult(RequestResult.SUCCESS).setAnswer(usersMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
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
                    boolean isLastMessageFromMe = dialogJSON.get("out").getAsInt() == MESSAGE_WAS_SEND_FROM_ME;

                    dialogs.add(new UserDialog(
                            chatId,
                            dialogJSON.get("user_id").getAsInt(),
                            dialogJSON.get("body").getAsString(),
                            isLastMessageFromMe
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
                //TODO: передавать реальный userId, а не заглушку -1.
                customResponseResult.setRequestResult(RequestResult.SUCCESS)
                        .setAnswer(new Message(messageId, -1, true, false, message, date, attachments));
            } else if (responseObject.has("error")) {
                JsonElement errorObject = responseObject.get("error");
                customResponseResult.setAnswer(errorObject.getAsJsonObject().get("error_code").getAsInt());
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
                } catch (NullPointerException ignored) {
                }    //Не понимаю, как, но иногда вылетает.

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


    //----------Parse specified JSON objects--------//


    private Message parseMessage(JsonObject messageJSON) {

        int messageId = messageJSON.get("id").getAsInt();
        int userId = messageJSON.get("user_id").getAsInt();
        boolean isMessageFromMe = messageJSON.get("out").getAsInt() == MESSAGE_WAS_SEND_FROM_ME;
        boolean isRead = messageJSON.get("read_state").getAsInt() == MESSAGE_WAS_READ;
        String messageBody = messageJSON.get("body").getAsString();

        //Полученную из Json дату домножаю на 1000, так как в Json дата хранится в формате UNIXTIME.
        Date date = new Date(messageJSON.get("date").getAsLong() * 1000);

        //Читаю данные о вложениях сообщений.
        Attachment[] attachments = null;
        if (messageJSON.has("attachments")) {
            attachments = parseAttachments(messageJSON.getAsJsonArray("attachments"));
        }
        return new Message(messageId, userId, isMessageFromMe, isRead, messageBody, date, attachments);
    }

    private Attachment[] parseAttachments(JsonArray attachmentsJSONArray) {

        Attachment[] attachments = new Attachment[attachmentsJSONArray.size()];

        for (int j = 0; j < attachmentsJSONArray.size(); j++) {
            JsonObject attachmentItemJson = attachmentsJSONArray.get(j).getAsJsonObject();
            String attachmentItemType = attachmentItemJson.get("type").getAsString();

            switch (attachmentItemType) {
                case Attachment.TYPE_PHOTO:
                    Photo photo = parsePhoto(attachmentItemJson.getAsJsonObject(Attachment.TYPE_PHOTO));
                    attachments[j] = new Attachment(attachmentItemType, photo);
                    break;
                case Attachment.TYPE_VIDEO:
                    Video video = parseVideo(attachmentItemJson.getAsJsonObject(Attachment.TYPE_VIDEO));
                    attachments[j] = new Attachment(attachmentItemType, video);
                    break;
                case Attachment.TYPE_AUDIO:
                    Audio audio = parseAudio(attachmentItemJson.getAsJsonObject(Attachment.TYPE_AUDIO));
                    attachments[j] = new Attachment(attachmentItemType, audio);
                    break;
                case Attachment.TYPE_DOC:
                    Document doc = parseDocument(attachmentItemJson.getAsJsonObject(Attachment.TYPE_DOC));
                    attachments[j] = new Attachment(attachmentItemType, doc);
                    break;
                case Attachment.TYPE_LINK:
                    Link link = parseLink(attachmentItemJson.getAsJsonObject(Attachment.TYPE_LINK));
                    attachments[j] = new Attachment(attachmentItemType, link);
                    break;
                case Attachment.TYPE_WALL_POST:
                    WallPost wallPost = parseWallPost(attachmentItemJson.getAsJsonObject(Attachment.TYPE_WALL_POST));
                    attachments[j] = new Attachment(attachmentItemType, wallPost);
                    break;
            }
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
        if (photoJSONObject.has("width")) {
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

    private Video parseVideo(JsonObject videoJSONObject) {

        int id = videoJSONObject.get("id").getAsInt();
        int ownerId = videoJSONObject.get("owner_id").getAsInt();
        String title = videoJSONObject.get("title").getAsString();
        String description = videoJSONObject.get("description").getAsString();
        int duration = videoJSONObject.get("duration").getAsInt();

        String photo130 = videoJSONObject.has("photo_130") ? videoJSONObject.get("photo_130").getAsString() : null;
        String photo320 = videoJSONObject.has("photo_320") ? videoJSONObject.get("photo_320").getAsString() : null;
        String photo640 = videoJSONObject.has("photo_640") ? videoJSONObject.get("photo_640").getAsString() : null;
        String photo800 = videoJSONObject.has("photo_800") ? videoJSONObject.get("photo_800").getAsString() : null;

        Date dateCreated = new Date(videoJSONObject.get("date").getAsLong() * 1000);
        Date dateAdding = videoJSONObject.has("adding_date") ?
                new Date(videoJSONObject.get("adding_date").getAsLong() * 1000) : dateCreated;

        int viewsCount = videoJSONObject.get("views").getAsInt();
        int commentsCount = videoJSONObject.get("comments").getAsInt();
        String playerUrl = videoJSONObject.has("player") ? videoJSONObject.get("player").getAsString() : null;

        String accessKey = videoJSONObject.get("access_key").getAsString();

        boolean processing = videoJSONObject.has("processing") && videoJSONObject.get("processing").getAsBoolean();
        boolean liveSteam = videoJSONObject.has("live") && videoJSONObject.get("live").getAsBoolean();

        return new Video(id, ownerId, title, description, duration,
                photo130, photo320, photo640, photo800, dateCreated, dateAdding,
                viewsCount, commentsCount, playerUrl, accessKey, processing, liveSteam);
    }

    private Audio parseAudio(JsonObject audioJSONObject) {

        int id = audioJSONObject.get("id").getAsInt();
        int ownerId = audioJSONObject.get("owner_id").getAsInt();
        String artist = audioJSONObject.get("artist").getAsString();
        String title = audioJSONObject.get("title").getAsString();
        int duration = audioJSONObject.get("duration").getAsInt();
        String mp3Url = audioJSONObject.get("url").getAsString();
        Date dateAdded = new Date(audioJSONObject.get("date").getAsLong() * 1000);

        //Необязательный параметры.
        int lyricsId = audioJSONObject.has("lyrics_id") ? audioJSONObject.get("lyrics_id").getAsInt() : -1;
        int albumId = audioJSONObject.has("album_id") ? audioJSONObject.get("album_id").getAsInt() : -1;
        int genreId = audioJSONObject.has("genre_id") ? audioJSONObject.get("genre_id").getAsInt() : -1;
        boolean noSearch = audioJSONObject.has("no_search") && audioJSONObject.get("no_search").getAsBoolean();

        return new Audio(id, ownerId, artist, title, duration, mp3Url, dateAdded,
                lyricsId, albumId, genreId, noSearch);
    }

    private Document parseDocument(JsonObject docJSONObject) {

        int id = docJSONObject.get("id").getAsInt();
        int ownerId = docJSONObject.get("owner_id").getAsInt();

        String title = docJSONObject.get("title").getAsString();
        int sizeInBytes = docJSONObject.get("size").getAsInt();
        String extension = docJSONObject.get("ext").getAsString();
        String url = docJSONObject.get("url").getAsString();

        String photo100 = docJSONObject.has("photo_100") ? docJSONObject.get("photo_100").getAsString() : null;
        String photo130 = docJSONObject.has("photo_130") ? docJSONObject.get("photo_130").getAsString() : null;

        Date dateAdded = new Date(docJSONObject.get("date").getAsLong() * 1000);
        int docType = docJSONObject.get("type").getAsInt();

        return new Document(id, ownerId, title, sizeInBytes, extension, url, photo100, photo130, dateAdded, docType);
    }

    private Link parseLink(JsonObject linkJSONObject) {
        String url = linkJSONObject.get("url").getAsString();
        String title = linkJSONObject.get("title").getAsString();
        String description = linkJSONObject.get("description").getAsString();

        Photo photo = null;
        if (linkJSONObject.has("photo")) {
            photo = parsePhoto(linkJSONObject.getAsJsonObject("photo"));
        }

        return new Link(url, title, description, photo);
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
        if (wallPostJSONObject.has("copy_history")) {

            JsonArray copyHistoryArray = wallPostJSONObject.getAsJsonArray("copy_history");
            copyHistory = new WallPost[copyHistoryArray.size()];

            for (int i = 0; i < copyHistoryArray.size(); i++) {
                JsonObject repostedWallPostJSON = copyHistoryArray.get(i).getAsJsonObject();
                copyHistory[i] = parseWallPost(repostedWallPostJSON);
            }
        }

        Attachment[] attachments = null;
        if (wallPostJSONObject.has("attachments")) {
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

    private User parseUser(JsonObject currentElement) {
        return new User(currentElement.get("id").getAsInt(),
                currentElement.has("first_name") ? currentElement.get("first_name").getAsString() : null,
                currentElement.has("last_name") ? currentElement.get("last_name").getAsString() : null,
                currentElement.has("bdate") ? currentElement.get("bdate").getAsString() : null,
                currentElement.has("photo_100") ? currentElement.get("photo_100").getAsString() : null,
                currentElement.has("photo_200") ? currentElement.get("photo_200").getAsString() : null,
                currentElement.has("photo_max_orig") ? currentElement.get("photo_max_orig").getAsString() : null,
                currentElement.has("online") && currentElement.get("online").getAsBoolean());
    }

    private Chat parseChat(JsonObject currentChatJsonObject, Map<Integer, List<Integer>> chatsUsersIdsMap,
                           Set<Integer> chatUserIdsSet, int position) {

        List<Integer> chatUsersIds = new ArrayList<>();
        JsonArray usersJsonArray = currentChatJsonObject.getAsJsonArray("users");
        //Получаю список id всех пользователей-собеседников текущего чата.
        for (JsonElement userJsonElement : usersJsonArray) {
            chatUsersIds.add(userJsonElement.getAsInt());
        }

        //Сохраняю список id пользователей для чата в Map, чтобы потом сделать один общий запрос.
        chatsUsersIdsMap.put(position, chatUsersIds);
        chatUserIdsSet.addAll(chatUsersIds);

        return new Chat(currentChatJsonObject.get("id").getAsInt(),
                currentChatJsonObject.get("title").getAsString(),
                currentChatJsonObject.get("admin_id").getAsInt(),
                currentChatJsonObject.has("photo_100") ? currentChatJsonObject.get("photo_100").getAsString() : null,
                currentChatJsonObject.has("photo_200") ? currentChatJsonObject.get("photo_200").getAsString() : null);
    }

    //---------------Groups------------//

    /**
     * Метод в данный момент используется для получения информации для заголовка (header)
     * в WallPostActivity.
     *
     * @param groupIds id групп, для которых нужно получить информацию.
     *                 Передаю не int[], а String[], т.к. можно передавать
     *                 не только целочисленные id, а вместо этого передать
     *                 короткий адрес сообщества (url), например 'tproger', вместо его id.
     */
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
     * <p>
     * (во входящем массиве обязательно должен быть хотя бы один элемент).
     */
    private String generateGroupsIdsStringParamFromArray(@NonNull String[] groupIds) {
        if (groupIds.length == 1) {
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