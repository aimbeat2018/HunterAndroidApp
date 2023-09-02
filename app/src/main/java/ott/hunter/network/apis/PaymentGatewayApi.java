package ott.hunter.network.apis;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentGatewayApi {
//    @FormUrlEncoded
    @GET("payumoney")
    Call<ResponseBody> payumoney(@Header("API-KEY") String apiKey,
                                   @Query("key") String key);

    @FormUrlEncoded
    @POST("paymentgatway_status")
    Call<ResponseBody> paymentgatway_status(@Header("API-KEY") String apiKey,
                                            @Field("userId") String userId);

}
