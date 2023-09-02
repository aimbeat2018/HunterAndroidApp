package ott.hunter.network.apis;


import ott.hunter.ChangePasswordResponse;
import ott.hunter.ForgotPasswordResponse;
import ott.hunter.more.FeedbackformdResponse;
import ott.hunter.network.model.PasswordReset;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PassResetApi {
    @FormUrlEncoded
    @POST("password_reset")
    Call<PasswordReset> resetPassword(@Header("API-KEY") String apiKey,
                                      @Field("email") String email);



    @FormUrlEncoded
    @POST("forgot_password_otp")
    Call<ForgotPasswordResponse> forgot_password_otp(@Header("API-KEY") String apiKey,
                                                     @Field("mobile") String email);





    @FormUrlEncoded
    @POST("order_entry")
    Call<OrderEntryResponse> order_entry(@Header("API-KEY") String apiKey,
                                                     @Field("user_id") String user_id,
                                                     @Field("plan_id") String plan_id,
                                                     @Field("amount") String amount,
                                                     @Field("order_id") String order_id);





    @FormUrlEncoded
    @POST("order_status_details")
    Call<OrderstatusResponse> order_status_details(@Header("API-KEY") String apiKey,
                                         @Field("user_id") String user_id);





    @FormUrlEncoded
    @POST("password_change")
    Call<ChangePasswordResponse> password_change(@Header("API-KEY") String apiKey,
                                                 @Field("mobile") String mobile,
                                                 @Field("password") String password);



    @FormUrlEncoded
    @POST("add_feedback")
    Call<FeedbackformdResponse>add_feedback(@Header("API-KEY") String apiKey,
                                            @Field("user_id") String userid,
                                            @Field("fullname") String fullname,
                                            @Field("email") String email,
                                            @Field("number") String number,
                                            @Field("issue") String issue,
                                            @Field("message") String message);



}
