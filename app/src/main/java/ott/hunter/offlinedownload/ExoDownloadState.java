package ott.hunter.offlinedownload;

public enum ExoDownloadState {

    DOWNLOAD_START("Download"),
    DOWNLOAD_PAUSE("Pause"),
    DOWNLOAD_RESUME("Resume"),
    DOWNLOAD_COMPLETED("Downloaded"),
    DOWNLOAD_RETRY("Retry"),
    DOWNLOAD_QUEUE("In Queue");



    private String value;

    public String getValue() {
        return value;
    }
    private ExoDownloadState(String value) {
        this.value = value;
    }

}
