package ua.nure.vkmessanger.http.retrofit;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Antony on 03.04.2016.
 */
public interface RetrofitAPI {

    @GET("messages.getDialogs")
    Call<JsonElement> userDialogs(@Query("count") int count, @Query("access_token") String accessToken);
}