package ott.hunter.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayuMoneyModel {
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("hashtest")
    @Expose
    private String hashtest;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getHashtest() {
        return hashtest;
    }

    public void setHashtest(String hashtest) {
        this.hashtest = hashtest;
    }
}
