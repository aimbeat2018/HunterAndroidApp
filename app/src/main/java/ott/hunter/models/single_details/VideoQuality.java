package ott.hunter.models.single_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoQuality {
    @SerializedName("quality_id")
    @Expose
    private String qualityId;
    @SerializedName("episodes_id")
    @Expose
    private String episodesId;
    @SerializedName("qualilty")
    @Expose
    private String qualilty;
    @SerializedName("url")
    @Expose
    private String url;

    public String getQualityId() {
        return qualityId;
    }

    public void setQualityId(String qualityId) {
        this.qualityId = qualityId;
    }

    public String getEpisodesId() {
        return episodesId;
    }

    public void setEpisodesId(String episodesId) {
        this.episodesId = episodesId;
    }

    public String getQualilty() {
        return qualilty;
    }

    public void setQualilty(String qualilty) {
        this.qualilty = qualilty;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
