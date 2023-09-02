package ott.hunter.models;

public class DownloadModel {
    public String video_id;
    public String file_name;
    public String progress;
    public String progress_complete;
    public String image;

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getProgress_complete() {
        return progress_complete;
    }

    public void setProgress_complete(String progress_complete) {
        this.progress_complete = progress_complete;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
