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
     * @param groupsIds id или screen_name одной или нескольких(до 500 шт.) групп,
     *                  разделенных запятыми между собой.
     * Полная документация: https://vk.com/dev/groups.getById
     */
    @GET("groups.getById")
    Call<JsonElement> getGroupsByIds(@Query("v") String vkApiVersion,
                                     @Query("group_ids") String groupsIds);

}