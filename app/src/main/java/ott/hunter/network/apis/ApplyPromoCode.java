package ott.hunter.network.apis;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ApplyPromoCode {
    @GET("check_promocode")
    Call<ResponseBody> check_promocode(@Header("API-KEY") String apiKey,
                                             @Field("user_id") String userId,
                                             @Field("promocode") String promocode);
}
