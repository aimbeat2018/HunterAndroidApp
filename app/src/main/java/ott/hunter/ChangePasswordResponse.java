package ott.hunter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangePasswordResponse {


        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("data")
        @Expose
        private String data;
        private final static long serialVersionUID = -4418721553015987626L;

        public String getStatus() {
        return status;
    }

        public void setStatus(String status) {
        this.status = status;
    }

        public String getData() {
        return data;
    }

        public void setData(String data) {
        this.data = data;
    }

    }
