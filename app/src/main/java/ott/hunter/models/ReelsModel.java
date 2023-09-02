package ott.hunter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReelsModel {

    @SerializedName("reels_id")
    @Expose
    private String reelsId;
    @SerializedName("video_id")
    @Expose
    private String videoId;
    @SerializedName("video_link")
    @Expose
    private String videoLink;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("video_description")
    @Expose
    private String videoDescription;
    @SerializedName("total_view")
    @Expose
    private String totalView;
    @SerializedName("total_like_count")
    @Expose
    private String totalLikeCount;
    @SerializedName("like_reels")
    @Expose
    private String likeReels;

    public String getReelsId() {
        return reelsId;
    }

    public void setReelsId(String reelsId) {
        this.reelsId = reelsId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getTotalView() {
        return totalView;
    }

    public void setTotalView(String totalView) {
        this.totalView = totalView;
    }

    public String getTotalLikeCount() {
        return totalLikeCount;
    }

    public void setTotalLikeCount(String totalLikeCount) {
        this.totalLikeCount = totalLikeCount;
    }

    public String getLikeReels() {
        return likeReels;
    }

    public void setLikeReels(String likeReels) {
        this.likeReels = likeReels;
    }

}