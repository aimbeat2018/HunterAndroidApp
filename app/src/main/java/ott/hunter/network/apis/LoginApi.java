package ott.hunter.network.apis;

import okhttp3.ResponseBody;
import ott.hunter.network.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LoginApi {

    @FormUrlEncoded
    @POST("login")
    Call<User> postLoginStatus(@Header("API-KEY") String apiKey,
                               @Field("email") String email,
                               @Field("password") String password,
                               @Field("device_no") String device_no,
                               @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("logout")
    Call<User> postLogoutStatus(@Header("API-KEY") String apiKey,
                                @Field("user_id") String user_id);



    @FormUrlEncoded
    @POST("updatedevice")
    Call<User> postChangeDeviceStatus(@Header("API-KEY") String apiKey,
                                      @Field("mobile_no") String mobile_no,
                                      @Field("device_no") String device_no,
                                      @Field("device_token") String device_token);



    @FormUrlEncoded
    @POST("login_otp")
    Call<ResponseBody> login_otp(@Header("API-KEY") String apiKey,
                                 @Field("mobile_no") String mobile_no,
                                 @Field("email") String email);

    @FormUrlEncoded
    @POST("mobile_check")
    Call<ResponseBody> mobile_check(@Header("API-KEY") String apiKey,
                                 @Field("mobile") String mobile_no);


    @FormUrlEncoded
    @POST("verify_otp")
    Call<ResponseBody> verify_otp(@Header("API-KEY") String apiKey,
                                 @Field("mobile_no") String mobile_no);
}
