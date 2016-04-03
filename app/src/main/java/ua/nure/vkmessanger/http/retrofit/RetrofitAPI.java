package ua.nure.vkmessanger.http.retrofit;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit API interface.
 */
public interface RetrofitAPI {

    @GET("messages.getDialogs")
    Call<JsonElement> userDialogs(@Query("v") double vkApiVersion,
                                  @Query("count") int count,
                                  @Query("access_token") String accessToken);

    @GET("messages.getHistory")
    Call<JsonElement> dialogHistory(@Query("v") double vkApiVersion,
                                    @Query("user_id") int dialogId,
                                    @Query("offset") int offsetCount,
                                    @Query("count") int count,
                                    @Query("access_token") String accessToken);
}