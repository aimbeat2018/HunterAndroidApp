package ott.hunter.models.single_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ott.hunter.models.SubtitleModel;

import java.util.List;

public class Episode {
    @SerializedName("episodes_id")
    @Expose
    private String episodesId;
    @SerializedName("episodes_name")
    @Expose
    private String episodesName;
    @SerializedName("stream_key")
    @Expose
    private String streamKey;
    @SerializedName("file_type")
    @Expose
    private String fileType;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("file_url")
    @Expose
    private String fileUrl;
    @SerializedName("episode_type")
    @Expose
    private String episodeType;
    @SerializedName("subtitle")
    @Expose
    private List<SubtitleModel> subtitle = null;
    @SerializedName("video_quality")
    @Expose
    private List<VideoQuality> videoQuality = null;

    public String getEpisodesId() {
        return episodesId;
    }

    public void setEpisodesId(String episodesId) {
        this.episodesId = episodesId;
    }

    public String getEpisodesName() {
        return episodesName;
    }

    public void setEpisodesName(String episodesName) {
        this.episodesName = episodesName;
    }

    public String getStreamKey() {
        return streamKey;
    }

    public void setStreamKey(String streamKey) {
        this.streamKey = streamKey;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public List<SubtitleModel> getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(List<SubtitleModel> subtitle) {
        this.subtitle = subtitle;
    }

    public List<VideoQuality> getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(List<VideoQuality> videoQuality) {
        this.videoQuality = videoQuality;
    }

    public String getEpisodeType() {
        return episodeType;
    }

    public void setEpisodeType(String episodeType) {
        this.episodeType = episodeType;
    }
}
