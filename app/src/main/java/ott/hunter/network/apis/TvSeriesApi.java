package ott.hunter.network.apis;

import ott.hunter.models.home_content.GoldVideo;
import ott.hunter.models.home_content.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface TvSeriesApi {

    @GET("tvseries")
    Call<List<Video>> getTvSeries(@Header("API-KEY") String apiKey,
                                  @Query("page") int page, @Query("user_id") String user_id);

    @GET("gold_tvseries")
    Call<List<GoldVideo>> gold_tvseries(@Header("API-KEY") String apiKey,
                                        @Query("page") int page, @Query("user_id") String user_id);


}
