package ott.hunter.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("mobile_no")
    @Expose
    private String mobile_no;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("country_code")
    @Expose
    private String country_code;
    //    @SerializedName("gender")
//    @Expose
//    private String gender;
    @SerializedName("join_date")
    @Expose
    private String joinDate;
    //    @SerializedName("last_login")
//    @Expose
//    private String lastLogin;
    @SerializedName("data")
    @Expose
    private String data;
    //    @SerializedName("image_url")
//    @Expose
//    private String imageUrl;
    @SerializedName("device_no")
    @Expose
    private String device_no;
    @SerializedName("logout_status")
    @Expose
    private String logout_status;
    @SerializedName("profile_status")
    @Expose
    private String profile_status;
    @SerializedName("password_available")
    @Expose
    private boolean isPasswordAvailable;

    public User() {
    }

    public String getLogout_status() {
        return logout_status;
    }

    public void setLogout_status(String logout_status) {
        this.logout_status = logout_status;
    }

    public String getProfile_status() {
        return profile_status;
    }

    public void setProfile_status(String profile_status) {
        this.profile_status = profile_status;
    }

    public String getDevice_no() {
        return device_no;
    }

    public void setDevice_no(String device_no) {
        this.device_no = device_no;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

//    public String getLastLogin() {
//        return lastLogin;
//    }
//
//    public void setLastLogin(String lastLogin) {
//        this.lastLogin = lastLogin;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }

    public boolean isPasswordAvailable() {
        return isPasswordAvailable;
    }

    public void setPasswordAvailable(boolean passwordAvailable) {
        isPasswordAvailable = passwordAvailable;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    @Override
    public String toString() {
        return "User{" +
                "status='" + status + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
//                ", gender='" + gender + '\'' +
                ", joinDate='" + joinDate + '\'' +
//                ", lastLogin='" + lastLogin + '\'' +
                ", data='" + data + '\'' +
//                ", image_url='" + imageUrl + '\'' +
                ", password_available='" + isPasswordAvailable + '\'' +
                '}';
    }
}
