package ott.hunter.network.apis;

import okhttp3.ResponseBody;
import ott.hunter.network.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserDataApi {
    @GET("user_details_by_user_id")
    Call<User> getUserData(@Header("API-KEY") String apiKey,
                           @Query("id") String userId);

    @FormUrlEncoded
    @POST("image_url")
    Call<ResponseBody> getImage(@Header("API-KEY") String apiKey,
                                @Field("id") String userId);
}
