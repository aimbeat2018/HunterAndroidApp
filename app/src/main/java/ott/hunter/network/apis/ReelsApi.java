package ott.hunter.network.apis;

import java.util.List;

import okhttp3.ResponseBody;
import ott.hunter.models.ReelsModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReelsApi {
    @GET("all_reels")
    Call<List<ReelsModel>> getReels(@Header("API-KEY") String apiKey,
                                    @Query("user_id") String userId);

    @FormUrlEncoded
    @POST("reels_like_unlike")
    Call<ResponseBody> likeUnlikeReels(@Header("API-KEY") String apiKey,
                                       @Field("user_id") String userId,
                                       @Field("reels_id") String reelsId);

    @FormUrlEncoded
    @POST("reels_view")
    Call<ResponseBody> videoView(@Header("API-KEY") String apiKey,
                                 @Field("user_id") String userId,
                                 @Field("reels_id") String reelsId);
}
