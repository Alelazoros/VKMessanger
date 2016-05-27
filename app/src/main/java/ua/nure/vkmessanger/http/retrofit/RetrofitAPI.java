package ua.nure.vkmessanger.http.retrofit;

import com.google.gson.JsonElement;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Retrofit API interface.
 */
public interface RetrofitAPI {

    @GET("messages.getDialogs")
    Call<JsonElement> userDialogs(@Query("v") String vkApiVersion,
                                  @Query("count") int count,
                                  @Query("access_token") String accessToken);

    @GET("messages.getHistory")
    Call<JsonElement> dialogHistory(@Query("v") String vkApiVersion,
                                    @Query("user_id") int dialogId,
                                    @Query("offset") int offsetCount,
                                    @Query("count") int count,
                                    @Query("access_token") String accessToken);

    @GET("messages.send")
    Call<JsonElement> sendMessage(@Query("v") String vkApiVersion,
                                  @Query("peer_id") int peerId,
                                  @Query("message") String message,
                                  @Query("access_token") String accessToken);

    /**
     * @param userIds  список id пользователей, разделенных через запятую.
     * @param fields   список дополнительных полей профилей, которые необходимо вернуть.
     * @param nameCase падеж для склонения имени и фамилии пользователя.
     *                 Возможные значения:
     *                 именительный – nom, родительный – gen, дательный – dat,
     *                 винительный – acc, творительный – ins, предложный – abl.
     *                 По умолчанию nom.
     */
    @GET("users.get")
    Call<JsonElement> getUsers(@Query("v") String vkApiVersion,
                               @Query("user_ids") String userIds,
                               @Query("fields") String fields,
                               @Query("name_case") String nameCase,
                               @Query("access_token") String accessToken);

    /**
     * @param chatIds список id чатов, разделенных через запятую.
     */
    @GET("messages.getChat")
    Call<JsonElement> getChats(@Query("v") String vkApiVersion,
                               @Query("chat_ids") String chatIds,
                               @Query("access_token") String accessToken);

    /**
     * @param groupsIds id или screen_name одной или нескольких(до 500 шт.) групп,
     *                  разделенных запятыми между собой.
     *                  Полная документация: https://vk.com/dev/groups.getById
     */
    @GET("groups.getById")
    Call<JsonElement> getGroupsByIds(@Query("v") String vkApiVersion,
                                     @Query("group_ids") String groupsIds);

}