package ott.hunter.models;

import java.util.List;

import ott.hunter.models.single_details.VideoQuality;

public class EpiModel {
    String seson, epi, streamURL, serverType, imageUrl,episodeId,episodeStatus;
    List<SubtitleModel> subtitleList;
    List<VideoQuality> videoQuality;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getStreamURL() {
        return streamURL;
    }

    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }

    public String getSeson() {
        return seson;
    }

    public void setSeson(String seson) {
        this.seson = seson;
    }

    public String getEpi() {
        return epi;
    }

    public void setEpi(String epi) {
        this.epi = epi;
    }

    public List<SubtitleModel> getSubtitleList() {
        return subtitleList;
    }

    public void setSubtitleList(List<SubtitleModel> subtitleList) {
        this.subtitleList = subtitleList;
    }

    public List<VideoQuality> getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(List<VideoQuality> videoQuality) {
        this.videoQuality = videoQuality;
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public String getEpisodeStatus() {
        return episodeStatus;
    }

    public void setEpisodeStatus(String episodeStatus) {
        this.episodeStatus = episodeStatus;
    }
}
