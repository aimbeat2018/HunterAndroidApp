package ott.hunter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponse {


        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("otp")
        @Expose
        private Integer otp;
        @SerializedName("data")
        @Expose
        private String data;
        private final static long serialVersionUID = -4072315560707240524L;

        public String getStatus() {
        return status;
    }

        public void setStatus(String status) {
        this.status = status;
    }

        public Integer getOtp() {
        return otp;
    }

        public void setOtp(Integer otp) {
        this.otp = otp;
    }

        public String getData() {
        return data;
    }

        public void setData(String data) {
        this.data = data;
    }

    }
