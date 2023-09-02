package ott.hunter.offlinedownload;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "download_table")
public class DownloadInfo {
    @PrimaryKey
    @ColumnInfo(name = "download_id")
    @NonNull
    private long downloadId;

    @ColumnInfo(name = "file_name")
    private String fileName;

    @ColumnInfo(name = "percentage")
    private int percentage;

    @ColumnInfo(name = "secretKey",typeAffinity = ColumnInfo.BLOB)
    private byte[] secretKey;

    public DownloadInfo(long downloadId, String fileName, int percentage, byte[] secretKey) {
        this.downloadId = downloadId;
        this.fileName = fileName;
        this.percentage = percentage;
        this.secretKey = secretKey;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public byte[] getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(byte[] secretKey) {
        this.secretKey = secretKey;
    }
}
